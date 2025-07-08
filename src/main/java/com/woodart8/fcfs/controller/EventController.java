package com.woodart8.fcfs.controller;

import com.woodart8.fcfs.dto.CouponDto;
import com.woodart8.fcfs.dto.EventRequestDto;
import com.woodart8.fcfs.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/participate")
    public Mono<ResponseEntity<CouponDto>> participateInEvent(@RequestBody EventRequestDto eventRequest) {
        return eventService.handleEventParticipation(eventRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }


}
