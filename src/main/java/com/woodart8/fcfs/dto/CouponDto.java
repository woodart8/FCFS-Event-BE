package com.woodart8.fcfs.dto;

import com.woodart8.fcfs.entity.Coupon;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CouponDto {

    private String code;
    private String description;
    private boolean isUsed;
    private LocalDateTime expirationDate;

    public static CouponDto from(Coupon coupon) {
        return new CouponDto(
                coupon.getCode(),
                coupon.getDescription(),
                coupon.isUsed(),
                coupon.getExpirationDate()
        );
    }

}
