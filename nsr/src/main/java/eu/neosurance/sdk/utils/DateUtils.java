package eu.neosurance.sdk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private static final String PATTERN_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String TIMEZONE = "UTC";

    public static Date jsonStringToDate(String s) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_DATE);
            sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
            return sdf.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    public static String dateToJsonString(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_DATE);
            sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }
}
