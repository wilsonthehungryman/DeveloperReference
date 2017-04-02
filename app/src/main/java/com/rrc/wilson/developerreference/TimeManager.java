package com.rrc.wilson.developerreference;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by Wilson on 2017-04-02.
 */

final class TimeManager {
    private TimeManager(){}

    static int daysBetween(long start, long end){
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(end);
        Days d = Days.daysBetween(startTime, endTime);

        return d.getDays();
    }

    static boolean updateClass(long start, long end){
        return daysBetween(start, end) >= 14;
    }

    static boolean updateLanguage(long start, long end){
        return daysBetween(start, end) >= 42;
    }
}
