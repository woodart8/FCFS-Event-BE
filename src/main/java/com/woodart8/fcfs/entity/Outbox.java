package com.woodart8.fcfs.entity;

import com.woodart8.fcfs.domain.AggregateType;
import com.woodart8.fcfs.domain.EventType;
import com.woodart8.fcfs.domain.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private AggregateType aggregateType;   // EVENT

    private Long aggregateId;       // eventId

    private EventType eventType;       // EVENT_CREATED

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    public static Outbox of(
            AggregateType aggregateType,
            Long aggregateId,
            EventType eventType,
            String payload
    ) {
        Outbox outbox = new Outbox();
        outbox.aggregateType = aggregateType;
        outbox.aggregateId = aggregateId;
        outbox.eventType = eventType;
        outbox.payload = payload;
        outbox.status = OutboxStatus.PENDING;
        return outbox;
    }

    public void complete() {
        this.status = OutboxStatus.SENT;
    }
}
