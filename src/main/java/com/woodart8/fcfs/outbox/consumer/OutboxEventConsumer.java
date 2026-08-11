package com.woodart8.fcfs.outbox.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.woodart8.fcfs.event.entity.Event;
import com.woodart8.fcfs.event.repository.EventRepository;
import com.woodart8.fcfs.outbox.service.OutboxService;
import com.woodart8.fcfs.util.converter.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventConsumer {

    private final EventRepository eventRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final OutboxService outboxService;

    @KafkaListener(
            topics = "cdc.eventdb.outbox",
            groupId = "outbox-processor-group"
    )
    public void consume(String message) {
        try {
            JsonNode rootNode = JsonConverter.readTree(message);
            JsonNode outerPayloadNode = rootNode.path("payload");

            String operation = outerPayloadNode.path("op").asText();

            if (!"c".equals(operation)) {
                log.debug("Outbox 이벤트가 아니므로 무시합니다. op={}", operation);
                return;
            }

            JsonNode afterNode = outerPayloadNode.path("after");

            if (afterNode.isMissingNode() || afterNode.isNull()) {
                return;
            }

            long outboxId = afterNode.path("id").asLong();

            String innerPayload = afterNode.path("payload").asText();
            log.info("수신된 아웃박스 페이로드: {}", innerPayload);

            JsonNode innerPayloadNode = JsonConverter.readTree(innerPayload);
            long eventId = innerPayloadNode.get("eventId").asLong();

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. ID: " + eventId));

            cacheMaxCouponAmount(event);
            log.info("이벤트 ID {}의 최대 쿠폰 수량 캐싱 완료", eventId);

            boolean completed = outboxService.completeOutbox(outboxId);

            if (!completed) {
                log.info("이미 처리된 아웃박스입니다. ID={}", outboxId);
                return;
            }

            log.info("아웃박스 ID {} 상태 SENT로 업데이트 완료.", outboxId);
        } catch (Exception e) {
            log.error("아웃박스 메시지 처리 실패 (상태 업데이트 안 됨)", e);
            throw new RuntimeException("메시지 처리 실패로 인한 재시도 트리거", e);
        }
    }

    private void cacheMaxCouponAmount(Event event) {
        try {
            JsonNode configNode = JsonConverter.readTree(event.getEventConfig());
            String maxCouponAmount = configNode.get("maxCouponAmount").asText();

            LocalDateTime expireAt = event.getEndDate().plusDays(1).atStartOfDay();
            Duration ttl = Duration.between(LocalDateTime.now(), expireAt);

            redisTemplate.opsForValue().set(
                    "event:" + event.getEventId() + ":coupon:max",
                    maxCouponAmount,
                    ttl
            );
        } catch (Exception e) {
            log.error("Redis 캐싱 중 에러 발생", e);
            throw new RuntimeException(e);
        }
    }

}