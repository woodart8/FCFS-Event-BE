package com.woodart8.fcfs.outbox.service;

import com.woodart8.fcfs.outbox.entity.Outbox;
import com.woodart8.fcfs.outbox.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Transactional
    public boolean completeOutbox(long outboxId) {
        Outbox outbox = outboxRepository.findById(outboxId)
                .orElseThrow(() -> new IllegalArgumentException("아웃박스 레코드를 찾을 수 없습니다. ID: " + outboxId));

        if (outbox.isSent())
            return false;

        outbox.complete();
        return true;
    }

}
