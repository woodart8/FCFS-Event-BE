package com.woodart8.fcfs.coupon.dto.response;

import com.woodart8.fcfs.coupon.entity.Coupon;
import lombok.*;
import java.time.LocalDate;
import java.time.ZoneId;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CouponResponse {

    private String code;
    private String description;
    private boolean isUsed;
    private LocalDate expirationDate;

    public static CouponResponse fromEntity(Coupon coupon) {
        return new CouponResponse(
                coupon.getCode(),
                coupon.getDescription(),
                coupon.isUsed(),
                LocalDate.ofInstant(coupon.getExpirationDate().toInstant(), ZoneId.systemDefault())
        );
    }

}
