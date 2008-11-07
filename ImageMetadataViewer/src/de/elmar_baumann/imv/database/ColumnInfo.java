package de.elmar_baumann.imv.database;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
class ColumnInfo {

    public String tableName;
    public String columnName;
    public String dataType;
    public IndexOfColumn index;

    public ColumnInfo(String tableName, String columnName, String dataType, IndexOfColumn index) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
        this.index = index;
    }
}
