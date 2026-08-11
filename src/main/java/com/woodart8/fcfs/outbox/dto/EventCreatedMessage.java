package com.woodart8.fcfs.outbox.dto;

public record EventCreatedMessage(
        Long eventId,
        Long maxCouponAmount
) {
}
