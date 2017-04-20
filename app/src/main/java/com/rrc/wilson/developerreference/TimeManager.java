package com.rrc.wilson.developerreference;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * TimeManager is a simple utility class to help with time operations.
 * Currently it is just used to determine if the Class or Language table needs to be updated.
 *
 * Utilizes JodaTime
 *
 * <pre>
 * Created by Wilson on 2017-04-02.
 *
 * Revisions
 * Wilson       2017-04-02      Created
 * Wilson       2017-04-20      Finalized and commented
 * </pre>
 */
final class TimeManager {
    static final int classThreshold = 14, languageThreshold = 42;

    private TimeManager(){}

    /**
     * Utility method to spit back the days between to long format times (System.timeInMilliseconds())
     * @param start The start time
     * @param end The end time
     * @return The number of days between
     */
    static int daysBetween(long start, long end){
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(end);
        Days d = Days.daysBetween(startTime, endTime);

        return d.getDays();
    }

    /**
     * Determines if the class table should be updated
     * @param start The start time
     * @param end The end time
     * @return True if the table should be updated, false otherwise
     */
    static boolean updateClass(long start, long end){
        return daysBetween(start, end) >= classThreshold;
    }

    /**
     * Determines if the language table should be updated
     * @param start The start time
     * @param end The end time
     * @return True if the table should be updated, false otherwise
     */
    static boolean updateLanguage(long start, long end){
        return daysBetween(start, end) >= languageThreshold;
    }
}
