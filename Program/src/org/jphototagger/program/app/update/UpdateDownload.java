package org.jphototagger.program.app.update;

import java.awt.EventQueue;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.CancelRequest;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.lib.net.HttpUtil;
import org.jphototagger.lib.net.NetVersion;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.misc.FinalExecutable;
import org.jphototagger.program.settings.AppPreferencesKeys;

/**
 * Checks for newer versions of JPhotoTagger and downloads them depending.
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
    private final Version currentVersion = Version.parseVersion(AppInfo.APP_VERSION, VERSION_DELIMITER);
    private Version netVersion = currentVersion;
    private volatile boolean cancel;
    private static boolean checkPending;
    private final Object pBarOwner = this;
    private static final Logger LOGGER = Logger.getLogger(UpdateDownload.class.getName());
    private final MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

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
     * Checks for a newer version if not {@code #isCheckPending()}.
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
     */
    public static void askOnceCheckForNewerVersion() {
        final ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);

        if (!appPropertiesRepo.getBoolean(KEY_ASK_ONCE_CHECK_FOR_NEWER_VERSION)) {
            try {
                EventQueue.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        String message = Bundle.getString(UpdateDownload.class, "UpdateDownload.Confirm.CheckForNewerVersion");
                        boolean isAutoDownload = MessageDisplayer.confirmYesNo(null, message);

                        setCheckForUpdates(isAutoDownload);
                        appPropertiesRepo.setBoolean(KEY_ASK_ONCE_CHECK_FOR_NEWER_VERSION, true);
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void setCheckForUpdates(boolean auto) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES, auto);
    }

    @Override
    public void run() {
        startProgressBar();

        try {
            netVersion = NetVersion.getOverHttp(URL_VERSION_CHECK_FILE, VERSION_DELIMITER);
            String message = Bundle.getString(UpdateDownload.class, "UpdateDownload.Confirm.Download", currentVersion.toString3(), netVersion.toString3());
            if (hasNewerVersion() && MessageDisplayer.confirmYesNo(null, message)) {
                progressBarDownloadInfo();
                download();

                File downloadFile = getDownloadFile();

                if (cancel && downloadFile.exists()) {
                    if (!downloadFile.delete()) {
                        LOGGER.log(Level.WARNING, "Uncomplete downloaded file ''{0}'' couldn''t be deleted!", downloadFile);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "The most recent version of JPhotoTagger couldn''t be retrieved: {0}", ex.getLocalizedMessage());
        } finally {
            progressBarProvider.progressEnded(pBarOwner);

            synchronized (UpdateDownload.class) {
                checkPending = false;
            }
        }
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
                String message = Bundle.getString(UpdateDownload.class, "UpdateDownload.Info.Success", downloadFile);
                MessageDisplayer.information(null, message);
            }
        } catch (Exception ex) {
            Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setFinalExecutable(File downloadFile) {
        String message = Bundle.getString(UpdateDownload.class, "UpdateDownload.Confirm.SetFinalExecutable", downloadFile);

        if (MessageDisplayer.confirmYesNo(null, message)) {
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
        PreferencesDirectoryProvider provider = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        String userDirectory = provider.getUserPreferencesDirectory().getAbsolutePath();
        String dirname = userDirectory;
        String filename = SystemUtil.isWindows()
                ? FILENAME_WINDOWS
                : FILENAME_ZIP;

        return new File(dirname + File.separator + filename);
    }

    private void startProgressBar() {
        ProgressEvent evt = new ProgressEvent.Builder().source(pBarOwner).indeterminate(true).stringPainted(true).stringToPaint(Bundle.getString(UpdateDownload.class, "UpdateDownload.Info.ProgressBar")).build();
        progressBarProvider.progressStarted(evt);
    }

    private void progressBarDownloadInfo() {
        ProgressEvent evt = new ProgressEvent.Builder().source(pBarOwner).indeterminate(true).stringPainted(true).stringToPaint(Bundle.getString(UpdateDownload.class, "UpdateDownload.Info.ProgressBarDownload")).build();
        progressBarProvider.progressStarted(evt);
    }

    private boolean hasNewerVersion() throws Exception {
        return currentVersion.compareTo(netVersion) < 0;
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
