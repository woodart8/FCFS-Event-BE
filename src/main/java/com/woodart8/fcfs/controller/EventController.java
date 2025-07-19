package com.woodart8.fcfs.controller;

import com.woodart8.fcfs.dto.request.EventRequestDto;
import com.woodart8.fcfs.dto.response.EventResponseDto;
import com.woodart8.fcfs.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 이벤트 등록
    @PostMapping
    public ResponseEntity<EventResponseDto> postEvent(@RequestBody EventRequestDto eventRequestDto) {
        return ResponseEntity.ok(eventService.uploadEvent(eventRequestDto));
    }


}
