package com.woodart8.fcfs.coupon.kafka;

import com.woodart8.fcfs.coupon.dto.request.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponKafkaProducer {

    private static final String TOPIC = "coupon-request";

    private final KafkaTemplate<String, CouponIssueRequest> kafkaTemplate;

    public Mono<Void> send(CouponIssueRequest request) {

        log.info("Kafka 전송 시작. requestId={}, eventId={}, userId={}",
                request.requestId(),
                request.eventId(),
                request.userId());

        return Mono.fromFuture(
                        kafkaTemplate.send(
                                TOPIC,
                                request.requestId(),
                                request
                        )
                )
                .doOnSuccess(result ->
                        log.info(
                                "쿠폰 요청 Kafka 발행 성공. requestId={}, eventId={}",
                                request.requestId(),
                                request.eventId()
                        )
                )
                .doOnError(e ->
                        log.error(
                                "쿠폰 요청 Kafka 발행 실패. requestId={}, eventId={}",
                                request.requestId(),
                                request.eventId(),
                                e
                        )
                )
                .then();
    }
}
