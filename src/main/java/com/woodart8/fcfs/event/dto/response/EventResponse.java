package com.woodart8.fcfs.event.dto.response;

import com.woodart8.fcfs.event.entity.Event;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventResponse {

    Long eventId;
    String eventName;
    LocalDate startDate;
    LocalDate endDate;

    public static EventResponse fromEntity(Event event) {
        return EventResponse.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();
    }

}
