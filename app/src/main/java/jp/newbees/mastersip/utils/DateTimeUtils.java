package jp.newbees.mastersip.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 12/9/16.
 */

public class DateTimeUtils {

    public static final SimpleDateFormat JAPAN_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日",
            Locale.JAPAN);

    public static final SimpleDateFormat ENGLISH_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd",
            Locale.ENGLISH);

    public static final SimpleDateFormat HEADER_CHAT_FORMAT = new SimpleDateFormat("MM/dd",
            Locale.ENGLISH);

    public static final SimpleDateFormat ENGLISH_FACEBOOK_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy",
            Locale.ENGLISH);

    public static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.JAPANESE);

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

    public static final String convertDateToString(Date date, SimpleDateFormat format) {
        String mData = format.format(date);
        return mData;
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

    public static String getServerTime(Date date) {
        return SERVER_DATE_FORMAT.format(date);
    }

    public static int calculateAgeWithDOB(Date dob) {
        Date to = Calendar.getInstance().getTime();
        Calendar a = getCalendar(dob);
        Calendar b = getCalendar(to);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                        && a.get(Calendar.DAY_OF_MONTH) > b.get(Calendar.DAY_OF_MONTH))) {
            diff--;
        }
        return diff;
    }

    public static int getCurrentAgeFromDoB(String dateOfBirth) {
        Date dob = convertStringToDate(dateOfBirth, ENGLISH_FACEBOOK_DATE_FORMAT);
        return calculateAgeWithDOB(dob);
    }

    public static String getHeaderDisplayDateInChatHistory(Date headerDate, Context context) {
        String strHeader = ENGLISH_DATE_FORMAT.format(headerDate);
        Calendar calendar = Calendar.getInstance();
        String today = ENGLISH_DATE_FORMAT.format(calendar.getTime());

        if (strHeader.equals(today)) {
            strHeader = context.getResources().getString(R.string.today);
        } else {
            strHeader = DateTimeUtils.convertDateToString(headerDate, HEADER_CHAT_FORMAT);
            strHeader += "(" + DateTimeUtils.getJapanDayOfWeek(headerDate) + ")";
        }
        return strHeader;
    }

    private static String getJapanDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.JAPANESE);
    }

    public static Date getDateWithoutTime(Date date) {
        try {
            return ENGLISH_DATE_FORMAT.parse(ENGLISH_DATE_FORMAT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTimerCallString(int time) {
        int mins = time / 60;
        int secs = time % 60;

        return String.format("%02d", mins) + ":"
                + String.format("%02d", secs);
    }
}
