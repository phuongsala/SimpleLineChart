package promiennam.co.simplelinechart.util;

import java.util.Comparator;
import java.util.Date;

import promiennam.co.simplelinechart.models.Nav;

/**
 * Created by Phuong on 14-May-17.
 */

public class SortByDateUtil implements Comparator<Nav> {

    @Override
    public int compare(Nav nav1, Nav nav2) {
        if (nav1 != null && nav2 != null) {
            DateTimeUtil dateTimeUtil = new DateTimeUtil();
            Date d1 = dateTimeUtil.convertStringDateToDate(nav1.getDate());
            Date d2 = dateTimeUtil.convertStringDateToDate(nav2.getDate());
            if (d1 != null && d2 != null) {
                if (d1.after(d2)) {
                    return 1;
                }
            }
            return 0;
        }
        return 0;
    }
}
