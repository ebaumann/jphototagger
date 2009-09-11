package de.elmar_baumann.imv.app.update.tables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Information about an table index.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-11
 */
public class IndexInfo {

    private final String tableName;
    private final String indexName;
    private final boolean unique;
    private final List<String> columnNames = new ArrayList<String>();

    public IndexInfo(
            boolean unique,
            String indexName,
            String tableName,
            String columnName,
            String... columnNames) {
        this.unique = unique;
        this.indexName = indexName;
        this.tableName = tableName;
        this.columnNames.add(columnName);
        this.columnNames.addAll(Arrays.asList(columnNames));
    }

    public String sql() {
        return "CREATE" +
                (unique
                ? " UNIQUE"
                : "") +
                " INDEX " + indexName +
                " ON " + tableName + getColumnsClause();
    }

    private String getColumnsClause() {
        StringBuilder sb = new StringBuilder(" (");
        int i = 0;
        for (String columnName : columnNames) {
            sb.append((i++ == 0
                    ? ""
                    : ", ") + columnName);
        }
        sb.append(")");
        return sb.toString();
    }
}
