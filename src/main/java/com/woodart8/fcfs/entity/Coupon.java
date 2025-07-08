package com.woodart8.fcfs.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "coupons")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Coupon {

    @Id
    private Long id;
    private String code;
    private String description;
    private boolean isUsed;
    private LocalDateTime expirationDate;

}
