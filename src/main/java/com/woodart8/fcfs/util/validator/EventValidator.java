package com.woodart8.fcfs.util.validator;

import com.woodart8.fcfs.event.dto.request.EventRequest;

import java.time.LocalDate;

public class EventValidator {

    private EventValidator() {}

    public static boolean isValidEventName(String eventName) {
        return eventName != null && !eventName.isEmpty();
    }

    public static boolean isValidPeriod(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }

    public static boolean isValidMaxCouponAmount(Long maxCouponAmount) {
        if (maxCouponAmount == null) return true;
        return maxCouponAmount > 0;
    }

    public static boolean isValidEvent(EventRequest eventRequest) {
        return isValidEventName(eventRequest.eventName())
                && isValidPeriod(eventRequest.startDate(), eventRequest.endDate())
                && isValidMaxCouponAmount(eventRequest.maxCouponAmount());
    }

}
