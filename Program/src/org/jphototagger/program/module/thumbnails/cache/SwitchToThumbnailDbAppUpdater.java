package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jphototagger.api.applifecycle.AppUpdater;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsDirectoryProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppUpdater.class)
public final class SwitchToThumbnailDbAppUpdater implements AppUpdater {

    private static final String UPDATE_KEY = "SwitchToThumbnailDb.Switched";

    @Override
    public void updateToVersion(int major, int minor1, int minor2) {
        if (isUpdate()) {
            Logger.getLogger(SwitchToThumbnailDbAppUpdater.class.getName()).log(Level.INFO, "Start updating Thumbnails from file system to database");
            update();
            Logger.getLogger(SwitchToThumbnailDbAppUpdater.class.getName()).log(Level.INFO, "Finished updating Thumbnails from file system to database");
        }
    }

    private boolean isUpdate() {
        ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        return !repo.existsKey(UPDATE_KEY);
    }

    private void update() {
        ImageFilesRepository imageFilesRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);
        long imageFileCount = imageFilesRepo.getFileCount();
        Logger.getLogger(SwitchToThumbnailDbAppUpdater.class.getName()).log(Level.INFO, "Processing {0} image files", imageFileCount);
        showUpdateInfo(imageFileCount);
        ProgressHandle progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle();
        ProgressEvent progressEvent = createProgressStartedEvent((int) imageFileCount);
        progressHandle.progressStarted(progressEvent);
        ThumbnailUpdateFunctor updater = new ThumbnailUpdateFunctor(progressHandle, progressEvent);
        try {
            imageFilesRepo.eachImage(updater);
        } catch (Throwable t) {
            Logger.getLogger(SwitchToThumbnailDbAppUpdater.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            progressHandle.progressEnded();
        }
        if (updater.updateFinished) {
            updateFinished();
        }
    }

    private void showUpdateInfo(final long imageFileCount) {
        if (imageFileCount > 300) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    MessageDisplayer.information(null, Bundle.getString(SwitchToThumbnailDbAppUpdater.class, "SwitchToThumbnailDbAppUpdater.UpdateInfo", imageFileCount));
                }
            });
        }
    }

    private final class ThumbnailUpdateFunctor implements Functor<File> {

        private final ProgressHandle progressHandle;
        private final ProgressEvent progressEvent;
        private int filesProcessedCount;
        private boolean updateFinished;

        private ThumbnailUpdateFunctor(ProgressHandle progressHandle, ProgressEvent progressEvent) {
            this.progressHandle = progressHandle;
            this.progressEvent = progressEvent;
        }

        @Override
        public void execute(File t) {
              thumbnailToDatabase(t);
              filesProcessedCount++;
              updateFinished = filesProcessedCount >= progressEvent.getMaximum();
              progressEvent.setValue(filesProcessedCount);
              progressEvent.setStringToPaint(getProgressMessage(filesProcessedCount, progressEvent.getMaximum()));
              progressHandle.progressPerformed(progressEvent);
        }
    }

    private void updateFinished() {
        ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        repo.setBoolean(UPDATE_KEY, true);
    }

    private void thumbnailToDatabase(File imageFile) {
        Image thumbnail = PersistentThumbnails.getThumbnail(imageFile);
        if (thumbnail != null) {
            try {
                ThumbnailsDb.insertThumbnail(thumbnail, imageFile);
            } catch (Throwable t) {
                Logger.getLogger(SwitchToThumbnailDbAppUpdater.class.getName()).log(Level.SEVERE, null, t);
            } finally {
                PersistentThumbnails.deleteThumbnail(imageFile);
            }
        }
    }

    private ProgressEvent createProgressStartedEvent(int fileCount) {
        return new ProgressEvent.Builder()
                .indeterminate(false)
                .minimum(0)
                .maximum(fileCount)
                .stringPainted(true)
                .stringToPaint(getProgressMessage(0, fileCount))
                .build();
    }

    private String getProgressMessage(int filesProcessedCount, int fileCount) {
        return Bundle.getString(SwitchToThumbnailDbAppUpdater.class, "SwitchToThumbnailDbAppUpdater.ProgressMessage", filesProcessedCount, fileCount);
    }

    /**
     * Persistent stored (cached) thumbnails.
     *
     * @author Martin Pohlack, Elmar Baumann
     */
    private static final class PersistentThumbnails {

        private static final Logger LOGGER = Logger.getLogger(PersistentThumbnails.class.getName());
        private static final String THUMBNAILS_DIRECTORY_NAME;

        static {
            ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
            File thumbnailsDirectory = provider.getThumbnailsDirectory();
            THUMBNAILS_DIRECTORY_NAME = thumbnailsDirectory.getAbsolutePath();
        }

        private PersistentThumbnails() {
        }

        static boolean deleteThumbnail(File imageFile) {
            if (imageFile == null) {
                throw new NullPointerException("imageFile == null");
            }
            File tnFile = getThumbnailFile(imageFile);
            if ((tnFile != null) && !tnFile.delete()) {
                LOGGER.log(Level.WARNING,
                        "Thumbnail ''{0}'' of image file ''{1}'' couldn''t be deleted!",
                        new Object[]{tnFile, imageFile});
                return false;
            }

            return true;
        }

        private static Image getThumbnail(String md5Filename) {
            Image thumbnail = null;
            FileInputStream fis = null;
            try {
                File tnFile = getThumbnailfile(md5Filename);
                if (tnFile == null) {
                    return null;
                }
                if (tnFile.exists()) {
                    fis = new FileInputStream(tnFile);
                    int bytecount = fis.available();
                    byte[] bytes = new byte[bytecount];
                    fis.read(bytes, 0, bytecount);
                    ImageIcon icon = new ImageIcon(bytes);
                    thumbnail = icon.getImage();
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, null, t);
            } finally {
                IoUtil.close(fis);
            }
            return thumbnail;
        }

        static Image getThumbnail(File imageFile) {
            if (imageFile == null) {
                throw new NullPointerException("imageFile == null");
            }
            String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);
            return getThumbnail(md5Filename);
        }

        private static File getThumbnailFile(File imageFile) {
            if (imageFile == null) {
                throw new NullPointerException("imageFile == null");
            }
            String md5Filename = FileUtil.getMd5FilenameOfAbsolutePath(imageFile);
            return (md5Filename == null)
                    ? null
                    : getThumbnailfile(md5Filename);
        }

        /**
         * Compute final name of thumbnail on disk.
         *
         * @param md5Filename hash
         * @return thumbnail file
         */
        private static File getThumbnailfile(String md5Filename) {
            return new File(THUMBNAILS_DIRECTORY_NAME + File.separator + md5Filename + ".jpeg");
        }
    }
}
