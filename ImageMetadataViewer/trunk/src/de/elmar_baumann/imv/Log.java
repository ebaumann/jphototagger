package de.elmar_baumann.imv;

import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs messages.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/11
 */
public final class Log {

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#FINEST}.
     *
     * @param c        class
     * @param message  Message
     */
    public static void logFinest(Class c, String message) {
        log(c, Level.FINEST, message);
    }

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#FINER}.
     *
     * @param c        class
     * @param message  Message
     */
    public static void logFiner(Class c, String message) {
        log(c, Level.FINER, message);
    }

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#INFO}.
     *
     * @param c        class
     * @param message  Message
     */
    public static void logInfo(Class c, String message) {
        log(c, Level.INFO, message);
    }

    /**
     * Logs a warning with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#WARNING}
     *
     * @param c        class
     * @param message  Message
     */
    public static void logWarning(Class c, String message) {
        log(c, Level.WARNING, message);
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(message, c));
    }

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#WARNING}
     * 
     * @param c   class
     * @param ex  Exception
     */
    public static void logWarning(Class c, Exception ex) {
        log(c, Level.WARNING, ex);
    }

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#SEVERE}.
     * 
     * @param c   class
     * @param ex  Exception
     */
    public static void logSevere(Class c, Exception ex) {
        log(c, Level.SEVERE, ex);
    }

    private static void log(Class c, Level level, String message) {
        Logger.getLogger(c.getName()).log(level, message);
    }


    private static void log(Class c, Level level, Exception ex) {
        Logger.getLogger(c.getName()).log(level, null, ex);
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.getMessage(), c));
    }
    private Log() {}
}
