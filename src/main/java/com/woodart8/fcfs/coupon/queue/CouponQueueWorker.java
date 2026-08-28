package com.woodart8.fcfs.coupon.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodart8.fcfs.coupon.dto.request.CouponIssueRequest;
import com.woodart8.fcfs.coupon.kafka.CouponKafkaProducer;
import com.woodart8.fcfs.coupon.redis.CouponRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponQueueWorker {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisScript<String> moveToProcessingScript;
    private final CouponKafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 100)
    public void processQueue() {

        reactiveRedisTemplate.opsForSet()
                .members(CouponRedisKey.events())
                .flatMap(eventId ->
                        processEvent(
                                Long.parseLong(eventId)
                        )
                )
                .subscribe(
                        null,
                        e -> log.error(
                                "쿠폰 대기열 처리 실패",
                                e
                        )
                );
    }

    public Mono<Void> processEvent(Long eventId) {

        return moveToProcessing(eventId)
                .flatMap(requestId ->
                        processRequest(
                                eventId,
                                requestId
                        )
                )
                .then();
    }

    private Mono<String> moveToProcessing(
            Long eventId
    ) {

        return reactiveRedisTemplate.execute(
                moveToProcessingScript,
                List.of(
                        CouponRedisKey.queue(eventId),
                        CouponRedisKey.processing(eventId)
                )
        ).next();
    }

    private Mono<Void> processRequest(
            Long eventId,
            String requestId
    ) {

        return reactiveRedisTemplate.opsForValue()
                .get(
                        CouponRedisKey.request(requestId)
                )
                .switchIfEmpty(
                        Mono.error(
                                new IllegalStateException(
                                        "쿠폰 요청 정보가 없습니다. requestId="
                                                + requestId
                                )
                        )
                )
                .flatMap(this::deserialize)
                .flatMap(request ->
                        kafkaProducer
                                .send(request)
                                .then(
                                        removeProcessing(
                                                eventId,
                                                requestId
                                        )
                                )
                );
    }

    private Mono<CouponIssueRequest> deserialize(
            String value
    ) {

        try {
            return Mono.just(
                    objectMapper.readValue(
                            value,
                            CouponIssueRequest.class
                    )
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Mono<Void> removeProcessing(
            Long eventId,
            String requestId
    ) {

        return reactiveRedisTemplate.opsForZSet()
                .remove(
                        CouponRedisKey.processing(eventId),
                        requestId
                )
                .then();
    }
}
