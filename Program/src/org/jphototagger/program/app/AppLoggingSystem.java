/*
 * @(#)AppLoggingSystem.java    Created on 2009-06-11
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

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.UserSettings;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;

/**
 * Logging system of the application.
 * <p>
 * Contains theses handlers:
 * <ul>
 * <li>System output handler, level {@link UserSettings#getLogLevel()}</li>
 * <li>File handler for all messages, level {@link Level#ALL}</li>
 * <li>File handler for warning messages, level {@link Level#WARNING}</li>
 * </ul>
 *
 * @author  Elmar Baumann
 */
public final class AppLoggingSystem implements UserSettingsListener {
    private static final int LOGFILE_ROTATE_COUNT      = 5;
    private static final int MAX_LOGFILE_SIZE_IN_BYTES = 1000000;

    // INSTANCE exists only for applying user settings!
    private static final AppLoggingSystem INSTANCE                 =
        new AppLoggingSystem();
    private static final List<Handler>    HANDLERS                 =
        new ArrayList<Handler>();
    private static final boolean          APPEND_OUTPUT_TO_LOGFILE = false;
    private static Logger                 appLogger;
    private static Handler                fileHandlerAllMsgs;
    private static Handler                fileHandlerImportant;
    private static boolean                init;
    private static Handler                systemOutHandler;

    public enum HandlerType { SYSTEM_OUT, FILE, }

    private AppLoggingSystem() {
        UserSettings.INSTANCE.addUserSettingsListener(this);
    }

    /**
     * Initializes the application's logging system.
     */
    public synchronized static void init() {
        assert !init;

        if (!init) {
            init = true;
            ensureLogDirectoryExists();
            createHandlers();
            setLevelsToHandlers();
            setFormattersToHandlers();
            createAppLogger();
            addHandlersTo(appLogger);
        }
    }

    private static void ensureLogDirectoryExists() {
        try {
            FileUtil.ensureDirectoryExists(
                UserSettings.INSTANCE.getSettingsDirectoryName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createHandlers() {
        try {
            fileHandlerImportant =
                new FileHandler(logfileNamePatternImportant(),
                                MAX_LOGFILE_SIZE_IN_BYTES,
                                LOGFILE_ROTATE_COUNT, APPEND_OUTPUT_TO_LOGFILE);
            fileHandlerAllMsgs =
                new FileHandler(logfileNamePatternAllMessages(),
                                MAX_LOGFILE_SIZE_IN_BYTES,
                                LOGFILE_ROTATE_COUNT, APPEND_OUTPUT_TO_LOGFILE);
            systemOutHandler = new StreamHandler(System.out,
                    new SimpleFormatter());

            synchronized (HANDLERS) {
                HANDLERS.add(systemOutHandler);
                HANDLERS.add(fileHandlerImportant);
                HANDLERS.add(fileHandlerAllMsgs);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(AppLoggingSystem.class, ex);
        }
    }

    private static void setLevelsToHandlers() throws SecurityException {
        systemOutHandler.setLevel(UserSettings.INSTANCE.getLogLevel());
        fileHandlerImportant.setLevel(Level.WARNING);
        fileHandlerAllMsgs.setLevel(Level.ALL);
    }

    private static void setFormattersToHandlers() {
        fileHandlerImportant.setFormatter(new XMLFormatter());
        fileHandlerAllMsgs.setFormatter(new SimpleFormatter());
    }

    private static String logfileNamePatternImportant() {
        return getLogfilePrefix() + "%g." + getLogfileSuffix();
    }

    private static String logfileNamePatternAllMessages() {
        return getLogfilePrefix() + "-all-%g.txt";
    }

    private static void createAppLogger() {
        try {
            appLogger = Logger.getLogger("org.jphototagger");

            // Handlers are restricting the output
            appLogger.setLevel(Level.ALL);

            // Don't log info records twice
            appLogger.setUseParentHandlers(false);
            LogManager.getLogManager().addLogger(appLogger);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void addHandlersTo(Logger logger) {
        synchronized (HANDLERS) {
            for (Handler handler : HANDLERS) {
                logger.addHandler(handler);
            }
        }
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if ((appLogger != null)
                && evt.getType().equals(UserSettingsEvent.Type.LOG_LEVEL)) {
            systemOutHandler.setLevel(UserSettings.INSTANCE.getLogLevel());
        }
    }

    public static void flush(HandlerType handler) {
        switch (handler) {
        case SYSTEM_OUT :
            if (systemOutHandler != null) {
                systemOutHandler.flush();
            }

            break;

        case FILE :
            if (fileHandlerImportant != null) {
                fileHandlerImportant.flush();
            }

            if (fileHandlerAllMsgs != null) {
                fileHandlerAllMsgs.flush();
            }

            break;

        default :
            assert false;
        }
    }

    /**
     * Returns the name of the current log file (complete path).
     *
     * @return log file name
     */
    public static String getCurrentLogfileName() {
        return getLogfilePrefix() + "0." + getLogfileSuffix();
    }

    public static String getCurrentAllLogifleName() {
        return getLogfilePrefix() + "-all-0.txt";
    }

    private static String getLogfilePrefix() {
        return UserSettings.INSTANCE.getSettingsDirectoryName()
               + File.separator + "imagemetadataviewerlog";
    }

    private static String getLogfileSuffix() {
        return "xml";
    }
}
