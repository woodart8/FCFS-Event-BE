package com.woodart8.fcfs.coupon.controller;

import com.woodart8.fcfs.coupon.dto.request.CouponRequest;
import com.woodart8.fcfs.coupon.dto.response.CouponReqResponse;
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
    public Mono<ResponseEntity<CouponReqResponse>> postCoupon(
            @RequestParam("eventId") Long eventId,
            @RequestParam("userId") Long userId,
            @RequestBody CouponRequest couponRequest
    ) {
        // userId는 JWT에서 추출해야하나 편의상 파라미터로 넘기게 구현했음.
        return couponService.requestCoupon(
                        eventId,
                        userId,
                        couponRequest
                )
                .map(ResponseEntity::ok);
    }

}
