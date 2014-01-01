package org.jphototagger.domain.metadata;

import org.jphototagger.lib.util.ObjectUtil;

/**
 * @author Elmar Baumann
 */
public final class MetaDataStringValue {

    private final MetaDataValue metaDataValue;
    private final String value;

    public MetaDataStringValue(MetaDataValue metaDataValue, String value) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        this.metaDataValue = metaDataValue;
        this.value = value;
    }

    public MetaDataValue getMetaDataValue() {
        return metaDataValue;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof MetaDataStringValue)) {
            return false;
        }

        MetaDataStringValue other = (MetaDataStringValue) obj;

        return metaDataValue.equals(other.metaDataValue) && ObjectUtil.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.metaDataValue != null ? this.metaDataValue.hashCode() : 0);
        hash = 73 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
