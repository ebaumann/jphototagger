package org.jphototagger.program.database.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utils für Datenbankmetadaten.
 *
 * @author Elmar Baumann
 */
public final class Util {
    private Util() {}

    /**
     * Returns from a collection of colums lists of subsets of colums separated
     * by their tables.
     *
     * @param  columns columns
     * @return         columns separated by tables: The collection of a
     *                 tablename are (only) columns within this table
     */
    public static Map<String, List<Column>> getColumnsSeparatedByTables(
            Collection<? extends Column> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        Map<String, List<Column>> columnsOfTable = new HashMap<String,
                                                       List<Column>>();

        for (Column col : columns) {
            String       tablename = col.getTablename();
            List<Column> cols      = columnsOfTable.get(tablename);

            if (cols == null) {
                cols = new ArrayList<Column>();
            }

            cols.add(col);
            columnsOfTable.put(tablename, cols);
        }

        return columnsOfTable;
    }

    /**
     * Returns the distinct table names of a collection of columns.
     *
     * @param columns Spalten
     * @return        Tabellen
     */
    public static Set<String> getDistinctTablenamesOfColumns(
            Collection<? extends Column> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        Set<String> tablenames = new HashSet<String>();

        for (Column column : columns) {
            tablenames.add(column.getTablename());
        }

        return tablenames;
    }
}
