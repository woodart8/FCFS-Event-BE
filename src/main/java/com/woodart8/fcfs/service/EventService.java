package com.woodart8.fcfs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodart8.fcfs.domain.AggregateType;
import com.woodart8.fcfs.domain.EventType;
import com.woodart8.fcfs.dto.message.EventCreatedMessage;
import com.woodart8.fcfs.dto.request.EventRequest;
import com.woodart8.fcfs.dto.response.EventResponse;
import com.woodart8.fcfs.entity.Event;
import com.woodart8.fcfs.entity.Outbox;
import com.woodart8.fcfs.repository.EventRepository;
import com.woodart8.fcfs.repository.OutboxRepository;
import com.woodart8.fcfs.util.converter.JsonConverter;
import com.woodart8.fcfs.util.validator.EventValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OutboxRepository outboxRepository;
    private final RedisTemplate<String, String> redisTemplate;

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
