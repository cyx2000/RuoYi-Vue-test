package com.ruoyi.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateTimeUtils {

    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now();
    }

    public static long differentDays(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);

        return duration.toDays();
    }

}
