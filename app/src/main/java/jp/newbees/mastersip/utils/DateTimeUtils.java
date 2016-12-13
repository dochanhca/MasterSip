package jp.newbees.mastersip.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ducpv on 12/9/16.
 */

public class DateTimeUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日",
            Locale.JAPAN);

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
}
