package org.jphototagger.program.app;

import org.jphototagger.program.app.logging.AppLoggingSystem;
import org.jphototagger.program.app.logging.AppLogUtil;
import com.imagero.reader.AbstractImageReader;
import org.jphototagger.lib.dialog.SystemOutputDialog;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.CommandLineParser;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.resource.ImageProperties;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.frames.AppFrame;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventBus;
import org.jphototagger.domain.event.AppWillInitEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.cache.CacheUtil;

/**
 * Initializes the application.
 *
 * Exits on errors. In that case the exit values are:
 *
 * <ul>
 * <li>1: The application couldn't be locked (create the lock file)</li>
 * <li>2: The Java version is too low</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class AppInit {

    public static final AppInit INSTANCE = new AppInit();
    private AppCommandLineOptions commandLineOptions;
    private volatile boolean init;

    private AppInit() {
    }

    public void init(String[] args) {
        synchronized (this) {
            assert !init;

            if (init) {
                return;
            }

            init = true;
        }

        this.commandLineOptions = new AppCommandLineOptions(new CommandLineParser(args, "-", "="));
        init();
    }

    private void init() {
        AppLookAndFeel.set();
        captureOutput();    // Has to be called before AppLoggingSystem.init()!
        AppLoggingSystem.init();
        AppLogUtil.logSystemInfo();
        checkJavaVersion();
        lock();
        showSplashScreen();
        EventBus.publish(new AppWillInitEvent(this));
        CacheUtil.initCaches();
        AppDatabase.init();
        SplashScreen.INSTANCE.setProgress(75);
        AbstractImageReader.install(ImageProperties.class);
        hideSplashScreen();
        showMainWindow();
        setJptEventQueue();
    }

    private void hideSplashScreen() {
        if (!commandLineOptions.isShowSplashScreen()) {
            return;
        }

        SplashScreen.INSTANCE.setMessage(JptBundle.INSTANCE.getString("AppInit.Info.InitGui"));
        SplashScreen.INSTANCE.setProgress(100);
        SplashScreen.INSTANCE.close();
    }

    private void showSplashScreen() {
        if (!commandLineOptions.isShowSplashScreen()) {
            return;
        }

        SplashScreen.INSTANCE.init();
        SplashScreen.INSTANCE.setProgress(50);
    }

    public AppCommandLineOptions getCommandLineOptions() {
        return commandLineOptions;
    }

    private void captureOutput() {
        if (commandLineOptions.isCaptureOutput()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        SystemOutputDialog.INSTANCE.captureOutput();
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(AppInit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void lock() {
        if (!AppLock.lock() && !AppLock.forceLock()) {
            System.exit(1);
        }
    }

    private static void showMainWindow() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppFrame appFrame = new AppFrame();

                appFrame.setVisible(true);
            }
        });
    }

    private void setJptEventQueue() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new AppEventQueue());
    }

    private static void checkJavaVersion() {
        Version javaVersion = SystemUtil.getJavaVersion();

        if ((javaVersion != null) && (javaVersion.compareTo(AppInfo.MIN_JAVA_VERSION) < 0)) {
            errorMessageJavaVersion(javaVersion);
            System.exit(2);
        }
    }

    private static void errorMessageJavaVersion(Version javaVersion) {
        MessageDisplayer.error(null, "AppInit.Error.JavaVersion", javaVersion, AppInfo.MIN_JAVA_VERSION);
    }
}
