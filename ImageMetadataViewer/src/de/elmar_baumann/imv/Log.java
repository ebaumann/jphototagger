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
public class Log {

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#WARNING}
     * 
     * @param c   class
     * @param ex  Exception
     */
    public static void logWarning(Class c, Exception ex) {
        Logger.getLogger(c.getName()).log(Level.WARNING, null, ex);
        ErrorListeners.getInstance().notifyErrorListener(
            new ErrorEvent(ex.getMessage(), c));
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
        ErrorListeners.getInstance().notifyErrorListener(
            new ErrorEvent(ex.getMessage(), c));
    }
}
