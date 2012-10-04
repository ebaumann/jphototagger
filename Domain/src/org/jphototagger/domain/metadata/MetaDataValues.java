package org.jphototagger.domain.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jphototagger.domain.metadata.exif.ExifMetaDataValues;
import org.jphototagger.domain.metadata.file.FilesMetaDataValues;
import org.jphototagger.domain.metadata.thumbnails.ThumbnailsThumbnailMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;

/**
 * @author Elmar Baumann
 */
public final class MetaDataValues {

    private static final Set<MetaDataValue> VALUES = new LinkedHashSet<MetaDataValue>();

    static {
        for (MetaDataValue metaDataValue : FilesMetaDataValues.get()) {
            VALUES.add(metaDataValue);
        }
        for (MetaDataValue metaDataValue : XmpMetaDataValues.get()) {
            VALUES.add(metaDataValue);
        }
        for (MetaDataValue metaDataValue : ExifMetaDataValues.get()) {
            VALUES.add(metaDataValue);
        }
        VALUES.add(ThumbnailsThumbnailMetaDataValue.INSTANCE);
    }

    public static Collection<? extends MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(VALUES);
    }

    private MetaDataValues() {
    }
}
