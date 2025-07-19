package com.woodart8.fcfs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long eventId;

    String eventName;
    LocalDate startDate;
    LocalDate endDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Lob
    String eventConfig;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Event of(
            final String eventName,
            final String eventConfig,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return Event.builder()
                .eventName(eventName)
                .eventConfig(eventConfig)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

}
