package org.jphototagger.image.thumbnail;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.image.ImagePreferencesKeys;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreationStrategyProvider.class)
public class ThumbnailCreationStrategyProviderImpl implements ThumbnailCreationStrategyProvider {

    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

    @Override
    public ThumbnailCreationStrategy getThumbnailCreationStrategy() {
        String value = ensureValidValue(prefs.getString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_CREATOR));
        return prefs.containsKey(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_CREATOR)
                ? ThumbnailCreationStrategy.valueOf(value)
                : ThumbnailCreationStrategy.JPHOTOTAGGER;
    }

    private String ensureValidValue(String value) {
        for (ThumbnailCreationStrategy strategy : ThumbnailCreationStrategy.values()) {
            String valueName = strategy.name();
            if (valueName.equals(value)) {
                return value;
            }
        }
        return ThumbnailCreationStrategy.JPHOTOTAGGER.name();
    }
}
