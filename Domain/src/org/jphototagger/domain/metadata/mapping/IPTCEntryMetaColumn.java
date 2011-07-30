package org.jphototagger.domain.metadata.mapping;

import org.jphototagger.domain.database.Column;

import com.imagero.reader.iptc.IPTCEntryMeta;
import org.jphototagger.lib.util.ObjectUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class IPTCEntryMetaColumn {

    private final IPTCEntryMeta iptcEntryMeta;
    private final Column column;

    public IPTCEntryMetaColumn(IPTCEntryMeta iptcEntryMeta, Column column) {
        if (iptcEntryMeta == null) {
            throw new NullPointerException("iptcEntryMeta == null");
        }

        this.iptcEntryMeta = iptcEntryMeta;
        this.column = column;
    }

    public IPTCEntryMeta getIptcEntryMeta() {
        return iptcEntryMeta;
    }

    public Column getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof IPTCEntryMetaColumn)) {
            return false;
        }

        IPTCEntryMetaColumn other = (IPTCEntryMetaColumn) obj;

        return iptcEntryMeta.equals(other.iptcEntryMeta) && ObjectUtil.equals(column, other.column);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.iptcEntryMeta != null ? this.iptcEntryMeta.hashCode() : 0);
        hash = 71 * hash + (this.column != null ? this.column.hashCode() : 0);
        return hash;
    }
}
