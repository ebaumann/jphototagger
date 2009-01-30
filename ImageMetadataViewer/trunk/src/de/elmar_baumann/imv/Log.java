package de.elmar_baumann.imv;

import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
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
        Logger.getLogger(c.getName()).log(Level.FINEST, message);
    }

    /**
     * Logs a message with the class' logger and the log level
     * {@link java.util.logging.Level#FINER}.
     *
     * @param c        class
     * @param message  Message
     */
    public static void logFiner(Class c, String message) {
        Logger.getLogger(c.getName()).log(Level.FINER, message);
    }

    /**
     * Logs a warning with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#WARNING}
     *
     * @param c        class
     * @param message  Message
     */
    public static void logWarning(Class c, String message) {
        Logger.getLogger(c.getName()).log(Level.WARNING, message);
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
        Logger.getLogger(c.getName()).log(Level.WARNING, null, ex);
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.getMessage(), c));
    }

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#SEVERE}.
     * 
     * @param c   class
     * @param ex  Exception
     */
    public static void logSevere(Class c, Exception ex) {
        Logger.getLogger(c.getName()).log(Level.SEVERE, null, ex);
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.getMessage(), c));
    }

    private Log() {}
}
