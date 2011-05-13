package org.jphototagger.program.app.update;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.net.CancelRequest;
import org.jphototagger.lib.net.HttpUtil;
import org.jphototagger.lib.net.NetVersion;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseApplicationProperties;
import org.jphototagger.program.helper.FinalExecutable;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ProgressBar;
import java.awt.EventQueue;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import javax.swing.JProgressBar;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Checks for newer versions of JPhotoTagger and downloads them depending
 * on {@link UserSettings#isAutoDownloadNewerVersions()}.
 *
 * @author Elmar Baumann
 */
public final class UpdateDownload extends Thread implements CancelRequest, Cancelable {
    private static final String FILENAME_WINDOWS = "JPhotoTagger-Setup.exe";
    private static final String FILENAME_ZIP = "JPhotoTagger.zip";
    private static final String URL_VERSION_CHECK_FILE = "http://www.jphototagger.org/jphototagger-version.txt";
    private static final String URL_WIN_INSTALLER = "http://www.jphototagger.org/dist/JPhotoTagger-setup.exe";
    private static final String URL_ZIP = "http://www.jphototagger.org/dist/JPhotoTagger.zip";
    private static final String VERSION_DELIMITER = ".";
    private Version currentVersion;
    private Version netVersion;
    private JProgressBar progressBar;
    private volatile boolean cancel;
    private static boolean checkPending;
    private final Object pBarOwner = this;

    public UpdateDownload() {
        super("JPhotoTagger: Checking for and downloading newer version");
    }

    /**
     * Returns whether a check is Pending.
     *
     * @return true if a check is pending
     */
    public static boolean isCheckPending() {
        synchronized (UpdateDownload.class) {
            return checkPending;
        }
    }

    /**
     * Checks for a newer version if not {@link #isCheckPending()}.
     */
    public static void checkForNewerVersion() {
        synchronized (UpdateDownload.class) {
            if (checkPending) {
                return;
            }

            checkPending = true;
        }

        new UpdateDownload().start();
    }

    private static final String KEY_ASK_ONCE_CHECK_FOR_NEWER_VERSION = "UpdateDownload.CheckForNewerVersion";

    /**
     * Asks via a confirmation dialog exactly once whether to check
     * automatically for updates.
     * <p>
     * Once asked, the ask property in {@link DatabaseApplicationProperties} is
     * set to true and calling this method again does nothing.
     * <p>
     * The answer will be stored in
     * {@link UserSettings#setAutoDownloadNewerVersions(boolean)}-
     */
    public static void askOnceCheckForNewerVersion() {
        if (!DatabaseApplicationProperties.INSTANCE.getBoolean(KEY_ASK_ONCE_CHECK_FOR_NEWER_VERSION)) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        UserSettings.INSTANCE.setAutoDownloadNewerVersions(MessageDisplayer.confirmYesNo(null,
                                "UpdateDownload.Confirm.CheckForNewerVersion"));
                        DatabaseApplicationProperties.INSTANCE.setBoolean(KEY_ASK_ONCE_CHECK_FOR_NEWER_VERSION, true);
                    }
                });
            } catch (Exception ex) {
                AppLogger.logSevere(UpdateDownload.class, ex);
            }
        }
    }

    @Override
    public void run() {
        startProgressBar();

        try {
            if (hasNewerVersion()
                    && MessageDisplayer.confirmYesNo(null, "UpdateDownload.Confirm.Download",
                        currentVersion.toString3(), netVersion.toString3())) {
                progressBarDownloadInfo();
                download();

                File downloadFile = getDownloadFile();

                if (cancel && downloadFile.exists()) {
                    if (!downloadFile.delete()) {
                        AppLogger.logWarning(getClass(), "UpdateDownload.Error.DeleteDownloadFile", downloadFile);
                    }
                }
            }
        } catch (Exception ex) {
            AppLogger.logInfo(UpdateDownload.class, "UpdateDownload.Error.Compare", ex.getLocalizedMessage());
        } finally {
            releaseProgressBar();

            synchronized (UpdateDownload.class) {
                checkPending = false;
            }
        }
    }

    private Version currentVersion() {
        currentVersion = Version.parseVersion(AppInfo.APP_VERSION, VERSION_DELIMITER);

        return currentVersion;
    }

    private void download() {
        try {
            File downloadFile = getDownloadFile();
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(downloadFile));

            HttpUtil.write(new URL(getDownloadUrl()), os, this);

            if (cancel) {
                return;
            }

            if (SystemUtil.isWindows()) {
                setFinalExecutable(downloadFile);
            } else {
                MessageDisplayer.information(null, "UpdateDownload.Info.Success", downloadFile);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(UpdateDownload.class, ex);
        }
    }

    private void setFinalExecutable(File downloadFile) {
        if (MessageDisplayer.confirmYesNo(null, "UpdateDownload.Confirm.SetFinalExecutable", downloadFile)) {
            FinalExecutable exec = new FinalExecutable(downloadFile.getAbsolutePath());

            AppLifeCycle.INSTANCE.addFinalTask(exec);
        }
    }

    private String getDownloadUrl() {
        return SystemUtil.isWindows()
               ? URL_WIN_INSTALLER
               : URL_ZIP;
    }

    private File getDownloadFile() {
        String dirname = UserSettings.INSTANCE.getSettingsDirectoryName() + File.separator;
        String filename = SystemUtil.isWindows()
                          ? FILENAME_WINDOWS
                          : FILENAME_ZIP;

        return new File(dirname + File.separator + filename);
    }

    private void startProgressBar() {
        progressBar = ProgressBar.INSTANCE.getResource(pBarOwner);
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setIndeterminate(true);
                    progressBar.setStringPainted(true);
                    progressBar.setString(JptBundle.INSTANCE.getString("UpdateDownload.Info.ProgressBar"));
                }
            }
        });
    }

    private void progressBarDownloadInfo() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setString(JptBundle.INSTANCE.getString("UpdateDownload.Info.ProgressBarDownload"));
                }
            }
        });
    }

    private void releaseProgressBar() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setIndeterminate(false);
                    progressBar.setString("");
                    progressBar.setStringPainted(false);
                    ProgressBar.INSTANCE.releaseResource(pBarOwner);
                    progressBar = null;
                }
            }
        });
    }

    private boolean hasNewerVersion() throws Exception {
        netVersion = NetVersion.getOverHttp(URL_VERSION_CHECK_FILE, VERSION_DELIMITER);

        return currentVersion().compareTo(netVersion) < 0;
    }

    @Override
    public boolean isCancel() {
        return cancel;
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
