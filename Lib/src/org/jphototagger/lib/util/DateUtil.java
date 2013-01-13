package org.jphototagger.lib.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elmar Baumann
 */
public final class DateUtil {

    private static final Map<Integer, Integer> DAYS_OF_MONTH = new HashMap<>(12);

    static {
        DAYS_OF_MONTH.put(1, 31);
        DAYS_OF_MONTH.put(2, 28);
        DAYS_OF_MONTH.put(3, 31);
        DAYS_OF_MONTH.put(4, 30);
        DAYS_OF_MONTH.put(5, 31);
        DAYS_OF_MONTH.put(6, 30);
        DAYS_OF_MONTH.put(7, 31);
        DAYS_OF_MONTH.put(8, 31);
        DAYS_OF_MONTH.put(9, 30);
        DAYS_OF_MONTH.put(10, 31);
        DAYS_OF_MONTH.put(11, 30);
        DAYS_OF_MONTH.put(12, 31);
    }

    /**
     *
     * @param year
     * @param month
     * @param day minimum 1582
     * @return
     */
    public static boolean isValidGregorianDate(int year, int month, int day) {
        return year >= 1582
                && month >= 1
                && month <= 12
                && isValidDay(year, month, day);
    }

    private static boolean isValidDay(int year, int month, int day) {
        int maxDays = month != 2
                ? DAYS_OF_MONTH.get(month)
                : isLeapYear(year)
                ? 29
                : 28;
        return day >= 1 && day <= maxDays;
    }

    public static boolean isLeapYear(int year) {
        return year > 0 && ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }
}
