package com.woodart8.fcfs.controller;

import com.woodart8.fcfs.dto.request.CouponRequestDto;
import com.woodart8.fcfs.dto.response.CouponResponseDto;
import com.woodart8.fcfs.service.CouponService;
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
    public Mono<ResponseEntity<CouponResponseDto>> postCoupon(
            @RequestParam("eventId") Long eventId,
            @RequestBody CouponRequestDto couponRequestDto
    ) {
        return couponService.uploadCoupon(eventId, couponRequestDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

}
