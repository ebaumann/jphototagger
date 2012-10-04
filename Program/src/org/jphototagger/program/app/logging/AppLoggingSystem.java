package org.jphototagger.program.app.logging;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.lib.io.FileUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class AppLoggingSystem {

    private static final String ALL_MESSAGES_LOGFILE_PATH;
    private static final String ERROR_MESSAGES_LOGFILE_PATH;
    private static final String LOGFILE_DIRECTORY_PATHNAME;
    private static final String LOGFILE_PATH_PREFIX;
    // Keeping a Reference ensures not loosing the Handlers (LogManager stores Loggers as Weak References)
    private static final Logger APP_LOGGER = Logger.getLogger("org.jphototagger");
    private static final Level DEFAULT_LOG_LEVEL = Level.INFO;
    private static boolean init;
    private static FileHandler allMessagesHandler;
    private static Handler systemOutHandler;
    private static Handler errorMessagesHandler;
    private static LogLevelUpdater levelUpdater;
    private static Level logLevel;
    private static volatile boolean listenToPrefs;

    static {
        PreferencesDirectoryProvider provider = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File userSettingsDirectory = provider.getUserPreferencesDirectory();
        LOGFILE_DIRECTORY_PATHNAME = userSettingsDirectory.getAbsolutePath();
        LOGFILE_PATH_PREFIX = LOGFILE_DIRECTORY_PATHNAME + File.separator + "jphototagger-log";
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
            levelUpdater = new LogLevelUpdater();
        } catch (Throwable t) {
            Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            APP_LOGGER.setLevel(Level.ALL); // Handlers are restricting the output
            APP_LOGGER.setUseParentHandlers(false);
            LogManager.getLogManager().addLogger(APP_LOGGER);
            setDefaultUncaughtExceptionHandler();
        }
    }

    private static void deleteObsoleteLogfiles() {
        File logfileDir = new File(LOGFILE_DIRECTORY_PATHNAME);
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
        PreferencesDirectoryProvider provider = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File userDirectory = provider.getUserPreferencesDirectory();
        String settingsDirectoryName = userDirectory.getAbsolutePath();
        File settingsDirectory = new File(settingsDirectoryName);
        try {
            FileUtil.ensureDirectoryExists(settingsDirectory);
        } catch (Throwable t) {
            Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private static void createAndAddHandlersToAppLogger() throws IOException {
        lookupLogLevel();
        createSystemOutHandler();
        createAllMessagesHandler();
        createErrorMessagesHandler();
        // Writing errors of others to the error logfile
        Logger.getLogger("").addHandler(errorMessagesHandler);
    }

    private static void createSystemOutHandler() throws SecurityException {
        systemOutHandler = new StreamHandler(System.out, new SimpleFormatter());
        systemOutHandler.setLevel(logLevel);
        APP_LOGGER.addHandler(systemOutHandler);
    }

    private static void createAllMessagesHandler() throws UnsupportedEncodingException, IOException, SecurityException {
        allMessagesHandler = new FileHandler(ALL_MESSAGES_LOGFILE_PATH);
        allMessagesHandler.setLevel(logLevel);
        allMessagesHandler.setFormatter(new SimpleFormatter());
        allMessagesHandler.setEncoding("UTF-8");
        APP_LOGGER.addHandler(allMessagesHandler);
    }

    private static void createErrorMessagesHandler() throws SecurityException, IOException, UnsupportedEncodingException {
        errorMessagesHandler = new FileHandler(ERROR_MESSAGES_LOGFILE_PATH);
        errorMessagesHandler.setLevel(Level.WARNING);
        errorMessagesHandler.setFormatter(new XMLFormatter());
        errorMessagesHandler.setEncoding("UTF-8");
        APP_LOGGER.addHandler(errorMessagesHandler);
    }

    // Usage now only for developers, "UserSettings.LogLevel", e.g. "INFO"
    private static void lookupLogLevel() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(Preferences.KEY_LOG_LEVEL)) {
            logLevel = resolveLevelString(prefs.getString(Preferences.KEY_LOG_LEVEL));
        }
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
            listenToPrefs = false;
            prefs.setString(Preferences.KEY_LOG_LEVEL, DEFAULT_LOG_LEVEL.getName());
            listenToPrefs = true;
        }
    }

    public static Level resolveLevelString(String levelString) {
        if (levelString == null) {
            return null;
        }
        try {
            return Level.parse(levelString);
        } catch (Exception ex) {
            Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                APP_LOGGER.log(Level.SEVERE, null, e);
            }
        });
    }

    /**
     * @return full path name
     */
    public static String getErrorMessagesLogfilePath() {
        return ERROR_MESSAGES_LOGFILE_PATH;
    }

    /**
     * @return full path name
     */
    public static String getAllMessagesLogfilePath() {
        return ALL_MESSAGES_LOGFILE_PATH;
    }

    public static File getLofileDirectory() {
        return new File(LOGFILE_DIRECTORY_PATHNAME);
    }

    public static Level getLogLevel() {
        return logLevel == null ? DEFAULT_LOG_LEVEL : logLevel;
    }

    private static class LogLevelUpdater {

        private LogLevelUpdater() {
            listen();
        }

        private void listen() {
            AnnotationProcessor.process(this);
            listenToPrefs = true;
        }

        @EventSubscriber(eventClass = PreferencesChangedEvent.class)
        public void preferencesChanged(PreferencesChangedEvent evt) {
            if (listenToPrefs && Preferences.KEY_LOG_LEVEL.equals(evt.getKey())) {
                Level level = resolveLevelString((String) evt.getNewValue());
                if (level == null) {
                    return;
                }
                if (systemOutHandler != null) {
                    systemOutHandler.setLevel(level);
                }
                if (allMessagesHandler != null) {
                    allMessagesHandler.setLevel(level);
                }
            }
        }
    }

    private AppLoggingSystem() {
    }
}
