/*
 * @(#)AppInit.java    Created on 2009-06-11
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.app;

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
 * @author  Elmar Baumann
 */
public final class AppInit {
    public static final AppInit   INSTANCE = new AppInit();
    private AppCommandLineOptions commandLineOptions;
    private volatile boolean      init;

    private AppInit() {}

    public void init(String[] args) {
        synchronized (this) {
            assert !init;

            if (init) {
                return;
            }

            init = true;
        }

        this.commandLineOptions =
            new AppCommandLineOptions(new CommandLineParser(args, "-", "="));
        init();
    }

    private void init() {
        AppLookAndFeel.set();
        captureOutput();    // Has to be called before AppLoggingSystem.init()!
        AppLoggingSystem.init();
        AppLogger.logSystemInfo();
        checkJavaVersion();
        lock();
        showSplashScreen();
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

        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("AppInit.Info.InitGui"));
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
                Logger.getLogger(AppInit.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }
    }

    private static void lock() {
        if (!AppLock.lock() &&!AppLock.forceLock()) {
            System.exit(1);
        }
    }

    private static void showMainWindow() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppFrame().setVisible(true);
            }
        });
    }

    private void setJptEventQueue() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(
            new AppEventQueue());
    }

    private static void checkJavaVersion() {
        Version javaVersion = SystemUtil.getJavaVersion();

        if ((javaVersion != null)
                && (javaVersion.compareTo(AppInfo.MIN_JAVA_VERSION) < 0)) {
            errorMessageJavaVersion(javaVersion);
            System.exit(2);
        }
    }

    private static void errorMessageJavaVersion(Version javaVersion) {
        MessageDisplayer.error(null, "AppInit.Error.JavaVersion", javaVersion,
                               AppInfo.MIN_JAVA_VERSION);
    }
}
