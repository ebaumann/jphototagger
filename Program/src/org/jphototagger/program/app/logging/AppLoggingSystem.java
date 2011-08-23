package org.jphototagger.program.app.logging;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.core.Storage;
import org.jphototagger.api.core.UserFilesProvider;
import org.jphototagger.domain.event.UserPropertyChangedEvent;
import org.jphototagger.lib.dialog.LogfileDialog;
import org.jphototagger.lib.io.FileUtil;
import org.openide.util.Lookup;

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
 * @author Elmar Baumann
 */
public final class AppLoggingSystem {

    private static final int LOGFILE_ROTATE_COUNT = 5;
    private static final int MAX_LOGFILE_SIZE_IN_BYTES = (int) LogfileDialog.DEFAULT_MAX_BYTES;
    private static final String LOGFILE_PATH_DIR;
    // INSTANCE exists only for applying user settings!
    private static final AppLoggingSystem INSTANCE = new AppLoggingSystem();
    private static final List<Handler> HANDLERS = new ArrayList<Handler>();
    private static final boolean APPEND_OUTPUT_TO_LOGFILE = false;
    private static Logger appLogger;
    private static Handler fileHandlerAllMsgs;
    private static Handler fileHandlerImportant;
    private static boolean init;
    private static Handler systemOutHandler;

    static {
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File userDirectory = provider.getUserSettingsDirectory();

        LOGFILE_PATH_DIR = userDirectory.getAbsolutePath();
    }

    public enum HandlerType {

        SYSTEM_OUT, FILE,}

    private AppLoggingSystem() {
        AnnotationProcessor.process(this);
    }

    /**
     * Initializes the application's logging system.
     */
    public synchronized static void init() {
        assert !init;

        if (!init) {
            init = true;

            try {
                ensureLogDirectoryExists();
                createHandlers();
                setLevelsToHandlers();
                setFormattersToFileHandlers();
                setEncodingToFileHandlers();
            } catch (Exception ex) {
                Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                createAppLogger();
                addHandlersToLogger(appLogger);
            }
        }
    }

    private static void ensureLogDirectoryExists() {
        try {
            UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
            File userDirectory = provider.getUserSettingsDirectory();
            String settingsDirectoryName = userDirectory.getAbsolutePath();
            File settingsDirectory = new File(settingsDirectoryName);

            FileUtil.ensureDirectoryExists(settingsDirectory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createHandlers() throws IOException {
        fileHandlerImportant = new FileHandler(getLogfilePathPatternErrorMessages(), MAX_LOGFILE_SIZE_IN_BYTES, LOGFILE_ROTATE_COUNT, APPEND_OUTPUT_TO_LOGFILE);
        fileHandlerAllMsgs = new FileHandler(getLogfilePathPatternAllMessages(), MAX_LOGFILE_SIZE_IN_BYTES, LOGFILE_ROTATE_COUNT, APPEND_OUTPUT_TO_LOGFILE);
        systemOutHandler = new StreamHandler(System.out, new SimpleFormatter());

        synchronized (HANDLERS) {
            HANDLERS.add(systemOutHandler);
            HANDLERS.add(fileHandlerImportant);
            HANDLERS.add(fileHandlerAllMsgs);
        }
    }

    private static void setLevelsToHandlers() throws SecurityException {
        systemOutHandler.setLevel(getLogLevel());
        fileHandlerImportant.setLevel(Level.WARNING);
        fileHandlerAllMsgs.setLevel(Level.ALL);
    }

    private static Level getLogLevel() {
        Level level = null;
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        if (storage.containsKey(Storage.KEY_LOG_LEVEL)) {
            String levelString = storage.getString(Storage.KEY_LOG_LEVEL);

            try {
                level = Level.parse(levelString);
            } catch (Exception ex) {
                Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (level == null) {
            storage.setString(Storage.KEY_LOG_LEVEL, Level.INFO.getLocalizedName());
        }

        return level == null ? Level.INFO : level;
    }

    private static void setFormattersToFileHandlers() {
        fileHandlerImportant.setFormatter(new XMLFormatter());
        fileHandlerAllMsgs.setFormatter(new SimpleFormatter());
    }

    // Else on Windows ANSII will be used
    private static void setEncodingToFileHandlers() throws SecurityException, UnsupportedEncodingException {
        fileHandlerImportant.setEncoding("UTF-8");
        fileHandlerAllMsgs.setEncoding("UTF-8");
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

    private static void addHandlersToLogger(Logger logger) {
        synchronized (HANDLERS) {
            for (Handler handler : HANDLERS) {
                logger.addHandler(handler);
            }
        }
    }

    @EventSubscriber(eventClass = UserPropertyChangedEvent.class)
    public void applySettings(UserPropertyChangedEvent evt) {
        if (appLogger != null && Storage.KEY_LOG_LEVEL.equals(evt.getPropertyKey())) {
            systemOutHandler.setLevel((Level) evt.getNewValue());
        }
    }

    public static void flush(HandlerType handler) {
        switch (handler) {
            case SYSTEM_OUT:
                if (systemOutHandler != null) {
                    systemOutHandler.flush();
                }

                break;

            case FILE:
                if (fileHandlerImportant != null) {
                    fileHandlerImportant.flush();
                }

                if (fileHandlerAllMsgs != null) {
                    fileHandlerAllMsgs.flush();
                }

                break;

            default:
                assert false;
        }
    }

    /**
     *
     * @return full path name
     */
    public static String getLogfilePathErrorMessages() {
        return getLogfilePathPrefix() + "-error-0.xml";
    }

    /**
     *
     * @return full path name
     */
    public static String geLogfilePathAllMessages() {
        return getLogfilePathPrefix() + "-all-0.txt";
    }

    private static String getLogfilePathPatternErrorMessages() {
        return getLogfilePathPrefix() + "-error-%g.xml";
    }

    private static String getLogfilePathPatternAllMessages() {
        return getLogfilePathPrefix() + "-all-%g.txt";
    }

    private static String getLogfilePathPrefix() {
        return LOGFILE_PATH_DIR + File.separator + "jphototagger-log";
    }
}
