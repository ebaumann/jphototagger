package org.jphototagger.program.settings;

import java.io.File;
import org.jphototagger.domain.thumbnails.ThumbnailsDirectoryProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsDirectoryProvider.class)
public final class ThumbnailsDirectoryProviderImpl implements ThumbnailsDirectoryProvider {

    private final String thumbnailsDirectoryName = UserPreferences.INSTANCE.getThumbnailsDirectoryName();

    @Override
    public File getThumbnailsDirectory() {
        return new File(thumbnailsDirectoryName);
    }
}
