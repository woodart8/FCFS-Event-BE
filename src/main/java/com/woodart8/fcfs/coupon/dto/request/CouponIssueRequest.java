package com.woodart8.fcfs.coupon.dto.request;

public record CouponIssueRequest(
        String requestId,
        Long eventId,
        Long userId,
        String description,
        Long duration
) {
}
