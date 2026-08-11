package com.woodart8.fcfs.coupon.controller;

import com.woodart8.fcfs.coupon.dto.request.CouponRequest;
import com.woodart8.fcfs.coupon.dto.response.CouponResponse;
import com.woodart8.fcfs.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 쿠폰 발급
    @PostMapping
    public Mono<ResponseEntity<CouponResponse>> postCoupon(
            @RequestParam("eventId") Long eventId,
            @RequestBody CouponRequest couponRequest
    ) {
        return couponService.uploadCoupon(eventId, couponRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

}
