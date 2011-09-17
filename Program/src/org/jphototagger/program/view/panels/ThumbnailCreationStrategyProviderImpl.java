package org.jphototagger.program.view.panels;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.storage.Preferences;
import org.jphototagger.program.app.AppStorageKeys;
import org.jphototagger.program.image.thumbnail.ThumbnailCreationStrategy;
import org.jphototagger.program.image.thumbnail.ThumbnailCreationStrategyProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreationStrategyProvider.class)
public class ThumbnailCreationStrategyProviderImpl implements ThumbnailCreationStrategyProvider {

    private final Preferences storage = Lookup.getDefault().lookup(Preferences.class);

    @Override
    public ThumbnailCreationStrategy getThumbnailCreationStrategy() {

        return storage.containsKey(AppStorageKeys.KEY_THUMBNAIL_CREATION_CREATOR)
                ? ThumbnailCreationStrategy.valueOf(storage.getString(AppStorageKeys.KEY_THUMBNAIL_CREATION_CREATOR))
                : ThumbnailCreationStrategy.JAVA_IMAGE_IO;
    }
}
