/*
 * @(#)AppLogger.java    Created on 2008-11-11
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
 * @author  Elmar Baumann
 */
public final class AppLogger {
    public static final String USE_STRING = "AppLog.UseString";

    private AppLogger() {}

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
    public static void logFinest(Class<?> c, String bundleKey,
                                 Object... params) {
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
    public static void logFiner(Class<?> c, String bundleKey,
                                Object... params) {
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
    public static void logWarning(Class<?> c, String bundleKey,
                                  Object... params) {
        log(c, Level.WARNING, bundleKey, params);
        ErrorListeners.INSTANCE.notifyListeners(c,
                JptBundle.INSTANCE.getString(bundleKey, params));
    }

    /**
     * Logs an exception with the class' logger and notifies the error
     * listeners.
     * <p>
     * The log level is {@link java.util.logging.Level#SEVERE}.
     *
     * @param c   logger's class
     * @param ex  Exception
     */
    public static void logSevere(Class<?> c, Exception ex) {
        LogRecord lr = new LogRecord(Level.SEVERE, "");

        lr.setThrown(ex);

        String className = c.getName();

        lr.setSourceClassName(className);
        lr.setSourceMethodName(getMethodName(className));
        Logger.getLogger(className).log(lr);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
        ErrorListeners.INSTANCE.notifyListeners(c, ex.getMessage());
    }

    private static void log(Class<?> c, Level level, String bundleKey,
                            Object... params) {
        LogRecord lr = new LogRecord(level,
                                     JptBundle.INSTANCE.getString(bundleKey,
                                         params));
        String className = c.getName();

        lr.setSourceClassName(className);
        lr.setSourceMethodName(getMethodName(className));
        Logger.getLogger(className).log(lr);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
    }

    // If this works false, java.util.logging.LogRecord.inferCaller() maybe
    // the better implementation
    private static String getMethodName(String classname) {
        for (StackTraceElement stackTraceElement :
                (new Throwable()).getStackTrace()) {
            if (stackTraceElement.getClassName().equals(classname)) {
                return stackTraceElement.getMethodName();
            }
        }

        return null;
    }
}
