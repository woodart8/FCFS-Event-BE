package com.woodart8.fcfs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.woodart8.fcfs.dto.request.EventRequestDto;
import com.woodart8.fcfs.dto.response.EventResponseDto;
import com.woodart8.fcfs.entity.Event;
import com.woodart8.fcfs.repository.EventRepository;
import com.woodart8.fcfs.util.converter.EventConfigConverter;
import com.woodart8.fcfs.util.validator.EventValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public EventService(EventRepository eventRepository, RedisTemplate<String, String> redisTemplate) {
        this.eventRepository = eventRepository;
        this.redisTemplate = redisTemplate;
    }

    public EventResponseDto uploadEvent(EventRequestDto eventRequestDto) {
        if (!EventValidator.isValidEvent(eventRequestDto)) {
            throw new IllegalArgumentException();
        }

        Map<String, Object> config = new HashMap<>();
        config.put("maxCouponAmount", eventRequestDto.getMaxCouponAmount());
        String eventConfig = EventConfigConverter.toJson(config);

        Event event = eventRepository.save(
            Event.of(
                eventRequestDto.getEventName(),
                eventConfig,
                eventRequestDto.getStartDate(),
                eventRequestDto.getEndDate()
            )
        );

        if (eventRequestDto.getMaxCouponAmount() != null) {
            cacheMaxCouponAmount(event);
        }

        return EventResponseDto.fromEntity(event);
    }

    private void cacheMaxCouponAmount(Event event) {
        JsonNode configNode = EventConfigConverter.fromJson(event.getEventConfig());
        String maxCouponAmount = configNode.get("maxCouponAmount").asText();

        redisTemplate.opsForValue().set("event:" + event.getEventId() + ":coupon:max", maxCouponAmount);
    }

}
