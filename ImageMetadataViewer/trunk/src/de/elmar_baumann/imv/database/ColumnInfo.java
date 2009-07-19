package de.elmar_baumann.imv.database;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
final class ColumnInfo {

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
