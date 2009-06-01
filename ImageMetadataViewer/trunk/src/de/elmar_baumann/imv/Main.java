package de.elmar_baumann.imv;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.AppLock;
import com.imagero.reader.AbstractImageReader;
import de.elmar_baumann.imv.app.AppInfo;
import de.elmar_baumann.imv.database.DatabaseTables;
import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.ImageProperties;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.lib.clipboard.lang.SystemUtil;
import de.elmar_baumann.lib.clipboard.lang.Version;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.JOptionPane;

/**
 * Startet das Programm.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public final class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        init();
    }

    private static void init() {
        LookAndFeelUtil.setSystemLookAndFeel();
        checkJavaVersion();
        lock();
        informationMessageInitDatabase();
        DatabaseTables.INSTANCE.createTables();
        initLogger();
        AbstractImageReader.install(ImageProperties.class);
        informationMessageInitGui();
        showFrame();
    }

    private static void lock() {
        if (!AppLock.lock() && !AppLock.forceUnlock()) {
            System.exit(1);
        }
    }

    private static void informationMessageInitDatabase() {
        SplashScreen.setMessageToSplashScreen(
                Bundle.getString("Main.Init.InformationMessage.SplashScreen.ConnectToDatabase"));
    }

    private static void informationMessageInitGui() {
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
            FileUtil.ensureDirectoryExists(UserSettings.INSTANCE.getSettingsDirectoryName());
            Logger logger = Logger.getLogger("de.elmar_baumann"); // NOI18N
            Level usersLevel = UserSettings.INSTANCE.getLogLevel();
            addFileLogHandler(logger);
            addStdoutLogHandler(usersLevel, logger);
            logger.setLevel(usersLevel);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void addFileLogHandler(Logger logger) throws IOException, SecurityException, InstantiationException, IllegalAccessException, SecurityException {
        Handler fileHandler = new FileHandler(AppLog.getLogfileName());
        fileHandler.setLevel(Level.WARNING);
        fileHandler.setFormatter((Formatter) UserSettings.INSTANCE.getLogfileFormatterClass().newInstance());
        logger.addHandler(fileHandler);
    }

    private static void addStdoutLogHandler(Level usersLevel, Logger logger) throws SecurityException {
        if (usersLevel != Level.WARNING && usersLevel != Level.SEVERE) {
            Handler stdoutHandler = new StreamHandler(System.out, new SimpleFormatter());
            stdoutHandler.setLevel(usersLevel);
            logger.addHandler(stdoutHandler);
        }
    }

    private static void checkJavaVersion() {
        Version javaVersion = SystemUtil.getJavaVersion();
        if (javaVersion != null && javaVersion.compareTo(AppInfo.minJavaVersion) < 0) {
            errorMessageJavaVersion(javaVersion);
            System.exit(2);
        }
    }

    private static void errorMessageJavaVersion(Version javaVersion) throws HeadlessException {
        JOptionPane.showMessageDialog(null,
                getVersionMessage(javaVersion),
                Bundle.getString("Main.ErrorMessage.JavaVersion.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static Object getVersionMessage(Version javaVersion) {
        return Bundle.getString("Main.ErrorMessage.JavaVersion",
                javaVersion, AppInfo.minJavaVersion);
    }

    private Main() {
    }
}
