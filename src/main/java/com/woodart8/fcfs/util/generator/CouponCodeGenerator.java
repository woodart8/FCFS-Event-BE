package com.woodart8.fcfs.util.generator;

import java.security.SecureRandom;

public class CouponCodeGenerator {

    private CouponCodeGenerator() {}

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 대문자 + 숫자

    public static String generateCouponCode(Integer couponCodeLength) {
        SecureRandom random = new SecureRandom();
        StringBuilder couponCode = new StringBuilder(couponCodeLength);

        for (int i = 0; i < couponCodeLength; i++) {
            int index = random.nextInt(CHARSET.length()); // CHARSET에서 랜덤 인덱스
            couponCode.append(CHARSET.charAt(index)); // 랜덤 문자 추가
        }

        return couponCode.toString();
    }

}
