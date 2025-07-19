package com.woodart8.fcfs.service;

import com.woodart8.fcfs.dto.request.CouponRequestDto;
import com.woodart8.fcfs.dto.response.CouponResponseDto;
import com.woodart8.fcfs.entity.Coupon;
import com.woodart8.fcfs.repository.CouponRepository;
import com.woodart8.fcfs.util.generator.CouponCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    public CouponService(
            CouponRepository couponRepository,
            ReactiveRedisTemplate<String, String> reactiveRedisTemplate
    ) {
        this.couponRepository = couponRepository;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    // 쿠폰 발급 로직
    public Mono<CouponResponseDto> uploadCoupon(
            final Long eventId,
            final CouponRequestDto couponRequestDto
    ) {
        // 1. 최대 발급 개수 조회
        Mono<Long> maxCouponAmount = reactiveRedisTemplate.opsForValue()
                .get("event:" + eventId + ":coupon:max")
                .switchIfEmpty(Mono.error(new RuntimeException("이벤트 최대 쿠폰 수량 정보 없음")))
                .map(Long::parseLong);

        // 2. 현재 발급 개수 조회
        Mono<Long> issuedCouponAmount = reactiveRedisTemplate.opsForValue()
                .get("event:" + eventId + ":coupon:issued")
                .defaultIfEmpty("0")
                .map(Long::parseLong);

        return reactiveRedisTemplate.opsForValue()
                .increment("event:" + eventId + ":coupon:issued", 1)
                .flatMap(currentIssued -> Mono.zip(maxCouponAmount, Mono.just(currentIssued)))
                .flatMap(tuple -> {
                    Long max = tuple.getT1();
                    Long issued = tuple.getT2();

                    if (issued > max) {
                        // 롤백
                        return reactiveRedisTemplate.opsForValue()
                                .decrement("event:" + eventId + ":coupon:issued", 1)
                                .then(Mono.error(new IllegalStateException("쿠폰 수량 초과")));
                    }

                    return issueCoupon(eventId, couponRequestDto)
                            .onErrorResume(ex -> reactiveRedisTemplate.opsForValue()
                                    .decrement("event:" + eventId + ":coupon:issued", 1)
                                    .then(Mono.error(ex)));
                });
    }

    // 쿠폰을 DB에 저장하는 로직
    private Mono<CouponResponseDto> issueCoupon(Long eventId, CouponRequestDto dto) {
        String code = CouponCodeGenerator.generateCouponCode(16);
        String description = dto.getDescription();
        LocalDate expirationDate = LocalDate.now().plusDays(dto.getDuration());

        return couponRepository.existsByEventIdAndCode(eventId, code)
                .flatMap(exists -> {
                    if (exists) {
                        return issueCoupon(eventId, dto); // 재귀 재시도
                    } else {
                        Coupon coupon = Coupon.of(eventId, code, description, expirationDate);
                        return couponRepository.save(coupon)
                                .map(CouponResponseDto::fromEntity);
                    }
                });
    }


}
