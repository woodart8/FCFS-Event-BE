package com.woodart8.fcfs.coupon.kafka;

import com.woodart8.fcfs.coupon.dto.request.CouponIssueRequest;
import com.woodart8.fcfs.coupon.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CouponKafkaConsumer {

    private final CouponIssueService couponIssueService;

    @KafkaListener(
            topics = "coupon-request",
            groupId = "coupon-issue-group",
            containerFactory = "couponKafkaListenerContainerFactory"
    )
    public void consume(CouponIssueRequest request) {

        log.info(
                "쿠폰 발급 요청 수신. requestId={}, eventId={}, userId={}",
                request.requestId(),
                request.eventId(),
                request.userId()
        );

        couponIssueService.issue(request)
                .block();
    }

}
