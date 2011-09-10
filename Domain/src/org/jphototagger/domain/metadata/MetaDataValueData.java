package org.jphototagger.domain.metadata;

/**
 *
 * @author Elmar Baumann
 */
public final class MetaDataValueData {

    private final MetaDataValue metaDataValue;
    private final Object data;

    public MetaDataValueData(MetaDataValue metaDataValue, Object data) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        this.metaDataValue = metaDataValue;
        this.data = data;
    }

    public MetaDataValue getMetaDataValue() {
        return metaDataValue;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return metaDataValue + "=" + data;
    }
}
