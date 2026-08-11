package com.woodart8.fcfs.event.service;

import com.woodart8.fcfs.event.domain.AggregateType;
import com.woodart8.fcfs.event.domain.EventType;
import com.woodart8.fcfs.outbox.dto.EventCreatedMessage;
import com.woodart8.fcfs.event.dto.request.EventRequest;
import com.woodart8.fcfs.event.dto.response.EventResponse;
import com.woodart8.fcfs.event.entity.Event;
import com.woodart8.fcfs.outbox.entity.Outbox;
import com.woodart8.fcfs.event.repository.EventRepository;
import com.woodart8.fcfs.outbox.repository.OutboxRepository;
import com.woodart8.fcfs.util.converter.JsonConverter;
import com.woodart8.fcfs.util.validator.EventValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public EventResponse uploadEvent(EventRequest eventRequest) {
        if (!EventValidator.isValidEvent(eventRequest)) {
            throw new IllegalArgumentException();
        }

        Map<String, Object> config = new HashMap<>();
        config.put("maxCouponAmount", eventRequest.maxCouponAmount());
        String eventConfig = JsonConverter.toJson(config);

        Event event = eventRepository.save(
            Event.of(
                eventRequest.eventName(),
                eventConfig,
                eventRequest.startDate(),
                eventRequest.endDate()
            )
        );

        EventCreatedMessage message =
                new EventCreatedMessage(
                        event.getEventId(),
                        eventRequest.maxCouponAmount()
                );

        String payload = JsonConverter.toJson(message);

        outboxRepository.save(
                Outbox.of(
                        AggregateType.EVENT,
                        event.getEventId(),
                        EventType.EVENT_CREATED,
                        payload
                )
        );

        return EventResponse.fromEntity(event);
    }

}
