package com.sztus.teldrassil.sprint.util;

import com.sztus.framework.component.core.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author free
 */
public class SprintUtil {
    public static Long atStartOfDay(Date date,Integer day) {
        return DateUtils.truncate(DateUtil.addDays(date, day), Calendar.DATE).getTime();
    }


}
