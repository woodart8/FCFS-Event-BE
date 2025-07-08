package com.woodart8.fcfs.repository;

import com.woodart8.fcfs.entity.Coupon;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CouponRepository extends ReactiveMongoRepository<Coupon, Long> {
    Mono<Boolean> existsByCode(String code);
}
