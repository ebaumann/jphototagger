package org.jphototagger.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Elmar Baumann
 */
public final class ExceptionUtil {

    public static String getStackTraceAsString(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t == null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        t.printStackTrace(ps);

        return baos.toString();
    }

    private ExceptionUtil() {
    }
}
