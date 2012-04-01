package org.jphototagger.duplicatefinder;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.applifecycle.AppUpdater;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppUpdater.class)
public final class FileCheckSumUpdater implements AppUpdater, Cancelable {

    private static final Logger LOGGER = Logger.getLogger(FileCheckSumUpdater.class.getName());
    private static final String KEY_UPDATED = "FileChecksumUpdater.Updated";
    private final ApplicationPropertiesRepository appRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
    private final ImageFilesRepository imageFilesRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private volatile boolean cancel;

    @Override
    public void updateToVersion(int major, int minor1, int minor2) {
        if (appRepo.existsKey(KEY_UPDATED)) {
            return;
        }
        updateCheckSum();
    }

    private void updateCheckSum() {
        LOGGER.info("Updating file checksums");
        ProgressHandle progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
        int countToUpdate = (int) imageFilesRepo.getFileCount();
        ProgressEvent progressEvent = createStartedProgressEvent(countToUpdate);
        progressHandle.progressStarted(progressEvent);
        try {
            Updater updater = new Updater(progressHandle, countToUpdate);
            imageFilesRepo.eachImage(updater);
            if (updater.getCountUpdated() == countToUpdate) {
                appRepo.setBoolean(KEY_UPDATED, true);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            progressHandle.progressEnded();
        }
    }

    private class Updater implements Functor<File> {

        private final ProgressHandle progressHandle;
        private final ProgressEvent progressEvent;
        private int countUpdated;

        private Updater(ProgressHandle progressHandle, int countToUpdate) {
            this.progressHandle = progressHandle;
            progressEvent = createStartedProgressEvent(countToUpdate);
        }

        @Override
        public void execute(File file) {
            if (!cancel) {
                updateCheckSum(file);
                countUpdated++;
                progressEvent.setValue(countUpdated);
            }
        }

        private void updateCheckSum(File file) {
            try {
                if (file.exists()) {
                    LOGGER.log(Level.INFO, "Updating checksum of file {0}", file);
                    String checkSum = FileUtil.getMd5HexOfFileContent(file);
                    imageFilesRepo.updateCheckSum(file, checkSum);
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, null, t);
            }
        }

        public int getCountUpdated() {
            return countUpdated;
        }
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    private ProgressEvent createStartedProgressEvent(int fileCount) {
        return new ProgressEvent.Builder()
                .minimum(0)
                .maximum(fileCount)
                .value(0)
                .stringPainted(true)
                .stringToPaint(Bundle.getString(FileCheckSumUpdater.class, "FileCheckSumUpdater.ProgressString", fileCount))
                .build();
    }
}
