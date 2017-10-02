package org.jphototagger.program.app.update;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.concurrent.CancelRequest;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.net.NetVersion;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.Version;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.openide.util.Lookup;

/**
 * Checks for newer versions of JPhotoTagger and downloads them depending.
 *
 * @author Elmar Baumann
 */
public final class UpdateDownload extends Thread implements CancelRequest, Cancelable {

    private static final String DOWNLOAD_PAGE = "http://www.jphototagger.org/download.html";
    private static final String URL_VERSION_CHECK_FILE = "http://www.jphototagger.org/jphototagger-version.txt";
    private static final String VERSION_DELIMITER = ".";
    private static final Preferences PREFS = Lookup.getDefault().lookup(Preferences.class);
    private final Version currentVersion = Version.parseVersion(AppInfo.APP_VERSION, VERSION_DELIMITER);
    private Version netVersion = currentVersion;
    private volatile boolean cancel;
    private static boolean checkPending;
    private final Object source = this;
    private static final Logger LOGGER = Logger.getLogger(UpdateDownload.class.getName());
    private ProgressHandle progressHandle;

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
            } catch (Throwable t) {
                Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private static void setCheckForUpdates(boolean auto) {
        PREFS.setBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES, auto);
    }

    @Override
    public void run() {
        try {
            startProgressHandle();
            netVersion = NetVersion.getOverHttp(URL_VERSION_CHECK_FILE, VERSION_DELIMITER);
            if (hasNewerVersion()) {
                String message = Bundle.getString(UpdateDownload.class, "UpdateDownload.Confirm.Download", currentVersion.toString3(), netVersion.toString3(), DOWNLOAD_PAGE);
                if (MessageDisplayer.confirmYesNo(null, message)) {
                    DesktopUtil.browse(DOWNLOAD_PAGE, "JPhotoTagger");
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "The most recent version of JPhotoTagger couldn''t be retrieved: {0}", t.getLocalizedMessage());
        } finally {
            synchronized (UpdateDownload.class) {
                progressHandle.progressEnded();
                checkPending = false;
            }
        }
    }

    private void startProgressHandle() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(source)
                .indeterminate(true)
                .stringPainted(true)
                .stringToPaint(Bundle.getString(UpdateDownload.class, "UpdateDownload.Info.ProgressBar"))
                .build();
        synchronized (UpdateDownload.class) {
        progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
        progressHandle.progressStarted(evt);
    }
    }

    private void progressBarDownloadInfo() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(source)
                .indeterminate(true)
                .stringPainted(true)
                .stringToPaint(Bundle.getString(UpdateDownload.class, "UpdateDownload.Info.ProgressBarDownload"))
                .build();
        synchronized (UpdateDownload.class) {
            progressHandle.progressPerformed(evt);
    }
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
