package jp.newbees.mastersip.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ducpv on 12/9/16.
 */

public class DateTimeUtils {

    public static final SimpleDateFormat JAPAN_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日",
            Locale.JAPAN);

    public static final SimpleDateFormat ENGLISH_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd",
            Locale.ENGLISH);

    public static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH);

    public static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("HH:mm",
            Locale.ENGLISH);

    public static int subtractDateToYear(Date from, Date to) {
        Calendar a = getCalendar(from);
        Calendar b = getCalendar(to);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                        && a.get(Calendar.DAY_OF_MONTH) > b.get(Calendar.DAY_OF_MONTH))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.JAPANESE);
        cal.setTime(date);
        return cal;
    }

    public static Date convertStringToDate(String date, SimpleDateFormat format) {
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String getShortTime(String fullDate) {
        Date date = convertStringToDate(fullDate, SERVER_DATE_FORMAT);
        return SHORT_TIME_FORMAT.format(date);
    }
}
