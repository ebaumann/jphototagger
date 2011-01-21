package org.jphototagger.program.data;

import org.jphototagger.program.database.metadata.Column;

/**
 * Data of a column.
 *
 * @author Elmar Baumann
 */
public final class ColumnData {
    private final Column column;
    private final Object data;

    public ColumnData(Column column, Object data) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        this.column = column;
        this.data   = data;
    }

    public Column getColumn() {
        return column;
    }

    public Object getData() {
        return data;
    }
}
