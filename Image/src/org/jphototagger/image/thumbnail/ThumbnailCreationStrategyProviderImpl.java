package org.jphototagger.image.thumbnail;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.image.ImagePreferencesKeys;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreationStrategyProvider.class)
public class ThumbnailCreationStrategyProviderImpl implements ThumbnailCreationStrategyProvider {

    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

    @Override
    public ThumbnailCreationStrategy getThumbnailCreationStrategy() {

        return prefs.containsKey(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_CREATOR)
                ? ThumbnailCreationStrategy.valueOf(prefs.getString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_CREATOR))
                : ThumbnailCreationStrategy.JAVA_IMAGE_IO;
    }
}
