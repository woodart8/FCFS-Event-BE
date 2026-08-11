package com.woodart8.fcfs.outbox.repository;

import com.woodart8.fcfs.outbox.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

}