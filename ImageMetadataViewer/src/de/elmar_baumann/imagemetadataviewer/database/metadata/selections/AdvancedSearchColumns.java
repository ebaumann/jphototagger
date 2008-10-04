package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesLastModified;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesThumbnail;
import java.util.Vector;

/**
 * Spalten für die erweiterte Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public class AdvancedSearchColumns {

    private static Vector<Column> columns = new Vector<Column>();
    private static Vector<Column> excludeColumns = new Vector<Column>();
    private static AdvancedSearchColumns instance = new AdvancedSearchColumns();
    

    static {
        excludeColumns.add(ColumnFilesLastModified.getInstance());
        excludeColumns.add(ColumnFilesThumbnail.getInstance());

        Vector<Table> tables = AllTables.get();
        for (Table table : tables) {
            Vector<Column> allColumns = table.getColumns();
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
    public Vector<Column> get() {
        return columns;
    }

    private AdvancedSearchColumns() {
    }
}
