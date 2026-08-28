package com.woodart8.fcfs.coupon.redis;

public final class CouponRedisKey {

    private CouponRedisKey() {
    }

    public static String users(Long eventId) {
        return "event:" + eventId + ":coupon:users";
    }

    public static String issued(Long eventId) {
        return "event:" + eventId + ":coupon:issued";
    }

    public static String max(Long eventId) {
        return "event:" + eventId + ":coupon:max";
    }

    public static String queue(Long eventId) {
        return "event:" + eventId + ":coupon:queue";
    }

    public static String processing(Long eventId) {
        return "event:" + eventId + ":coupon:processing";
    }

    public static String request(String requestId) {
        return "coupon:request:" + requestId;
    }

    public static String events() {
        return "coupon:events";
    }

}