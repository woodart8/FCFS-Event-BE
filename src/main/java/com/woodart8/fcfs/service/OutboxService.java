package com.woodart8.fcfs.service;

import com.woodart8.fcfs.entity.Outbox;
import com.woodart8.fcfs.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Transactional
    public void completeOutbox(long outboxId) {
        Outbox outbox = outboxRepository.findById(outboxId)
                .orElseThrow(() -> new IllegalArgumentException("아웃박스 레코드를 찾을 수 없습니다. ID: " + outboxId));

        outbox.complete();
    }

}
