package com.woodart8.fcfs.outbox.repository;

import com.woodart8.fcfs.outbox.domain.OutboxStatus;
import com.woodart8.fcfs.outbox.entity.Outbox;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Outbox o where o.id = :id")
    Optional<Outbox> findByIdForUpdate(@Param("id") long id);

    List<Outbox> findByStatus(OutboxStatus status);

}