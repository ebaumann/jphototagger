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
    public static void logFiner(Class<?> c, String bundleKey,
                                Object... params) {
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
    public static void logWarning(Class<?> c, String bundleKey,
                                  Object... params) {
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
        if (c == null) {
            throw new NullPointerException("c == null");
        }

        if (ex == null) {
            throw new NullPointerException("ex == null");
        }

        String    className  = c.getName();
        String    loggerName = className;
        String    message    = getMessage(ex);
        LogRecord lr         = new LogRecord(Level.SEVERE, message);

        setLogRecord(lr, loggerName, className);
        lr.setThrown(ex);
        Logger.getLogger(loggerName).log(lr);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
        ErrorListeners.INSTANCE.notifyListeners(c, message);
    }

    private static void log(Class<?> c, Level level, String bundleKey,
                            Object... params) {
        String    className  = c.getName();
        String    loggerName = className;
        LogRecord lr = new LogRecord(level, getMessage(bundleKey, params));

        setLogRecord(lr, loggerName, className);
        Logger.getLogger(loggerName).log(lr);
        AppLoggingSystem.flush(AppLoggingSystem.HandlerType.SYSTEM_OUT);
    }

    private static void setLogRecord(LogRecord lr, String loggerName,
                                     String className) {
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

        return prependVersionInfo(message);
    }

    private static String getMessage(String bundleKey, Object[] params) {
        return prependVersionInfo(JptBundle.INSTANCE.getString(bundleKey,
                params));
    }

    private static String prependVersionInfo(String s) {
        return AppInfo.APP_NAME + " " + AppInfo.APP_VERSION + ": " + s;
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
