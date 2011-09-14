package org.jphototagger.program.view.panels;

import org.jphototagger.api.core.Storage;
import org.jphototagger.program.image.thumbnail.ThumbnailCreationStrategy;
import org.jphototagger.program.image.thumbnail.ThumbnailCreationStrategyProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreationStrategyProvider.class)
public class ThumbnailCreationStrategyProviderImpl implements ThumbnailCreationStrategyProvider {

    @Override
    public ThumbnailCreationStrategy getThumbnailCreationStrategy() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_THUMBNAIL_CREATOR)
                ? ThumbnailCreationStrategy.valueOf(storage.getString(Storage.KEY_THUMBNAIL_CREATOR))
                : ThumbnailCreationStrategy.JAVA_IMAGE_IO;
    }
}
