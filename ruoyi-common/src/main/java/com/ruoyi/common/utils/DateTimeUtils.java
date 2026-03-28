package com.ruoyi.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 时间工具类
 *
 * @author winter123
 */
public class DateTimeUtils {

    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now();
    }

    public static long differentDays(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);

        return duration.toDays();
    }

}
