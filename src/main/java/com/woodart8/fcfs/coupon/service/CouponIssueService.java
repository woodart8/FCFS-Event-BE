package com.woodart8.fcfs.coupon.service;

import com.woodart8.fcfs.coupon.dto.request.CouponIssueRequest;
import com.woodart8.fcfs.coupon.entity.Coupon;
import com.woodart8.fcfs.coupon.repository.CouponRepository;
import com.woodart8.fcfs.util.generator.CouponCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponIssueService {

    private final CouponRepository couponRepository;

    public Mono<Void> issue(CouponIssueRequest request) {

        log.info(
                "쿠폰 발급 처리 시작. requestId={}, eventId={}",
                request.requestId(),
                request.eventId()
        );

        return couponRepository
                .existsByRequestId(request.requestId())
                .doOnNext(exists ->
                        log.info(
                                "requestId 존재 여부. requestId={}, exists={}",
                                request.requestId(),
                                exists
                        )
                )
                .flatMap(exists -> {

                    if (exists) {
                        log.warn(
                                "이미 발급된 요청입니다. requestId={}",
                                request.requestId()
                        );
                        return Mono.empty();
                    }

                    return generateUniqueCode(request.eventId())
                            .doOnNext(code ->
                                    log.info("쿠폰 코드 생성. code={}", code)
                            )
                            .flatMap(code -> {

                                LocalDate expirationDate =
                                        LocalDate.now()
                                                .plusDays(request.duration());

                                Coupon coupon = Coupon.of(
                                        request.eventId(),
                                        request.requestId(),
                                        code,
                                        request.description(),
                                        expirationDate
                                );

                                return couponRepository
                                        .save(coupon)
                                        .doOnSuccess(saved ->
                                                log.info(
                                                        "MongoDB 쿠폰 저장 성공. id={}, requestId={}",
                                                        saved.getId(),
                                                        saved.getRequestId()
                                                )
                                        )
                                        .doOnError(e ->
                                                log.error(
                                                        "MongoDB 쿠폰 저장 실패",
                                                        e
                                                )
                                        );
                            });
                })
                .then();
    }

    private Mono<String> generateUniqueCode(Long eventId) {

        String code =
                CouponCodeGenerator.generateCouponCode(16);

        log.info(
                "쿠폰 코드 중복 확인. eventId={}, code={}",
                eventId,
                code
        );

        return couponRepository
                .existsByEventIdAndCode(eventId, code)
                .doOnNext(exists ->
                        log.info(
                                "쿠폰 코드 중복 여부. code={}, exists={}",
                                code,
                                exists
                        )
                )
                .flatMap(exists -> {

                    if (exists) {
                        return generateUniqueCode(eventId);
                    }

                    return Mono.just(code);
                });
    }
}
