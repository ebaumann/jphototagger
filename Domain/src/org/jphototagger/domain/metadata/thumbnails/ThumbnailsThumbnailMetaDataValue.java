package org.jphototagger.domain.metadata.thumbnails;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsThumbnailMetaDataValue extends MetaDataValue {

    public static final ThumbnailsThumbnailMetaDataValue INSTANCE = new ThumbnailsThumbnailMetaDataValue();

    private ThumbnailsThumbnailMetaDataValue() {
        super("thumbnail", "files", ValueType.BINARY);
        setDescription(Bundle.getString(ThumbnailsThumbnailMetaDataValue.class, "ThumbnailsThumbnailMetaDataValue.Description"));
    }
}
