package com.woodart8.fcfs.coupon.repository;

import com.woodart8.fcfs.coupon.entity.Coupon;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CouponRepository extends ReactiveMongoRepository<Coupon, String> {
    Mono<Boolean> existsByEventIdAndCode(Long EventId, String code);

    Mono<Boolean> existsByRequestId(String requestId);
}
