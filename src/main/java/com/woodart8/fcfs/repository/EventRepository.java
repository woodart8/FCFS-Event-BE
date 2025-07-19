package com.woodart8.fcfs.repository;

import com.woodart8.fcfs.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
