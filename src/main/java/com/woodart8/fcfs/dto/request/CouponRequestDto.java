package com.woodart8.fcfs.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CouponRequestDto {

    String description;
    Long duration;

}
