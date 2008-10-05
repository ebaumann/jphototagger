package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;
import de.elmar_baumann.imagemetadataviewer.database.metadata.collections.ColumnCollectionsSequenceNumber;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesLastModified;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesThumbnail;
import java.util.ArrayList;

/**
 * Spalten für die erweiterte Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public class AdvancedSearchColumns {

    private static ArrayList<Column> columns = new ArrayList<Column>();
    private static ArrayList<Column> excludeColumns = new ArrayList<Column>();
    private static AdvancedSearchColumns instance = new AdvancedSearchColumns();
    

    static {
        excludeColumns.add(ColumnFilesLastModified.getInstance());
        excludeColumns.add(ColumnFilesThumbnail.getInstance());
        excludeColumns.add(ColumnCollectionsSequenceNumber.getInstance());

        ArrayList<Table> tables = AllTables.get();
        for (Table table : tables) {
            ArrayList<Column> allColumns = table.getColumns();
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
    public ArrayList<Column> get() {
        return columns;
    }

    private AdvancedSearchColumns() {
    }
}
