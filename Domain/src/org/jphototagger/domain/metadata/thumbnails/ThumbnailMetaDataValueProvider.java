package org.jphototagger.domain.metadata.thumbnails;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.MetaDataValueProvider;
import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetaDataValueProvider.class)
public final class ThumbnailMetaDataValueProvider implements MetaDataValueProvider {

    private static final List<MetaDataValue> PROVIDED_META_DATA_VALUES = new LinkedList<>();

    static {
        PROVIDED_META_DATA_VALUES.add(ThumbnailsThumbnailMetaDataValue.INSTANCE);
    }

    @Override
    public Collection<MetaDataValue> getProvidedValues() {
        return new ArrayList<>(PROVIDED_META_DATA_VALUES);
    }

    @Override
    public Collection<MetaDataValueData> getMetaDataForImageFile(File file) {
        List<MetaDataValueData> metaData = new ArrayList<>();
        ThumbnailProvider provider = Lookup.getDefault().lookup(ThumbnailProvider.class);
        Image thumbnail = provider.getThumbnail(file);
        if (thumbnail != null) {
            metaData.add(new MetaDataValueData(ThumbnailsThumbnailMetaDataValue.INSTANCE, thumbnail));
        }
        return metaData;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}
