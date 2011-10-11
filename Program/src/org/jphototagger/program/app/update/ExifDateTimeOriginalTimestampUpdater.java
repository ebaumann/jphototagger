package org.jphototagger.program.app.update;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.api.startup.AppUpdater;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.maintainance.RefreshExifInDbOfKnownFiles;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppUpdater.class)
public final class ExifDateTimeOriginalTimestampUpdater implements AppUpdater, ProgressListener {

    private static final String KEY_UPDATED = "ExifDateTimeOriginalTimestampUpdater.Updated";
    private final ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
    private final RefreshExifInDbOfKnownFiles refreshExifTask = new RefreshExifInDbOfKnownFiles();

    @Override
    public void updateToVersion(int major, int minor1, int minor2) {
        if (repo.existsKey(KEY_UPDATED)) {
            return;
        }

        String message = Bundle.getString(ExifDateTimeOriginalTimestampUpdater.class, "ExifDateTimeOriginalTimestampUpdater.Info");
        MessageDisplayer.information(ComponentUtil.getFrameWithIcon(), message);
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
