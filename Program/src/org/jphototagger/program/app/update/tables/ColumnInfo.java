package org.jphototagger.program.app.update.tables;

/**
 *
 *
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

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @return the index
     */
    public IndexOfColumn getIndex() {
        return index;
    }
}
