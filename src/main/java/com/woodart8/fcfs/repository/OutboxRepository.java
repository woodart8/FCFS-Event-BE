package com.woodart8.fcfs.repository;

import com.woodart8.fcfs.domain.OutboxStatus;
import com.woodart8.fcfs.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findByStatus(OutboxStatus status);

}