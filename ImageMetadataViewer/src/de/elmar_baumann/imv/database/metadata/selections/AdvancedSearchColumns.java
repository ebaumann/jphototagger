package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Table;
import de.elmar_baumann.imv.database.metadata.collections.ColumnCollectionsSequenceNumber;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesLastModified;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesThumbnail;
import java.util.ArrayList;
import java.util.List;

/**
 * Spalten für die erweiterte Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public class AdvancedSearchColumns {

    private static List<Column> columns = new ArrayList<Column>();
    private static List<Column> excludeColumns = new ArrayList<Column>();
    private static AdvancedSearchColumns instance = new AdvancedSearchColumns();
    

    static {
        excludeColumns.add(ColumnFilesLastModified.getInstance());
        excludeColumns.add(ColumnFilesThumbnail.getInstance());
        excludeColumns.add(ColumnCollectionsSequenceNumber.getInstance());

        List<Table> tables = AllTables.get();
        for (Table table : tables) {
            List<Column> allColumns = table.getColumns();
            for (Column column : allColumns) {
                if (!column.isPrimaryKey() && !column.isForeignKey() &&
                    !excludeColumns.contains(column)) {
                    columns.add(column);
                }
            }
        }
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static AdvancedSearchColumns getInstance() {
        return instance;
    }

    /**
     * Liefert die Spalten für die erweiterte Suche.
     * 
     * @return Suchspalten
     */
    public List<Column> get() {
        return columns;
    }

    private AdvancedSearchColumns() {
    }
}
