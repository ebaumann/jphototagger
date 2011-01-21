package org.jphototagger.lib.util;

/**
 * Math utils.
 *
 * @author Elmar Baumann
 */
public final class MathUtil {

    /**
     * Returns whether a double value is integer (has no decimal places).
     *
     * @param  value  double value
     * @return true if the value is integer
     */
    public static boolean isInteger(double value) {
        return value - java.lang.Math.floor(value) == 0;
    }

    private MathUtil() {}
}
