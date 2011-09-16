package org.jphototagger.program.app.logging;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.program.app.AppInfo;

/**
 *
 * @author Elmar Baumann
 */
public final class AppLogUtil {

    private static final String OS_VM_INFO = getOsVmInfo();
    private static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * Returns {@code Throwable#getLocalizedMessage()} prepended by
     * {@code AppInfo#APP_NAME} and {@code AppInfo#APP_VERSION}.
     *
     * @param  t throwable
     * @return   message
     */
    public static String createMessage(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t == null");
        }

        String message = t.getLocalizedMessage();

        if ((message == null) || message.isEmpty()) {
            message = "Severe: " + t.getClass();
        }

        return prependVersionInfo(true, message);
    }

    private static String prependVersionInfo(boolean prepend, String s) {
        return prepend
                ? MessageFormat.format("{0} {1}, {2}: {3}", AppInfo.APP_NAME, AppInfo.APP_VERSION, OS_VM_INFO, s)
                : s;
    }

    private static String getOsVmInfo() {
        return MessageFormat.format("{0} {1} {2}, {3} {4}", System.getProperty("os.name"),
                System.getProperty("os.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.version"));
    }

    private static String getSystemInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append(LINE_SEP).append("Java Runtime Environment version: ").append(System.getProperty("java.version"));
        sb.append(LINE_SEP).append("Java Runtime Environment vendor: ").append(System.getProperty("java.vendor"));
        sb.append(LINE_SEP).append("Java vendor URL: ").append(System.getProperty("java.vendor.url"));
        sb.append(LINE_SEP).append("Java installation directory: ").append(System.getProperty("java.home"));
        sb.append(LINE_SEP).append("Java Virtual Machine specification version: ").append(System.getProperty("java.vm.specification.version"));
        sb.append(LINE_SEP).append("Java Virtual Machine specification vendor: ").append(System.getProperty("java.vm.specification.vendor"));
        sb.append(LINE_SEP).append("Java Virtual Machine specification name: ").append(System.getProperty("java.vm.specification.name"));
        sb.append(LINE_SEP).append("Java Virtual Machine implementation version: ").append(System.getProperty("java.vm.version"));
        sb.append(LINE_SEP).append("Java Virtual Machine implementation vendor: ").append(System.getProperty("java.vm.vendor"));
        sb.append(LINE_SEP).append("Java Virtual Machine implementation name: ").append(System.getProperty("java.vm.name"));
        sb.append(LINE_SEP).append("Java Runtime Environment specification version: ").append(System.getProperty("java.specification.version"));
        sb.append(LINE_SEP).append("Java Runtime Environment specification vendor: ").append(System.getProperty("java.specification.vendor"));
        sb.append(LINE_SEP).append("Java Runtime Environment specification name: ").append(System.getProperty("java.specification.name"));
        sb.append(LINE_SEP).append("Java class format version number: ").append(System.getProperty("java.class.version"));
        sb.append(LINE_SEP).append("Java class path: ").append(System.getProperty("java.class.path"));
        sb.append(LINE_SEP).append("List of paths to search when loading libraries: ").append(System.getProperty("java.library.path"));
        sb.append(LINE_SEP).append("Default temp file path: ").append(System.getProperty("java.io.tmpdir"));
        sb.append(LINE_SEP).append("Name of JIT compiler to use: ").append(System.getProperty("java.compiler"));
        sb.append(LINE_SEP).append("Path of extension directory or directories: ").append(System.getProperty("java.ext.dirs"));
        sb.append(LINE_SEP).append("Operating system name: ").append(System.getProperty("os.name"));
        sb.append(LINE_SEP).append("Operating system architecture: ").append(System.getProperty("os.arch"));
        sb.append(LINE_SEP).append("Operating system version: ").append(System.getProperty("os.version"));
        sb.append(LINE_SEP).append("File separator: ").append(System.getProperty("file.separator"));
        sb.append(LINE_SEP).append("Path separator: ").append(System.getProperty("path.separator"));
        sb.append(LINE_SEP).append("User's account name: ").append(System.getProperty("user.name"));
        sb.append(LINE_SEP).append("User's home directory: ").append(System.getProperty("user.home"));
        sb.append(LINE_SEP).append("User's current working directory: ").append(System.getProperty("user.dir"));

        return sb.toString();
    }

    public static void logSystemInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append(LINE_SEP).append("JPhotoTagger " + AppInfo.APP_VERSION);
        sb.append(getSystemInfo());
        Logger.getLogger(AppLogUtil.class.getName()).log(Level.INFO, sb.toString());
    }

    private AppLogUtil() {
    }
}
