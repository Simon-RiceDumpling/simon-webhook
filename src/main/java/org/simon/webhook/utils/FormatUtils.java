package org.simon.webhook.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-07-10 11:04
 **/
public class FormatUtils {

    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static String localDataTimeFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND));
    }


}
