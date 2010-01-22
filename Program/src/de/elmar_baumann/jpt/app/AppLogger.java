/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.jpt.event.ErrorEvent;
import de.elmar_baumann.jpt.event.listener.impl.ErrorListeners;
import de.elmar_baumann.jpt.resource.Bundle;
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
 * AppLogger.logFiner(MyClass.class, AppLogger.USE_STRING, "For developers only");
 * }
 *
 * This key contains only one parameter which will be substitued through the
 * following string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-11
 */
public final class AppLogger {

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
    public static void logFinest(Class<?> c, String bundleKey, Object... params) {
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
        log(c, Level.WARNING, bundleKey, params);
        ErrorListeners.INSTANCE.notifyListeners(new ErrorEvent(bundleKey, c));
    }

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#SEVERE}.
     *
     * @param c   logger's class
     * @param ex  Exception
     */
    public static void logSevere(Class<?> c, Exception ex) {
        Logger.getLogger(c.getName()).log(Level.SEVERE, null, ex);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
        ErrorListeners.INSTANCE.notifyListeners(new ErrorEvent(ex.getMessage(), c));
    }

    private static void log(Class<?> c, Level level, String bundleKey, Object... params) {
        Logger.getLogger(c.getName()).log(level, Bundle.getString(bundleKey, params));
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
    }

    private AppLogger() {
    }
}
