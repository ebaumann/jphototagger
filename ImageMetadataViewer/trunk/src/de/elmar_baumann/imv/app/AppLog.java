package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.impl.ErrorListeners;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs <strong>localized</strong> messages.
 *
 * Uses {@link Bundle} to get the messages. If a message must not be displayed
 * to the user, the following bundle key can be used: * {@link #USE_STRING}.
 * Example:
 * 
 * {@code
 * AppLog.logFiner(MyClass.class, AppLog.USE_STRING, "For developers only");
 * }
 *
 * This key contains only one parameter which will be substitued through the
 * following string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-11
 */
public final class AppLog {

    public static final String USE_STRING = "AppLog.UseString";

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
    public static void logFinest(Class c, String bundleKey, Object... params) {
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
    public static void logFiner(Class c, String bundleKey, Object... params) {
        log(c, Level.FINER, bundleKey, params);
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
    public static void logInfo(Class c, String bundleKey, Object... params) {
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
    public static void logWarning(Class c, String bundleKey, Object... params) {
        log(c, Level.WARNING, Bundle.getString(bundleKey, params));
        ErrorListeners.INSTANCE.notifyErrorListener(new ErrorEvent(bundleKey, c));
    }

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#SEVERE}.
     * 
     * @param c   logger's class
     * @param ex  Exception
     */
    public static void logSevere(Class c, Exception ex) {
        Logger.getLogger(c.getName()).log(Level.SEVERE, null, ex);
        ErrorListeners.INSTANCE.notifyErrorListener(
                new ErrorEvent(ex.getMessage(), c));
    }

    private static void log(
            Class c, Level level, String bundleKey, Object... params) {
        Logger.getLogger(c.getName()).log(
                level, Bundle.getString(bundleKey, params));
    }

    private AppLog() {
    }
}
