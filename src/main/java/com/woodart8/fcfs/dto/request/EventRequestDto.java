package com.woodart8.fcfs.dto.request;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventRequestDto {

    String eventName;
    Long maxCouponAmount;
    LocalDate startDate;
    LocalDate endDate;

}
