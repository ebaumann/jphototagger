/*
 * @(#)UpdateDownload.java    Created on 2010-01-05
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.app.update;

import org.jphototagger.lib.net.HttpUtil;
import org.jphototagger.lib.net.NetVersion;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.helper.FinalExecutable;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ProgressBar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.URL;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Checks for newer versions of JPhotoTagger and downloads them depending
 * on {@link UserSettings#isAutoDownloadNewerVersions()}.
 *
 * @author  Elmar Baumann
 */
public final class UpdateDownload extends Thread {
    private static final String FILENAME_WINDOWS = "JPhotoTagger-Setup.exe";
    private static final String FILENAME_ZIP = "JPhotoTagger.zip";
    private static final String URL_VERSION_CHECK_FILE =
        "http://www.jphototagger.org/jphototagger-version.txt";
    private static final String URL_WIN_INSTALLER =
        "http://www.jphototagger.org/dist/JPhotoTagger-setup.exe";
    private static final String URL_ZIP =
        "http://www.jphototagger.org/dist/JPhotoTagger.zip";
    private static final String VERSION_DELIMITER = ".";
    private Version             currentVersion;
    private Version             netVersion;
    private JProgressBar        progressBar;

    public UpdateDownload() {
        setName("Checking for and downloading newer version @ "
                + getClass().getSimpleName());
    }

    /**
     * Creates a new <code>UpdateDownload</code> thread.
     *
     * @param millisecondsToWait milliseconds to wait before starting the check
     */
    public static void checkForNewerVersion(final int millisecondsToWait) {
        if (millisecondsToWait < 0) {
            throw new IllegalArgumentException("Invalid milliseconds: "
                                               + millisecondsToWait);
        }

        if (!UserSettings.INSTANCE.isAutoDownloadNewerVersions()) {
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (millisecondsToWait > 0) {
                        Thread.sleep(millisecondsToWait);
                    }

                    new UpdateDownload().start();
                } catch (Exception ex) {
                    AppLogger.logSevere(UpdateDownload.class, ex);
                }
            }
        });

        t.setName("Waiting for version check @ "
                  + UpdateDownload.class.getSimpleName());
        t.start();
    }

    @Override
    public void run() {
        startProgressBar();

        try {
            if (hasNewerVersion()
                    && MessageDisplayer.confirmYesNo(null,
                        "UpdateDownload.Confirm.Download",
                        currentVersion.toString3(), netVersion.toString3())) {
                progressBarDownloadInfo();
                download();
            }
        } catch (Exception ex) {
            AppLogger.logInfo(UpdateDownload.class,
                              "UpdateDownload.Error.Compare",
                              ex.getLocalizedMessage());
        } finally {
            stopProgressBar();
        }
    }

    private Version currentVersion() {
        int    index         = AppInfo.APP_VERSION.indexOf(" ");
        String versionString = AppInfo.APP_VERSION.substring(0, (index > 0)
                ? index
                : AppInfo.APP_VERSION.length());

        currentVersion = Version.parseVersion(versionString, VERSION_DELIMITER);

        return currentVersion;
    }

    private void download() {
        try {
            File                 downloadFile = getDownloadFile();
            BufferedOutputStream os =
                new BufferedOutputStream(new FileOutputStream(downloadFile));

            HttpUtil.write(new URL(getDownloadUrl()), os);

            if (SystemUtil.isWindows()) {
                setFinalExecutable(downloadFile);
            } else {
                MessageDisplayer.information(null,
                                             "UpdateDownload.Info.Success",
                                             downloadFile);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(UpdateDownload.class, ex);
        }
    }

    private void setFinalExecutable(File downloadFile) {
        if (MessageDisplayer.confirmYesNo(
                null, "UpdateDownload.Confirm.SetFinalExecutable",
                downloadFile)) {
            FinalExecutable exec =
                new FinalExecutable(downloadFile.getAbsolutePath());

            AppLifeCycle.INSTANCE.addFinalTask(exec);
        }
    }

    private String getDownloadUrl() {
        return SystemUtil.isWindows()
               ? URL_WIN_INSTALLER
               : URL_ZIP;
    }

    private File getDownloadFile() {
        String dirname = UserSettings.INSTANCE.getSettingsDirectoryName()
                         + File.separator;
        String filename = SystemUtil.isWindows()
                          ? FILENAME_WINDOWS
                          : FILENAME_ZIP;

        return new File(dirname + File.separator + filename);
    }

    private void startProgressBar() {
        progressBar = ProgressBar.INSTANCE.getResource(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setIndeterminate(true);
                    progressBar.setStringPainted(true);
                    progressBar.setString(
                        JptBundle.INSTANCE.getString(
                            "UpdateDownload.Info.ProgressBar"));
                }
            }
        });
    }

    private void progressBarDownloadInfo() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setString(
                        JptBundle.INSTANCE.getString(
                            "UpdateDownload.Info.ProgressBarDownload"));
                }
            }
        });
    }

    private void stopProgressBar() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setIndeterminate(false);
                    progressBar.setString("");
                    progressBar.setStringPainted(false);
                    ProgressBar.INSTANCE.releaseResource(this);
                    progressBar = null;
                }
            }
        });
    }

    private boolean hasNewerVersion() throws Exception {
        netVersion = NetVersion.getOverHttp(URL_VERSION_CHECK_FILE,
                VERSION_DELIMITER);

        return currentVersion().compareTo(netVersion) < 0;
    }
}
