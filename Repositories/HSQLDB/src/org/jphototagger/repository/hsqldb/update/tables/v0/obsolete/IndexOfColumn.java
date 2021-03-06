package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

/**
 * @author Elmar Baumann
 */
public final class IndexOfColumn {

    private final String tableName;
    private final String columnName;
    private final String indexName;
    private final boolean unique;

    public IndexOfColumn(String tableName, String columnName, String indexName, boolean unique) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.indexName = indexName;
        this.unique = unique;
    }

    public String getSql() {
        return "CREATE" + (unique
                ? " UNIQUE INDEX "
                : " INDEX ") + indexName + " ON " + tableName + " (" + columnName + ")";
    }
}
