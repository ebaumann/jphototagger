package org.jphototagger.program.app;

import org.jphototagger.program.event.listener.impl.ErrorListeners;
import org.jphototagger.program.resource.JptBundle;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

/**
 * Logs <strong>localized</strong> messages.
 * <p>
 * Uses {@link JptBundle} to get the messages. If a message must not be
 * displayed  to the user, the following bundle key can be used:
 * {@link #USE_STRING}. Example:
 * <p>
 * {@code
 * AppLogger.logFiner(MyClass.class, AppLogger.USE_STRING, "Bla");
 * }
 * <p>
 * This key contains only one parameter which will be substitued through the
 * following string.
 *
 * @author Elmar Baumann
 */
public final class AppLogger {
    public static final String USE_STRING = "AppLog.UseString";
    private static final String OS_VM_INFO = getOsVmInfo();
    private static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#FINEST}.
     *
     * @param c         logger's class
     * @param bundleKey key for the message string in the bundle, optional with
     *                  placeholders for <code>params</code> formatted as
     *                  described in {@link MessageFormat}
     * @param params    optional params for the message string
     */
    public static void logFinest(Class<?> c, String bundleKey, Object... params) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (bundleKey == null) {
            throw new NullPointerException("bundleKey == null");
        }

        if (params == null) {
            throw new NullPointerException("params == null");
        }

        log(c, Level.FINEST, bundleKey, params);
    }

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#FINER}.
     *
     * @param c         logger's class
     * @param bundleKey key for the message string in the bundle, optional with
     *                  placeholders for <code>params</code> formatted as
     *                  described in {@link MessageFormat}
     * @param params    optional params for the message string
     */
    public static void logFiner(Class<?> c, String bundleKey, Object... params) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (bundleKey == null) {
            throw new NullPointerException("bundleKey == null");
        }

        if (params == null) {
            throw new NullPointerException("params == null");
        }

        log(c, Level.FINER, bundleKey, params);
    }

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#FINE}.
     *
     * @param c         logger's class
     * @param bundleKey key for the message string in the bundle, optional with
     *                  placeholders for <code>params</code> formatted as
     *                  described in {@link MessageFormat}
     * @param params    optional params for the message string
     */
    public static void logFine(Class<?> c, String bundleKey, Object... params) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (bundleKey == null) {
            throw new NullPointerException("bundleKey == null");
        }

        if (params == null) {
            throw new NullPointerException("params == null");
        }

        log(c, Level.FINE, bundleKey, params);
    }

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#INFO}.
     *
     * @param c         logger's class
     * @param bundleKey key for the message string in the bundle, optional with
     *                  placeholders for <code>params</code> formatted as
     *                  described in {@link MessageFormat}
     * @param params    optional params for the message string
     */
    public static void logInfo(Class<?> c, String bundleKey, Object... params) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (bundleKey == null) {
            throw new NullPointerException("bundleKey == null");
        }

        if (params == null) {
            throw new NullPointerException("params == null");
        }

        log(c, Level.INFO, bundleKey, params);
    }

    /**
     * Logs a warning with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#WARNING}
     *
     * @param c         logger's class
     * @param bundleKey key for the message string in the bundle, optional with
     *                  placeholders for <code>params</code> formatted as
     *                  described in {@link MessageFormat}
     * @param params    optional params for the message string
     */
    public static void logWarning(Class<?> c, String bundleKey, Object... params) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (bundleKey == null) {
            throw new NullPointerException("bundleKey == null");
        }

        if (params == null) {
            throw new NullPointerException("params == null");
        }

        log(c, Level.WARNING, bundleKey, params);
        ErrorListeners.INSTANCE.notifyListeners(c, JptBundle.INSTANCE.getString(bundleKey, params));
    }

    /**
     * Logs an exception with the class' logger and notifies the error
     * listeners.
     * <p>
     * The log level is {@link java.util.logging.Level#SEVERE}.
     *
     * @param c  logger's class
     * @param t  Throwable
     */
    public static void logSevere(Class<?> c, Throwable t) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (t == null) {
            throw new NullPointerException("ex == null");
        }

        String className = c.getName();
        String loggerName = className;
        String message = getMessage(t);
        LogRecord lr = new LogRecord(Level.SEVERE, message);

        setLogRecord(lr, loggerName, className);
        lr.setThrown(t);
        Logger.getLogger(loggerName).log(lr);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
        ErrorListeners.INSTANCE.notifyListeners(c, message);
    }

    private static void log(Class<?> c, Level level, String bundleKey, Object... params) {
        String className = c.getName();
        String loggerName = className;
        boolean osVmInfo = level.equals(Level.WARNING) || level.equals(Level.SEVERE);
        LogRecord lr = new LogRecord(level, getMessage(bundleKey, params, osVmInfo));

        setLogRecord(lr, loggerName, className);
        Logger.getLogger(loggerName).log(lr);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
    }

    private static void setLogRecord(LogRecord lr, String loggerName, String className) {
        lr.setLoggerName(loggerName);
        lr.setMillis(System.currentTimeMillis());
        lr.setSourceClassName(className);
        lr.setSourceMethodName(getMethodName(className));
    }

    /**
     * Returns {@link Throwable#getLocalizedMessage()} prepended by
     * {@link AppInfo#APP_NAME} and {@link AppInfo#APP_VERSION}.
     *
     * @param  t throwable
     * @return   message
     */
    public static String getMessage(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t == null");
        }

        String message = t.getLocalizedMessage();

        if ((message == null) || message.isEmpty()) {
            message = "Severe: " + t.getClass();
        }

        return prependVersionInfo(true, message);
    }

    private static String getMessage(String bundleKey, Object[] params, boolean vInfo) {
        return prependVersionInfo(vInfo, JptBundle.INSTANCE.getString(bundleKey, params));
    }

    private static String prependVersionInfo(boolean prepend, String s) {
        return prepend
               ? MessageFormat.format("{0} {1}, {2}: {3}", AppInfo.APP_NAME, AppInfo.APP_VERSION, OS_VM_INFO, s)
               : s;
    }

    // If this works false, java.util.logging.LogRecord.inferCaller() maybe
    // the better implementation
    private static String getMethodName(String classname) {
        for (StackTraceElement stackTraceElement : (new Throwable()).getStackTrace()) {
            if (stackTraceElement.getClassName().equals(classname)) {
                return stackTraceElement.getMethodName();
            }
        }

        return null;
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
        sb.append(LINE_SEP).append("Java Virtual Machine specification version: ").append(
            System.getProperty("java.vm.specification.version"));
        sb.append(LINE_SEP).append("Java Virtual Machine specification vendor: ").append(
            System.getProperty("java.vm.specification.vendor"));
        sb.append(LINE_SEP).append("Java Virtual Machine specification name: ").append(
            System.getProperty("java.vm.specification.name"));
        sb.append(LINE_SEP).append("Java Virtual Machine implementation version: ").append(
            System.getProperty("java.vm.version"));
        sb.append(LINE_SEP).append("Java Virtual Machine implementation vendor: ").append(
            System.getProperty("java.vm.vendor"));
        sb.append(LINE_SEP).append("Java Virtual Machine implementation name: ").append(
            System.getProperty("java.vm.name"));
        sb.append(LINE_SEP).append("Java Runtime Environment specification version: ").append(
            System.getProperty("java.specification.version"));
        sb.append(LINE_SEP).append("Java Runtime Environment specification vendor: ").append(
            System.getProperty("java.specification.vendor"));
        sb.append(LINE_SEP).append("Java Runtime Environment specification name: ").append(
            System.getProperty("java.specification.name"));
        sb.append(LINE_SEP).append("Java class format version number: ").append(
            System.getProperty("java.class.version"));
        sb.append(LINE_SEP).append("Java class path: ").append(System.getProperty("java.class.path"));
        sb.append(LINE_SEP).append("List of paths to search when loading libraries: ").append(
            System.getProperty("java.library.path"));
        sb.append(LINE_SEP).append("Default temp file path: ").append(System.getProperty("java.io.tmpdir"));
        sb.append(LINE_SEP).append("Name of JIT compiler to use: ").append(System.getProperty("java.compiler"));
        sb.append(LINE_SEP).append("Path of extension directory or directories: ").append(
            System.getProperty("java.ext.dirs"));
        sb.append(LINE_SEP).append("Operating system name: ").append(System.getProperty("os.name"));
        sb.append(LINE_SEP).append("Operating system architecture: ").append(System.getProperty("os.arch"));

        StringBuilder append =
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
        logInfo(AppLogger.class, USE_STRING, sb.toString());
    }

    private AppLogger() {}
}
