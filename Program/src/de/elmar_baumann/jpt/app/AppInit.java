/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.app;

import com.imagero.reader.AbstractImageReader;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.ImageProperties;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.lib.system.SystemUtil;
import de.elmar_baumann.lib.util.Version;
import de.elmar_baumann.lib.dialog.SystemOutputDialog;

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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-11
 */
public final class AppInit {

    private static       AppInit  INSTANCE;
    private static final String   CMD_LINE_OPTION_NO_OUTPUT_CAPTURE = "-nocapture";
    private final        String[] cmdLineArgs;
    private static       boolean  captureOutput                     = true;

    public static synchronized void init(String[] args) {
        if (INSTANCE == null) {
            INSTANCE = new AppInit(args);
        }
    }

    private AppInit(String[] cmdLineArgs) {
        this.cmdLineArgs = cmdLineArgs;
        initApp();
    }

    private void initApp() {
        AppLookAndFeel.set();
        captureOutput();
        checkJavaVersion();
        lock();
        SplashScreen.INSTANCE.init();
        SplashScreen.INSTANCE.setProgress(50);
        AppDatabase.init();
        AppLoggingSystem.init();
        SplashScreen.INSTANCE.setProgress(75);
        AbstractImageReader.install(ImageProperties.class);
        SplashScreen.INSTANCE.setMessage(Bundle.getString("AppInit.Info.SplashScreen.InitGui"));
        SplashScreen.INSTANCE.setProgress(100);
        showMainWindow();
        SplashScreen.INSTANCE.close();
    }

    private void captureOutput() {
        setCaptureOutput();
        if (captureOutput) {
            SystemOutputDialog.INSTANCE.captureOutput();
        }
    }

    private void setCaptureOutput() {
        if (cmdLineArgs == null) return;
        for (String arg : cmdLineArgs) {
            if (arg.equals(CMD_LINE_OPTION_NO_OUTPUT_CAPTURE)) {
                captureOutput = false;
                return;
            }
        }
    }

    public static boolean isCaptureOutput() {
        return captureOutput;
    }

    private static void lock() {
        if (!AppLock.lock() && !AppLock.forceLock()) {
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

    private static void checkJavaVersion() {
        Version javaVersion = SystemUtil.getJavaVersion();

        if (javaVersion != null && javaVersion.compareTo(AppInfo.MIN_JAVA_VERSION) < 0) {
            errorMessageJavaVersion(javaVersion);
            System.exit(2);
        }
    }

    private static void errorMessageJavaVersion(Version javaVersion) {
        MessageDisplayer.error(
                null,
                "AppInit.Error.JavaVersion",
                javaVersion, AppInfo.MIN_JAVA_VERSION);
    }
}
