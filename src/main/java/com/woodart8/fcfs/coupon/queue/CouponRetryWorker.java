package com.woodart8.fcfs.coupon.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodart8.fcfs.coupon.dto.request.CouponIssueRequest;
import com.woodart8.fcfs.coupon.kafka.CouponKafkaProducer;
import com.woodart8.fcfs.coupon.redis.CouponRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponRetryWorker {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final ObjectMapper objectMapper;
    private final CouponKafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 1000)
    public void retryProcessing() {

        reactiveRedisTemplate.opsForSet()
                .members(CouponRedisKey.events())
                .flatMap(eventId ->
                        retryEvent(
                                Long.parseLong(eventId)
                        )
                )
                .subscribe(
                        null,
                        e -> log.error(
                                "쿠폰 재처리 실패",
                                e
                        )
                );
    }

    public Mono<Void> retryEvent(Long eventId) {

        String processingKey =
                CouponRedisKey.processing(eventId);

        double threshold =
                System.currentTimeMillis() / 1000.0 - 10;

        return reactiveRedisTemplate.opsForZSet()
                .rangeByScore(
                        processingKey,
                        Range.closed(
                                0.0,
                                threshold
                        )
                )
                .flatMap(requestId ->
                        retryRequest(
                                eventId,
                                requestId
                        )
                )
                .then();
    }

    private Mono<Void> retryRequest(
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
                                        "쿠폰 요청 정보가 없습니다."
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