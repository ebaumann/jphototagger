package de.elmar_baumann.imv.app;

import com.imagero.reader.AbstractImageReader;
import de.elmar_baumann.imv.SplashScreen;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.ImageProperties;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.lib.clipboard.lang.SystemUtil;
import de.elmar_baumann.lib.clipboard.lang.Version;
import de.elmar_baumann.lib.dialog.SystemOutputDialog;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;

/**
 * Initializes the application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/11
 */
public final class AppInit {

    private static AppInit INSTANCE;

    public static synchronized void init() {
        if (INSTANCE == null) {
            INSTANCE = new AppInit();
        }
    }

    private AppInit() {
        initApp();
    }

    private void initApp() {
        SystemOutputDialog.INSTANCE.captureOutput();
        AppLookAndFeel.set();
        checkJavaVersion();
        lock();
        AppDatabase.init();
        AppLoggingSystem.init();
        AbstractImageReader.install(ImageProperties.class);
        informationMessageInitGui();
        showMainWindow();
    }

    private static void lock() {
        if (!AppLock.lock() && !AppLock.forceUnlock()) {
            System.exit(1);
        }
    }

    private static void informationMessageInitGui() {
        SplashScreen.setMessageToSplashScreen(
                Bundle.getString(
                "Main.Main.InformationMessage.SplashScreen.InitGui"));
    }

    private static void showMainWindow() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new AppFrame().setVisible(true);
            }
        });
    }

    private static void checkJavaVersion() {
        Version javaVersion = SystemUtil.getJavaVersion();
        if (javaVersion != null &&
                javaVersion.compareTo(AppInfo.minJavaVersion) < 0) {
            errorMessageJavaVersion(javaVersion);
            System.exit(2);
        }
    }

    private static void errorMessageJavaVersion(Version javaVersion) throws
            HeadlessException {
        JOptionPane.showMessageDialog(null,
                getVersionMessage(javaVersion),
                Bundle.getString("Main.ErrorMessage.JavaVersion.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static Object getVersionMessage(Version javaVersion) {
        return Bundle.getString("Main.ErrorMessage.JavaVersion",
                javaVersion, AppInfo.minJavaVersion);
    }
}
