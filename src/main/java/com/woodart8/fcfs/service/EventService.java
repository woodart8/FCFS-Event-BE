package com.woodart8.fcfs.service;

import com.woodart8.fcfs.dto.CouponDto;
import com.woodart8.fcfs.dto.EventRequestDto;
import com.woodart8.fcfs.entity.Coupon;
import com.woodart8.fcfs.repository.CouponRepository;
import com.woodart8.fcfs.util.CouponCodeGenerator;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class EventService {

    private final CouponRepository couponRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public EventService(CouponRepository couponRepository,
                        ReactiveRedisTemplate<String, String> redisTemplate) {
        this.couponRepository = couponRepository;
        this.redisTemplate = redisTemplate;
    }

    public Mono<CouponDto> handleEventParticipation(EventRequestDto eventRequest) {
        return redisTemplate.opsForValue().decrement("event:coupons", 1)
                .flatMap(remainingCoupons -> {
                    if (remainingCoupons != null && remainingCoupons > 0) {
                        return createUniqueCoupon()
                                .map(CouponDto::from);
                    } else {
                        // 쿠폰이 소진된 경우 Mono.empty() 반환
                        return Mono.empty();
                    }
                });
    }

    private Mono<Coupon> createUniqueCoupon() {
        return Mono.defer(() -> {
            String code = CouponCodeGenerator.generateCouponCode(12);
            return couponRepository.existsByCode(code)
                    .flatMap(exists -> {
                        if (exists) {
                            // 중복이면 재시도
                            return createUniqueCoupon();
                        } else {
                            Coupon coupon = Coupon.builder()
                                    .code(code)
                                    .description("이벤트 쿠폰")
                                    .expirationDate(LocalDateTime.now().plusDays(7))
                                    .isUsed(false)
                                    .build();

                            return couponRepository.save(coupon);
                        }
                    });
        });
    }

}
