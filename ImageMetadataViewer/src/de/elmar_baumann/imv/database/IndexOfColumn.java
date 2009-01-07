package de.elmar_baumann.imv.database;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public final class IndexOfColumn {

    private final String tableName;
    private final String columnName;
    private final String indexName;
    private final boolean unique;

    IndexOfColumn(String tableName, String columnName, String indexName, boolean unique) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.indexName = indexName;
        this.unique = unique;
    }

    String getSql() {
        return "CREATE" + // NOI18N
            (unique ? " UNIQUE INDEX " : " INDEX ") + // NOI18N
            indexName + " ON " + tableName + // NOI18N
            " (" + columnName + ")"; // NOI18N
    }
}
