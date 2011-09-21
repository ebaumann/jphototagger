package org.jphototagger.program.app.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.SettingsDirectoryProvider;
import org.jphototagger.lib.io.FileUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class AppLoggingSystem {

    private static final String ALL_MESSAGES_LOGFILE_PATH;
    private static final String ERROR_MESSAGES_LOGFILE_PATH;
    private static final String LOGFILE_DIR_PATHNAME;
    private static final String LOGFILE_PATH_PREFIX;
    // Holding a Reference ensures not loosing the Handlers (LogManager stores Loggers as Weak References)
    private static final Logger APP_LOGGER = Logger.getLogger("org.jphototagger");
    private static boolean init;

    static {
        SettingsDirectoryProvider provider = Lookup.getDefault().lookup(SettingsDirectoryProvider.class);
        File userSettingsDirectory = provider.getUserSettingsDirectory();

        LOGFILE_DIR_PATHNAME = userSettingsDirectory.getAbsolutePath();
        LOGFILE_PATH_PREFIX = LOGFILE_DIR_PATHNAME + File.separator + "jphototagger-log";
        ALL_MESSAGES_LOGFILE_PATH = LOGFILE_PATH_PREFIX + "-all.txt";
        ERROR_MESSAGES_LOGFILE_PATH = LOGFILE_PATH_PREFIX + "-errors.xml";
    }

    public static void init() {
        synchronized (AppLoggingSystem.class) {
            if (init) {
                return;
            }
            init = true;
        }

        try {
            deleteObsoleteLogfiles();
            ensureLogDirectoryExists();
            createAndAddHandlersToAppLogger();
        } catch (Throwable t) {
            Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            APP_LOGGER.setLevel(Level.ALL); // Handlers are restricting the output
            APP_LOGGER.setUseParentHandlers(false);
            LogManager.getLogManager().addLogger(APP_LOGGER);
        }
    }

    private static void deleteObsoleteLogfiles() {
        File logfileDir = new File(LOGFILE_DIR_PATHNAME);
        File[] dirFiles = logfileDir.listFiles();

        for (File file : dirFiles) {
            String filePathname = file.getAbsolutePath();
            boolean isLogfile = filePathname.startsWith(LOGFILE_PATH_PREFIX);
            boolean isLocked = filePathname.endsWith(".lck");

            if (isLogfile && !isLocked) {
                boolean logfileDeleted = file.delete();
                
                if (logfileDeleted) {
                    APP_LOGGER.log(Level.INFO, "Deleted obsolete logfile ''{0}''", file);
                } else {
                    APP_LOGGER.log(Level.WARNING, "Can''t delete obsolete logfile ''{0}''", file);
                }
            }
        }
    }

    private static void ensureLogDirectoryExists() {
        SettingsDirectoryProvider provider = Lookup.getDefault().lookup(SettingsDirectoryProvider.class);
        File userDirectory = provider.getUserSettingsDirectory();
        String settingsDirectoryName = userDirectory.getAbsolutePath();
        File settingsDirectory = new File(settingsDirectoryName);

        try {
            FileUtil.ensureDirectoryExists(settingsDirectory);
        } catch (Throwable t) {
            Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private static void createAndAddHandlersToAppLogger() throws IOException {
        FileHandler fileHandlerErrorMessages = new FileHandler(ERROR_MESSAGES_LOGFILE_PATH);
        FileHandler fileHandlerAllMessages = new FileHandler(ALL_MESSAGES_LOGFILE_PATH);
        StreamHandler systemOutHandler = new StreamHandler(System.out, new SimpleFormatter());

        systemOutHandler.setLevel(lookupLogLevel());

        fileHandlerAllMessages.setLevel(Level.ALL);
        fileHandlerAllMessages.setFormatter(new SimpleFormatter());
        fileHandlerAllMessages.setEncoding("UTF-8");

        fileHandlerErrorMessages.setLevel(Level.WARNING);
        fileHandlerErrorMessages.setFormatter(new XMLFormatter());
        fileHandlerErrorMessages.setEncoding("UTF-8");

        APP_LOGGER.addHandler(systemOutHandler);
        APP_LOGGER.addHandler(fileHandlerErrorMessages);
        APP_LOGGER.addHandler(fileHandlerAllMessages);

        // Writing errors of others to the error logfile
        Logger.getLogger("").addHandler(fileHandlerErrorMessages);
    }

    // Usage now only for developers, "UserSettings.LogLevel", e.g. "INFO"
    private static Level lookupLogLevel() {
        Level level = null;
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage.containsKey(Preferences.KEY_LOG_LEVEL)) {
            String levelString = storage.getString(Preferences.KEY_LOG_LEVEL);

            try {
                level = Level.parse(levelString);
            } catch (Exception ex) {
                Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (level == null) {
            storage.setString(Preferences.KEY_LOG_LEVEL, Level.ALL.getLocalizedName());
        }

        return level == null ? Level.ALL : level;
    }

    /**
     *
     * @return full path name
     */
    public static String getErrorMessagesLogfilePath() {
        return ERROR_MESSAGES_LOGFILE_PATH;
    }

    /**
     *
     * @return full path name
     */
    public static String getAllMessagesLogfilePath() {
        return ALL_MESSAGES_LOGFILE_PATH;
    }

    private AppLoggingSystem() {
    }
}
