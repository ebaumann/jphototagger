/*
 * @(#)Logger.java    2008-11-11
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

package de.elmar_baumann.lib.util.logging;

import de.elmar_baumann.lib.resource.Bundle;

import java.text.MessageFormat;

import java.util.logging.Level;
import java.util.Properties;

/**
 * Logs localized messages.
 * <p>
 * Uses a {@link Properties} object to get the messages. If a message must not
 * be displayed to the user, the following bundle key can be used:
 * {@link #USE_STRING}. Then the properties object have to contain a key
 * <code>Log.UseString</code> with the value <code>{0}</code> which will be
 * replaced by the logged string.
 * Example:
 * <p>
 * {@code
 * logger.logFiner(MyClass.class, Logger.USE_STRING, "For developers only");
 * }
 *
 * @author  Elmar Baumann
 */
public final class Logger {
    public static final String USE_STRING = "Log.UseString";
    private final Bundle       bundle;

    /**
     * Creates a logger which reads strings from a specific bundle.
     *
     * @param bundle bundle with the logger's strings
     */
    public Logger(Bundle bundle) {
        this.bundle = bundle;
    }

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
    public void logFinest(Class<?> c, String bundleKey, Object... params) {
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
    public void logFiner(Class<?> c, String bundleKey, Object... params) {
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
    public void logFine(Class<?> c, String bundleKey, Object... params) {
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
    public void logInfo(Class<?> c, String bundleKey, Object... params) {
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
    public void logWarning(Class<?> c, String bundleKey, Object... params) {
        log(c, Level.WARNING, bundleKey, params);
    }

    /**
     * Logs an exception with the class' logger and notifies the error listeners.
     * The log level is {@link java.util.logging.Level#SEVERE}.
     *
     * @param c   logger's class
     * @param ex  Exception
     */
    public void logSevere(Class<?> c, Exception ex) {
        java.util.logging.Logger.getLogger(c.getName()).log(Level.SEVERE, null,
                                           ex);
    }

    private void log(Class<?> c, Level level, String bundleKey,
                     Object... params) {
        java.util.logging.Logger.getLogger(c.getName()).log(level,
                                           bundle.getString(bundleKey, params));
    }
}
