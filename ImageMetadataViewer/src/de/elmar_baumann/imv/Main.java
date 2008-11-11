package de.elmar_baumann.imv;

import com.imagero.reader.AbstractImageReader;
import de.elmar_baumann.imv.database.DatabaseTables;
import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.ImageProperties;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.resource.Settings;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Startet das Programm.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        init();
    }

    private static void init() {
        initSettings();
        LookAndFeelUtil.setSystemLookAndFeel();
        lock();
        messageInitDatabase();
        DatabaseTables.getInstance().createTables();
        initLogger();
        AbstractImageReader.install(ImageProperties.class);
        messageInitGui();
        showFrame();
    }

    private static void initSettings() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.setAppName("ImageMetaDataViewer"); // NOI18N NEVER CHANGE NAME AND LOCATION
        settings.removeEmptyKeys();
        Settings.getInstance().setIconImagesPath(AppSettings.getAppIconPaths());
    }

    private static void lock() {
        if (!AppLock.lock() && !AppLock.forceUnlock()) {
            System.exit(1);
        }
    }

    private static void messageInitDatabase() {
        SplashScreen.setMessageToSplashScreen(
            Bundle.getString("Main.Init.InformationMessage.SplashScreen.ConnectToDatabase"));
    }

    private static void messageInitGui() {
        SplashScreen.setMessageToSplashScreen(
            Bundle.getString("Main.Main.InformationMessage.SplashScreen.InitGui"));
    }

    private static void showFrame() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new AppFrame().setVisible(true);
            }
        });
    }

    private static void initLogger() {
        try {
            FileUtil.ensureDirectoryExists(PersistentSettings.getInstance().getDirectoryName());
            Logger logger = Logger.getLogger("de.elmar_baumann"); // NOI18N
            Level usersLevel = UserSettings.getInstance().getLogLevel();
            addFileLogHandler(logger);
            addStdoutLogHandler(usersLevel, logger);
            logger.setLevel(usersLevel);
        } catch (Exception ex) {
            de.elmar_baumann.imv.Logging.logSevere(Main.class, ex);
        }
    }

    private static void addFileLogHandler(Logger logger) throws IOException, SecurityException, InstantiationException, IllegalAccessException, SecurityException {
        Handler fileHandler = new FileHandler(AppSettings.getLogfileName());
        fileHandler.setLevel(Level.WARNING);
        fileHandler.setFormatter((Formatter) UserSettings.getInstance().getLogfileFormatterClass().newInstance());
        logger.addHandler(fileHandler);
    }

    private static void addStdoutLogHandler(Level usersLevel, Logger logger) throws SecurityException {
        if (usersLevel != Level.WARNING && usersLevel != Level.SEVERE) {
            Handler stdoutHandler = new StreamHandler(System.out, new SimpleFormatter());
            stdoutHandler.setLevel(usersLevel);
            logger.addHandler(stdoutHandler);
        }
    }
}
