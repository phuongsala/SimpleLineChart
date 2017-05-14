package promiennam.co.simplelinechart.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Phuong on 13-May-17.
 */

public class DateTimeUtil {

    public DateTimeUtil() {

    }

    public long convertDateStringToMillis(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(strDate).getTime();
        } catch (ParseException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public String convertFloatDateToStringDate(float floatDate) {
        Date date = new Date((long) floatDate);
        DateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
        return dateFormat.format(date);
    }

    public String convertFloatDateToMonth(float floatDate) {
        Date date = new Date((long) floatDate);
        DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        return dateFormat.format(date);
    }

    public String convertFloatDateToQuarter(float floatDate) {
        Date date = new Date((long) floatDate);
        DateFormat dateFormat = new SimpleDateFormat("MMM");
        String rs = dateFormat.format(date);

        if (rs.equals("Mar")) {
            return "Quarter I";
        } else if (rs.equals("Jun")) {
            return "Quarter II";
        } else if (rs.equals("Sep")) {
            return "Quarter III";
        } else if (rs.equals("Dec")) {
            return "Quarter IV";
        }

        return "";
    }

    public Date convertStringDateToDate(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isTheLastDayOfMonth(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);

            if (day < 28) {
                return false;
            }

            if (month == 2) {
                if (isLeapYear(year)) {
                    if (day == 29) {
                        return true;
                    }
                } else {
                    if (day == 28) {
                        return true;
                    }
                }
            } else if (month == 1 || month == 3 || month == 5 || month == 7
                    || month == 8 || month == 10 || month == 12) {
                if (day == 31) {
                    return true;
                }
            } else {
                if (day == 30) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean isLeapYear(int year) {
        if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0))) {
            return true;
        }
        return false;
    }

    public boolean isQuarterMonth(int month) {
        if (month == 3 || month == 6 || month == 9 || month == 12) {
            return true;
        }
        return false;
    }

    public boolean isQuarterMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH) + 1;
        if (month == 3 || month == 6 || month == 9 || month == 12) {
            return true;
        }
        return false;
    }


}
