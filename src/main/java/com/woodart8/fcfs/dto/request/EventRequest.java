package com.woodart8.fcfs.dto.request;

import java.time.LocalDate;

public record EventRequest (
    String eventName,
    Long maxCouponAmount,
    LocalDate startDate,
    LocalDate endDate
) {
}
