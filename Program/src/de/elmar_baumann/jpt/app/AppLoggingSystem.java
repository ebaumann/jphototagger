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

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;

/**
 * Logging system of the application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-11
 */
public final class AppLoggingSystem implements UserSettingsListener {

    private static final int           MAX_LOGFILE_SIZE_IN_BYTES = 1000000;
    private static final int           LOGFILE_ROTATE_COUNT      = 5;
    private static final boolean       APPEND_OUTPUT_TO_LOGFILE  = false;
    private static final List<Handler> HANDLERS                  = new ArrayList<Handler>();
    private static       boolean       init;
    private static       Handler       systemOutHandler;
    private static       Handler       fileHandler;
    private static       Logger        appLogger;

    /**
     * Initializes the application's logging system.
     */
    public synchronized static void init() {
        assert !init;
        if (!init) {
            init = true;
            ensureLogDirectoryExists();
            createHandlers();
            createLogger();
        }
    }

    private static void ensureLogDirectoryExists() {
        try {
            FileUtil.ensureDirectoryExists(UserSettings.INSTANCE.getSettingsDirectoryName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createHandlers() {
        try {
            addFileHandler();
            addSystemOutHandler();
        } catch (Exception ex) {
            AppLogger.logSevere(AppLoggingSystem.class, ex);
        }
    }

    // Publishes only warning and severe events (on severe events the last
    // 1000 logfile records through it's memory handler)
    private static void addFileHandler() throws Exception {

        fileHandler = new FileHandler(logfileNamePattern(),
                                      MAX_LOGFILE_SIZE_IN_BYTES,
                                      LOGFILE_ROTATE_COUNT,
                                      APPEND_OUTPUT_TO_LOGFILE);

         // Ignoring user settings obove (INFO, FINE, ...) and keeping size small
        fileHandler.setLevel(Level.WARNING);
        fileHandler.setFormatter(new XMLFormatter());

        synchronized (HANDLERS) {
            HANDLERS.add(fileHandler);
        }
    }

    private static String logfileNamePattern() {

        return getLogfilePrefix() + "%g." + getLogfileSuffix();
    }

    private static void addSystemOutHandler() {

        systemOutHandler = new StreamHandler(System.out, new SimpleFormatter());

         // Log level shall be restricted only through the logger owning this handler
        systemOutHandler.setLevel(Level.FINEST);

        synchronized (HANDLERS) {
            HANDLERS.add(systemOutHandler);
        }
    }

    private static void createLogger() {
        try {
            appLogger = Logger.getLogger("de.elmar_baumann");
            addHandlersTo(appLogger);
            appLogger.setLevel(UserSettings.INSTANCE.getLogLevel());
            appLogger.setUseParentHandlers(false); // Don't log info records twice
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

    /**
     * Returns the name of the current log file (complete path).
     *
     * @return log file name
     */
    public static String getCurrentLogfileName() {

        return getLogfilePrefix() + "0." + getLogfileSuffix();
    }

    private static String getLogfilePrefix() {

        return UserSettings.INSTANCE.getSettingsDirectoryName() +
                    File.separator +
                    "imagemetadataviewerlog";
    }

    private static String getLogfileSuffix() {
        return "xml";
    }

    // INSTANCE exists only for applying user settings!
    private static final AppLoggingSystem INSTANCE = new AppLoggingSystem();

    private AppLoggingSystem() {

        UserSettings.INSTANCE.addUserSettingsListener(this);
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {

        if (appLogger != null && evt.getType().equals(UserSettingsEvent.Type.LOG_LEVEL)) {
            appLogger.setLevel(UserSettings.INSTANCE.getLogLevel());
        }
    }
    
    public enum HandlerType {
        SYSTEM_OUT,
        FILE,
    }

    public static void flush(HandlerType handler) {
        switch (handler) {
            case SYSTEM_OUT: if (systemOutHandler != null) systemOutHandler.flush(); break;
            case FILE      : if (fileHandler      != null) fileHandler     .flush(); break;
            default        : assert false;
        }

    }
}
