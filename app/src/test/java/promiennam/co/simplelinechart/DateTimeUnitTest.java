package promiennam.co.simplelinechart;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by Phuong on 14-May-17.
 */

public class DateTimeUnitTest {
    @Test
    public void isTheLastDayOfMonthTest() throws Exception {
        assertEquals(false, isTheLastDayOfMonth(new Date()));
    }

    private boolean isTheLastDayOfMonth(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);

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
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
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


}
