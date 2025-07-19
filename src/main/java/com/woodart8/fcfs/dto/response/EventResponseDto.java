package com.woodart8.fcfs.dto.response;

import com.woodart8.fcfs.entity.Event;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventResponseDto {

    Long eventId;
    String eventName;
    LocalDate startDate;
    LocalDate endDate;

    public static EventResponseDto fromEntity(Event event) {
        return EventResponseDto.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .build();
    }

}
