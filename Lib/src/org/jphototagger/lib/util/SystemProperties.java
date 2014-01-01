package org.jphototagger.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;

/**
 * Encapsulates {@code  System.getProperty()} keys.
 *
 * @author  Elmar Baumann
 */
public final class SystemProperties {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

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

    public static String getTemporaryDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String systemInfoToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR).append("Java Runtime Environment version: ").append(System.getProperty("java.version"));
        sb.append(LINE_SEPARATOR).append("Java Runtime Environment vendor: ").append(System.getProperty("java.vendor"));
        sb.append(LINE_SEPARATOR).append("Java vendor URL: ").append(System.getProperty("java.vendor.url"));
        sb.append(LINE_SEPARATOR).append("Java installation directory: ").append(System.getProperty("java.home"));
        sb.append(LINE_SEPARATOR).append("Java Virtual Machine specification version: ").append(System.getProperty("java.vm.specification.version"));
        sb.append(LINE_SEPARATOR).append("Java Virtual Machine specification vendor: ").append(System.getProperty("java.vm.specification.vendor"));
        sb.append(LINE_SEPARATOR).append("Java Virtual Machine specification name: ").append(System.getProperty("java.vm.specification.name"));
        sb.append(LINE_SEPARATOR).append("Java Virtual Machine implementation version: ").append(System.getProperty("java.vm.version"));
        sb.append(LINE_SEPARATOR).append("Java Virtual Machine implementation vendor: ").append(System.getProperty("java.vm.vendor"));
        sb.append(LINE_SEPARATOR).append("Java Virtual Machine implementation name: ").append(System.getProperty("java.vm.name"));
        sb.append(LINE_SEPARATOR).append("Java Runtime Environment specification version: ").append(System.getProperty("java.specification.version"));
        sb.append(LINE_SEPARATOR).append("Java Runtime Environment specification vendor: ").append(System.getProperty("java.specification.vendor"));
        sb.append(LINE_SEPARATOR).append("Java Runtime Environment specification name: ").append(System.getProperty("java.specification.name"));
        sb.append(LINE_SEPARATOR).append("Java class format version number: ").append(System.getProperty("java.class.version"));
        sb.append(LINE_SEPARATOR).append("Java class path: ").append(System.getProperty("java.class.path"));
        sb.append(LINE_SEPARATOR).append("List of paths to search when loading libraries: ").append(System.getProperty("java.library.path"));
        sb.append(LINE_SEPARATOR).append("Default temp file path: ").append(System.getProperty("java.io.tmpdir"));
        sb.append(LINE_SEPARATOR).append("Name of JIT compiler to use: ").append(System.getProperty("java.compiler"));
        sb.append(LINE_SEPARATOR).append("Path of extension directory or directories: ").append(System.getProperty("java.ext.dirs"));
        sb.append(LINE_SEPARATOR).append("Operating system name: ").append(System.getProperty("os.name"));
        sb.append(LINE_SEPARATOR).append("Operating system architecture: ").append(System.getProperty("os.arch"));
        sb.append(LINE_SEPARATOR).append("Operating system version: ").append(System.getProperty("os.version"));
        sb.append(LINE_SEPARATOR).append("File separator: ").append(System.getProperty("file.separator"));
        sb.append(LINE_SEPARATOR).append("Path separator: ").append(System.getProperty("path.separator"));
        sb.append(LINE_SEPARATOR).append("User's account name: ").append(System.getProperty("user.name"));
        sb.append(LINE_SEPARATOR).append("User's home directory: ").append(System.getProperty("user.home"));
        sb.append(LINE_SEPARATOR).append("User's current working directory: ").append(System.getProperty("user.dir"));
        appendMemoryUsage(sb);
        return sb.toString();
    }

    private static void appendMemoryUsage(StringBuilder sb) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemoryInBytes = runtime.totalMemory();
        long freeMemoryInBytes = runtime.freeMemory();
        long maxMemoryInBytes = runtime.maxMemory();
        long usedMemoryInBytes = totalMemoryInBytes - freeMemoryInBytes;
        sb.append(LINE_SEPARATOR).append("Total Memory: ").append(formatAsMegabytes(totalMemoryInBytes));
        sb.append(LINE_SEPARATOR).append("Free Memory: ").append(formatAsMegabytes(freeMemoryInBytes));
        sb.append(LINE_SEPARATOR).append("Maximum Memory: ").append(formatAsMegabytes(maxMemoryInBytes));
        sb.append(LINE_SEPARATOR).append("Used Memory: ").append(formatAsMegabytes(usedMemoryInBytes));
    }

    private static String formatAsMegabytes(long bytes) {
        double megabytes = (double) bytes / 1024 / 1024;
        MessageFormat format = new MessageFormat("{0,choice,0#{0,number,0.0}|999<{0,number,0}} MB");
        return format.format(new Object[]{megabytes});
    }

    private SystemProperties() {
    }
}
