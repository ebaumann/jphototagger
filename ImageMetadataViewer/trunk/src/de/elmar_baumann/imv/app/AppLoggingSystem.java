package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
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

    /**
     * Initializes the application's logging system.
     */
    public static void init() {
        initLogger();
    }

    private static void initLogger() {
        try {
            FileUtil.ensureDirectoryExists(new File(
                    UserSettings.INSTANCE.getSettingsDirectoryName()));
            Logger logger = Logger.getLogger("de.elmar_baumann"); // NOI18N
            Level usersLevel = UserSettings.INSTANCE.getLogLevel();
            addFileLogHandler(logger);
            addStdoutLogHandler(usersLevel, logger);
            logger.setLevel(usersLevel);
            LogManager.getLogManager().addLogger(logger);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void addFileLogHandler(Logger logger) throws IOException,
            SecurityException, InstantiationException, IllegalAccessException,
            SecurityException {
        Handler fileHandler = new FileHandler(
                getLogfilePrefix() + "%g." + getLogfileSuffix(),
                MAX_LOGFILE_SIZE_IN_BYTES,
                MAX_LOGFILE_COUNT,
                APPEND_OUTPUT_TO_LOGFILE);
        fileHandler.setLevel(Level.WARNING);
        fileHandler.setFormatter((Formatter) UserSettings.INSTANCE.
                getLogfileFormatterClass().newInstance());
        logger.addHandler(fileHandler);
    }

    private static void addStdoutLogHandler(Level usersLevel, Logger logger)
            throws SecurityException {
        if (usersLevel != Level.WARNING && usersLevel != Level.SEVERE) {
            Handler stdoutHandler =
                    new StreamHandler(System.out, new SimpleFormatter());
            stdoutHandler.setLevel(usersLevel);
            logger.addHandler(stdoutHandler);
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
