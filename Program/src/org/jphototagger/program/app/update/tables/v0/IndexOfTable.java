package org.jphototagger.program.app.update.tables.v0;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class IndexOfTable {

    private final String indexName;
    private final String tableName;

    public IndexOfTable(String indexName, String tableName) {
        if (indexName == null) {
            throw new NullPointerException("indexName == null");
        }
        if (tableName == null) {
            throw new NullPointerException("tableName == null");
        }

        this.indexName = indexName;
        this.tableName = tableName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof IndexOfTable)) {
            return false;
        }

        IndexOfTable other = (IndexOfTable) obj;

        return indexName.equals(other.indexName) && tableName.equals(other.tableName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.indexName != null ? this.indexName.hashCode() : 0);
        hash = 59 * hash + (this.tableName != null ? this.tableName.hashCode() : 0);
        return hash;
    }
}
