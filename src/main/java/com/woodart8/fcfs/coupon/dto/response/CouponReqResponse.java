package com.woodart8.fcfs.coupon.dto.response;

public record CouponReqResponse(
        String requestId,
        String status
) {

    public static CouponReqResponse success(String requestId) {
        return new CouponReqResponse(requestId, "SUCCESS");
    }

    public static CouponReqResponse fail(String requestId) {
        return new CouponReqResponse(requestId, "FAIL");
    }

    public static CouponReqResponse duplicate(String requestId) { return new CouponReqResponse(requestId, "DUPLICATE"); }
}
