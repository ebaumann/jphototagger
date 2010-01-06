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
package de.elmar_baumann.jpt.app.update;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppInfo;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.net.HttpUtil;
import de.elmar_baumann.lib.net.NetVersion;
import de.elmar_baumann.lib.util.Version;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import javax.swing.JProgressBar;

/**
 * Checks for newer versions of JPhotoTagger and downloads them depending
 * on {@link UserSettings#isAutoDownloadNewerVersions()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-05
 */
public final class UpdateDownload extends Thread {

    private static final String       URL_CHECK         = "http://www.elmar-baumann.de/fotografie/tipps/computer/lightroom/jphototagger-version.txt";
    private static final String       URL_JAR           = "http://www.elmar-baumann.de/fotografie/download/JPhotoTagger.zip";
    private static final String       URL_WIN_INSTALLER = "http://www.elmar-baumann.de/fotografie/download/JPhotoTagger-setup.exe";
    private static final String       VERSION_DELIMITER = ".";
    private static final String       FILENAME_WINDOWS  = "JPhotoTagger-Setup.exe";
    private static final String       FILENAME_JAR      = "JPhotoTagger.jar";
    private              JProgressBar progressBar;
    private              Version      currentVersion;
    private              Version      netVersion;

    public UpdateDownload() {
        setName("Checking for and downloading newer version @ " + getClass().getSimpleName());
    }

    /**
     * Creates a new <code>UpdateDownload</code> thread.
     *
     * @param millisecondsToWait milliseconds to wait before starting the check
     */
    public static void checkForNewerVersion(int millisecondsToWait) {
        try {
            if (millisecondsToWait > 0) {
                Thread.sleep(millisecondsToWait);
            }
            new UpdateDownload().start();
        } catch (InterruptedException ex) {
            AppLog.logSevere(UpdateDownload.class, ex);
        }

    }

    @Override
    public void run() {

        if (!UserSettings.INSTANCE.isAutoDownloadNewerVersions()) return;
        startProgressBar();
        try {
            if (hasNewerVersion() && MessageDisplayer.confirmYesNo(null, "UpdateDownload.Confirm.Download", currentVersion, netVersion)) {
                progressBarDownloadInfo();
                download();
            }
        } catch (Exception ex) {
            AppLog.logInfo(UpdateDownload.class, "UpdateDownload.Error.Compare", ex.getLocalizedMessage());
        } finally {
            stopProgressBar();
        }
    }

    private Version currentVersion() {
        int    index         = AppInfo.APP_VERSION.indexOf(" ");
        String versionString = AppInfo.APP_VERSION.substring(0, index > 0 ? index : AppInfo.APP_VERSION.length());

        currentVersion = Version.parseVersion(versionString, VERSION_DELIMITER);

        return currentVersion;
    }

    private void download() {
        try {
            File                 downloadFilename = getDownloadFile();
            BufferedOutputStream os               = new BufferedOutputStream(new FileOutputStream(downloadFilename));

            HttpUtil.write(new URL(getDownloadUrl()), os);

            MessageDisplayer.information(null, "UpdateDownload.Info.Success", downloadFilename);
        } catch (Exception ex) {
            AppLog.logSevere(UpdateDownload.class, ex);
        }
    }

    private String getDownloadUrl() {
        return isWindows() ? URL_WIN_INSTALLER : URL_JAR;
    }

    private File getDownloadFile() {
        String dirname  = UserSettings.INSTANCE.getSettingsDirectoryName() + File.separator;
        String filename = isWindows() ? FILENAME_WINDOWS : FILENAME_JAR;

        return new File(dirname + File.separator + filename);
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    private void startProgressBar() {
        progressBar = ProgressBar.INSTANCE.getResource(this);
        if (progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(true);
            progressBar.setString(Bundle.getString("UpdateDownload.Info.ProgressBar"));
        }
    }

    private void progressBarDownloadInfo() {
        if (progressBar != null) {
            progressBar.setString(Bundle.getString("UpdateDownload.Info.ProgressBarDownload"));
        }
    }

    private void stopProgressBar() {
        if (progressBar != null) {
            progressBar.setIndeterminate(false);
            progressBar.setString("");
            progressBar.setStringPainted(false);
            ProgressBar.INSTANCE.releaseResource(this);
        }
    }

    private boolean hasNewerVersion() throws Exception {
        netVersion = NetVersion.getOverHttp(URL_CHECK, VERSION_DELIMITER);
        return currentVersion().compareTo(netVersion) < 0;
    }
}
