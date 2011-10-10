package org.jphototagger.lib.util;

import java.util.Collection;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class NumberUtil {

    /**
     *
     * @param  string may be null
     * @return b
     */
    public static boolean isShort(String string) {
        if (string == null) {
            return false;
        }

        try {
            Short.parseShort(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     *
     * @param  string may be null
     * @return b
     */
    public static boolean isLong(String string) {
        if (string == null) {
            return false;
        }

        try {
            Long.parseLong(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     *
     * @param  string may be null
     * @return b
     */
    public static boolean isInteger(String string) {
        if (string == null) {
            return false;
        }

        try {
            Integer.parseInt(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean allStringsAreIntegers(Collection<? extends String> strings) {
        if (strings == null) {
            throw new NullPointerException("strings == null");
        }

        for (String string : strings) {
            if (!isInteger(string)) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param  string may be null
     * @return b
     */
    public static boolean isFloat(String string) {
        if (string == null) {
            return false;
        }

        try {
            Float.parseFloat(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     *
     * @param  string may be null
     * @return b
     */
    public static boolean isDouble(String string) {
        if (string == null) {
            return false;
        }

        try {
            Double.parseDouble(string);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private NumberUtil() {
    }
}
