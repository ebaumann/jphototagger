package org.jphototagger.program.app;

import com.imagero.reader.AbstractImageReader;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.applifecycle.AppWillInitEvent;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.LongMessageDialog;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CommandLineParser;
import org.jphototagger.lib.util.ExceptionUtil;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.app.logging.AppLogUtil;
import org.jphototagger.program.app.logging.AppLoggingSystem;
import org.jphototagger.program.app.ui.AppFrame;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.app.ui.SplashScreen;
import org.jphototagger.program.module.thumbnails.cache.CacheUtil;
import org.jphototagger.program.resource.ImageProperties;
import org.openide.util.Lookup;

/**
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
            if (init) {
                throw new IllegalStateException("Already init!");
            }
            init = true;
        }
        commandLineOptions = new AppCommandLineOptions(new CommandLineParser(args, "-", "="));
        init();
    }

    private void init() {
        try {
            AppLookAndFeel.set(commandLineOptions.getLookAndFeel());
            AppLoggingSystem.init();
            AppLogUtil.logSystemInfo();
            checkJavaVersion();
            lock();
            showSplashScreen();
            EventBus.publish(new AppWillInitEvent(this));
            CacheUtil.initCaches();
            SplashScreen.INSTANCE.setMessage(Bundle.getString(AppInit.class, "AppInit.Info.ConnectToRepository"));
            Lookup.getDefault().lookup(Repository.class).init();
            SplashScreen.INSTANCE.setProgress(75);
            AbstractImageReader.install(ImageProperties.class);
            hideSplashScreen();
            showMainWindow();
            setJptEventQueue();
        } catch (Throwable t) {
            Logger.getLogger(AppInit.class.getName()).log(Level.SEVERE, null, t);
            showErrorMessage(t);
            AppLifeCycle.quitBeforeGuiWasCreated();
        }
    }

    private void hideSplashScreen() {
        if (!commandLineOptions.isShowSplashScreen()) {
            return;
        }
        SplashScreen.INSTANCE.setMessage(Bundle.getString(AppInit.class, "AppInit.Info.InitGui"));
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

    private static void lock() {
        if (!AppStartupLock.lock() && !AppStartupLock.forceLock()) {
            throw new RuntimeException("Application can't be locked");
        }
    }

    private static void showMainWindow() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                try {
                AppFrame appFrame = new AppFrame();
                appFrame.setVisible(true);
                } catch (Throwable t) {
                    Logger.getLogger(AppInit.class.getName()).log(Level.SEVERE, null, t);
                    MessageDisplayer.thrown(Bundle.getString(AppInit.class, "AppInit.Error.ShowMainWindow"), t);
                    AppLifeCycle.quitBeforeGuiWasCreated();
            }
            }
        });
    }

    private void setJptEventQueue() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new AppEventQueue());
    }

    private static void checkJavaVersion() {
        Version javaVersion = SystemUtil.getJavaVersion();
        boolean javaVersionIsTooOld = javaVersion != null && javaVersion.compareTo(AppInfo.MIN_JAVA_VERSION) < 0;
        if (javaVersionIsTooOld) {
            errorMessageJavaVersion(javaVersion);
            throw new RuntimeException("Java version" + javaVersion.toString3()
                    + " is too old. Required minimum version is " + AppInfo.MIN_JAVA_VERSION);
        }
    }

    private static void errorMessageJavaVersion(Version javaVersion) {
        String message = Bundle.getString(AppInit.class, "AppInit.Error.JavaVersion",
                javaVersion.toString3(), AppInfo.MIN_JAVA_VERSION.toString3());
        MessageDisplayer.error(null, message);
    }

    private void showErrorMessage(Throwable t) {
        LongMessageDialog dlg = new LongMessageDialog(null, true);
        String message = t.getLocalizedMessage() + "\n" + ExceptionUtil.getStackTraceAsString(t);
        dlg.setTitle(Bundle.getString(AppInit.class, "AppInit.Error.Thrown"));
        dlg.setLongMessage(message);
        dlg.setVisible(true);
    }
}
