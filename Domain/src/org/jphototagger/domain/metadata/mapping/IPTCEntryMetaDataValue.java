package org.jphototagger.domain.metadata.mapping;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.ObjectUtil;

import com.imagero.reader.iptc.IPTCEntryMeta;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class IPTCEntryMetaDataValue {

    private final IPTCEntryMeta iptcEntryMeta;
    private final MetaDataValue metaDataValue;

    public IPTCEntryMetaDataValue(IPTCEntryMeta iptcEntryMeta, MetaDataValue metaDataValue) {
        if (iptcEntryMeta == null) {
            throw new NullPointerException("iptcEntryMeta == null");
        }

        this.iptcEntryMeta = iptcEntryMeta;
        this.metaDataValue = metaDataValue;
    }

    public IPTCEntryMeta getIptcEntryMeta() {
        return iptcEntryMeta;
    }

    public MetaDataValue getMetaDataValue() {
        return metaDataValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof IPTCEntryMetaDataValue)) {
            return false;
        }

        IPTCEntryMetaDataValue other = (IPTCEntryMetaDataValue) obj;

        return iptcEntryMeta.equals(other.iptcEntryMeta) && ObjectUtil.equals(metaDataValue, other.metaDataValue);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.iptcEntryMeta != null ? this.iptcEntryMeta.hashCode() : 0);
        hash = 71 * hash + (this.metaDataValue != null ? this.metaDataValue.hashCode() : 0);
        return hash;
    }
}
