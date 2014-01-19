package org.jphototagger.exif.cache;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.applifecycle.AppUpdater;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppUpdater.class)
public final class SwitchToExifCacheDbAppUpdater implements AppUpdater {

    private static final String UPDATE_KEY = "SwitchToExifCacheDbAppUpdater.Switched";

    @Override
    public void updateToVersion(int major, int minor1, int minor2) {
        if (isUpdate()) {
            Logger.getLogger(SwitchToExifCacheDbAppUpdater.class.getName()).log(Level.INFO, "Start updating EXIF cache from file system to database");
            update();
            Logger.getLogger(SwitchToExifCacheDbAppUpdater.class.getName()).log(Level.INFO, "Finished updating EXIF cache from file system to database");
        }
    }

    private boolean isUpdate() {
        ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        return !repo.existsKey(UPDATE_KEY);
    }

    private void update() {
        int delCount = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(ExifCache.INSTANCE.getCacheDir().toPath(), "*.xml")) {
            for (Path path : directoryStream) {
                try {
                    Logger.getLogger(SwitchToExifCacheDbAppUpdater.class.getName()).log(Level.INFO, "Deleting EXIF cache file ''{0}''", path);
                    Files.delete(path);
                } catch (IOException ex) {
                    Logger.getLogger(SwitchToExifCacheDbAppUpdater.class.getName()).log(Level.WARNING,
                            "Couldn''t delete EXIF cache file ''{0}'': {1}",
                            new Object[]{path, ex.getLocalizedMessage()});
                }
            }
            ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
            repo.setBoolean(UPDATE_KEY, true);
        } catch (Throwable t) {
            Logger.getLogger(SwitchToExifCacheDbAppUpdater.class.getName()).log(Level.SEVERE, null, t);
        }
        Logger.getLogger(SwitchToExifCacheDbAppUpdater.class.getName()).log(Level.INFO, "Deleted {0} EXIF cache files.", delCount);
    }
}
