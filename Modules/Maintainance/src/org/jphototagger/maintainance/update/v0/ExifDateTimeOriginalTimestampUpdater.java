package org.jphototagger.maintainance.update.v0;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.applifecycle.AppUpdater;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.maintainance.RefreshExifOfKnownFilesInRepository;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppUpdater.class)
public final class ExifDateTimeOriginalTimestampUpdater implements AppUpdater, ProgressListener {

    private static final String KEY_UPDATED = "ExifDateTimeOriginalTimestampUpdater.Updated";
    private final ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
    private final RefreshExifOfKnownFilesInRepository refreshExifTask = new RefreshExifOfKnownFilesInRepository();

    @Override
    public void updateToVersion(int major, int minor1, int minor2) {
        if (repo.existsKey(KEY_UPDATED)) {
            return;
        }
        Logger.getLogger(ExifDateTimeOriginalTimestampUpdater.class.getName()).log(Level.INFO,
                "Updating database for all known images with the EXIF time of day");
        refreshExifTask.addProgressListener(this);
        refreshExifTask.start();
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        // ignore
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        // ignore
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (!refreshExifTask.isCanceled()) {
            repo.setBoolean(KEY_UPDATED, true);
        }

        refreshExifTask.removeProgressListener(this);
    }
}
