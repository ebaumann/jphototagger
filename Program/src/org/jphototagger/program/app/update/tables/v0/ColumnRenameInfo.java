package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.update.tables.ColumnInfo;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ColumnRenameInfo {

    private final ColumnInfo oldColumnInfo;
    private final ColumnInfo newColumnInfo;

    public ColumnRenameInfo(ColumnInfo oldColumnInfo, ColumnInfo newColumnInfo) {
        if (oldColumnInfo == null) {
            throw new NullPointerException("oldColumnInfo == null");
        }
        if (newColumnInfo == null) {
            throw new NullPointerException("newColumnInfo == null");
        }

        this.oldColumnInfo = oldColumnInfo;
        this.newColumnInfo = newColumnInfo;
    }

    public ColumnInfo getOldColumnInfo() {
        return oldColumnInfo;
    }

    public ColumnInfo getNewColumnInfo() {
        return newColumnInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColumnRenameInfo)) {
            return false;
        }
        ColumnRenameInfo other = (ColumnRenameInfo) obj;
        return oldColumnInfo.equals(other.oldColumnInfo) && newColumnInfo.equals(other.newColumnInfo);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.oldColumnInfo != null ? this.oldColumnInfo.hashCode() : 0);
        hash = 61 * hash + (this.newColumnInfo != null ? this.newColumnInfo.hashCode() : 0);
        return hash;
    }
}
