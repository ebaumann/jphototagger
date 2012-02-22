package org.jphototagger.lib.util;

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

    private ThreadUtil() {
    }
}
