package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>files</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableFiles extends Table {

    private static TableFiles instance = new TableFiles();

    public static TableFiles getInstance() {
        return instance;
    }

    private TableFiles() {
        super("files"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imv.database.metadata.selections.AllTables.get()
        addColumn(ColumnFilesId.getInstance());
        addColumn(ColumnFilesFilename.getInstance());
        addColumn(ColumnFilesLastModified.getInstance());
        addColumn(ColumnFilesThumbnail.getInstance());
    }
}
