package com.woodart8.fcfs.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodart8.fcfs.coupon.dto.request.CouponIssueRequest;
import com.woodart8.fcfs.coupon.dto.request.CouponRequest;
import com.woodart8.fcfs.coupon.dto.response.CouponReqResponse;
import com.woodart8.fcfs.coupon.redis.CouponRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisScript<Long> couponReserveScript;

    public Mono<CouponReqResponse> requestCoupon(
            Long eventId,
            Long userId,
            CouponRequest request
    ) {

        String requestId = UUID.randomUUID().toString();

        CouponIssueRequest issueRequest =
                new CouponIssueRequest(
                        requestId,
                        eventId,
                        userId,
                        request.description(),
                        request.duration()
                );

        String value;

        try {
            value = objectMapper.writeValueAsString(issueRequest);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }

        return reactiveRedisTemplate.opsForValue()
                .set(
                        CouponRedisKey.request(requestId),
                        value,
                        Duration.ofHours(1)
                )
                .then(
                        checkAndReserve(
                                eventId,
                                userId,
                                requestId
                        )
                )
                .flatMap(result -> {

                    if (result == -2L) {
                        return Mono.error(
                                new IllegalStateException(
                                        "이벤트 쿠폰 수량 정보가 없습니다."
                                )
                        );
                    }

                    if (result == -1L) {
                        return Mono.just(
                                CouponReqResponse.duplicate(
                                        requestId
                                )
                        );
                    }

                    if (result == 0L) {
                        return Mono.just(
                                CouponReqResponse.fail(
                                        requestId
                                )
                        );
                    }

                    return Mono.just(
                            CouponReqResponse.success(
                                    requestId
                            )
                    );
                });
    }

    private Mono<Long> checkAndReserve(
            Long eventId,
            Long userId,
            String requestId
    ) {

        return reactiveRedisTemplate.execute(
                couponReserveScript,
                List.of(
                        CouponRedisKey.users(eventId),
                        CouponRedisKey.issued(eventId),
                        CouponRedisKey.max(eventId),
                        CouponRedisKey.queue(eventId)
                ),
                userId.toString(),
                requestId
        ).next();
    }
}