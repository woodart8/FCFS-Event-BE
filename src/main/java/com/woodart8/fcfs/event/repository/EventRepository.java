package com.woodart8.fcfs.event.repository;

import com.woodart8.fcfs.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
