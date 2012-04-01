package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

/**
 * @author Elmar Baumann
 */
public final class ColumnInfo {

    private final String tableName;
    private final String columnName;
    private final String dataType;
    private final IndexOfColumn index;

    public ColumnInfo(String tableName, String columnName, String dataType, IndexOfColumn index) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
        this.index = index;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public IndexOfColumn getIndex() {
        return index;
    }
}
