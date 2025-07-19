package com.woodart8.fcfs.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Document(collection = "coupons")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Coupon {

    @Id
    private String id;

    private Long eventId;
    private String code;
    private String description;
    private boolean isUsed;

    // TTL 적용용 필드: 만료 시간(날짜+시간)
    @Indexed(name = "expirationDate_ttl_idx")
    private Date expirationDate;

    public static Coupon of(
            final Long eventId,
            final String code,
            final String description,
            final LocalDate expirationDate
    ) {
        return Coupon.builder()
                .eventId(eventId)
                .code(code)
                .description(description)
                .isUsed(false)
                .expirationDate(
                    Date.from(
                        expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    )
                )
                .build();
    }
}
