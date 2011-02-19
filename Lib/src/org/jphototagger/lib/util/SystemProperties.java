package org.jphototagger.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

/**
 * Encapsulates {@link System.getProperty()} keys.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2011-02-19
 */
public final class SystemProperties {

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    public static String getJavaVendorUrl() {
        return System.getProperty("java.vendor.url");
    }

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    public static String getJavaClassVersion() {
        return System.getProperty("java.class.version");
    }

    public static String getJavaClassPath() {
        return System.getProperty("java.class.path");
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static String getOsArch() {
        return System.getProperty("os.arch");
    }

    public static String getOsVersion() {
        return System.getProperty("os.version");
    }

    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    public static String getPathSeparator() {
        return System.getProperty("path.separator");
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public static String getFileEncoding() {
        // http://download.oracle.com/javase/tutorial/i18n/text/convertintro.html
        OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
        String outEncoding = out.getEncoding();

        return outEncoding.isEmpty()
                ? System.getProperty("file.encoding")
                : outEncoding;
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    private SystemProperties() {
    }
}
