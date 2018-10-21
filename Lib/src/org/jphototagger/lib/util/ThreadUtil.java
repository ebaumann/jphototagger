package org.jphototagger.lib.util;

import java.util.Objects;

/**
 * @author Elmar Baumann
 */
public final class ThreadUtil {

    /**
     * Method to clarify, that invoking {@code Runnable#run()} is really intented. This also prevents IDEs and other
     * tools from showing warnings.
     *
     * @param runnable
     */
    public static void runInThisThread(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }
        runnable.run();
    }

    public static String stackTraceToString(Thread t) {
        Objects.requireNonNull(t, "t == null");

        return toString(t.getStackTrace());
    }

    public static String toString(StackTraceElement[] elts) {
        Objects.requireNonNull(elts, "elts == null");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < elts.length; i++) {
            StackTraceElement elt = elts[i];
            sb.append(i == 0 ? "" : "\n\t")
              .append(elt);
        }

        return sb.toString();
    }

    private ThreadUtil() {
    }
}
