package org.jphototagger.domain.database;

import org.jphototagger.lib.util.ObjectUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ColumnStringValue {

    private final Column column;
    private final String value;

    public ColumnStringValue(Column column, String value) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        this.column = column;
        this.value = value;
    }

    public Column getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ColumnStringValue)) {
            return false;
        }

        ColumnStringValue other = (ColumnStringValue) obj;

        return column.equals(other.column) && ObjectUtil.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.column != null ? this.column.hashCode() : 0);
        hash = 73 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
