package org.jphototagger.program.database.metadata;

import org.jphototagger.domain.metadata.MetaDataValue;
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
    public static Map<String, List<MetaDataValue>> getColumnsSeparatedByTables(Collection<? extends MetaDataValue> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        Map<String, List<MetaDataValue>> columnsOfTable = new HashMap<String, List<MetaDataValue>>();

        for (MetaDataValue col : columns) {
            String tablename = col.getCategory();
            List<MetaDataValue> cols = columnsOfTable.get(tablename);

            if (cols == null) {
                cols = new ArrayList<MetaDataValue>();
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
    public static Set<String> getDistinctTablenamesOfColumns(Collection<? extends MetaDataValue> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        Set<String> tablenames = new HashSet<String>();

        for (MetaDataValue column : columns) {
            tablenames.add(column.getCategory());
        }

        return tablenames;
    }
}
