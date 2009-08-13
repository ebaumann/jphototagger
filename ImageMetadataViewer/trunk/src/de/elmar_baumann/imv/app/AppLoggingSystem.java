package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Logging system of the application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-11
 */
public final class AppLoggingSystem {

    private static final int MAX_LOGFILE_SIZE_IN_BYTES = 1000000;
    private static final int MAX_LOGFILE_COUNT = 5;
    private static final boolean APPEND_OUTPUT_TO_LOGFILE = false;
    private static final int MEMORY_HANDLER_LOG_RECORDS_COUNT = 1000;
    private static final List<Handler> HANDLERS = new ArrayList<Handler>();
    private static boolean init;

    /**
     * Initializes the application's logging system.
     */
    public synchronized static void init() {
        assert !init;
        if (!init) {
            init = true;
            Level userLevel = UserSettings.INSTANCE.getLogLevel();
            ensureLogDirectoryExists();
            createHandlers(userLevel);
            createLogger(userLevel);
        }
    }

    private static void ensureLogDirectoryExists() {
        FileUtil.ensureDirectoryExists(new File(
                UserSettings.INSTANCE.getSettingsDirectoryName()));
    }

    private static void createLogger(Level userLevel) {
        try {
            Logger logger = Logger.getLogger("de.elmar_baumann"); // NOI18N
            addHandlers(logger);
            logger.setLevel(userLevel);
            LogManager.getLogManager().addLogger(logger);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void addHandlers(Logger logger) {
        for (Handler handler : HANDLERS) {
            logger.addHandler(handler);
        }
    }

    private static void createHandlers(Level userLevel) {
        try {
            createFileHandler();
            createSystemOutHandler(userLevel);

            // Has to be called after all other handlers were added!
            createMemoryHandlers();
        } catch (Exception ex) {
            AppLog.logSevere(AppLoggingSystem.class, ex);
        }
    }

    private static void createFileHandler()
            throws
            IOException,
            InstantiationException,
            IllegalAccessException {
        Handler fileHandler = new FileHandler(
                getLogfilePrefix() + "%g." + getLogfileSuffix(),
                MAX_LOGFILE_SIZE_IN_BYTES,
                MAX_LOGFILE_COUNT,
                APPEND_OUTPUT_TO_LOGFILE);
        fileHandler.setLevel(Level.WARNING); // Ignoring user's settings
        fileHandler.setFormatter((Formatter) UserSettings.INSTANCE.
                getLogfileFormatterClass().newInstance());
        HANDLERS.add(fileHandler);
    }

    private static void createSystemOutHandler(Level userLevel) {
        Handler systemOutHandler =
                new StreamHandler(System.out, new SimpleFormatter());
        systemOutHandler.setLevel(userLevel);
        HANDLERS.add(systemOutHandler);
    }

    private static void createMemoryHandlers() {
        for (Handler handler : new ArrayList<Handler>(HANDLERS)) {
            HANDLERS.add(new MemoryHandler(
                    handler, MEMORY_HANDLER_LOG_RECORDS_COUNT, Level.SEVERE));
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
                File.separator + "imagemetadataviewerlog";  // NOI18N
    }

    private static String getLogfileSuffix() {
        return "xml";
    }

    private AppLoggingSystem() {
    }
}
