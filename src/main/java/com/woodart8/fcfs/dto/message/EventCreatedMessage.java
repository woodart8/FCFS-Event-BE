package com.woodart8.fcfs.dto.message;

public record EventCreatedMessage(
        Long eventId,
        Long maxCouponAmount
) {
}
