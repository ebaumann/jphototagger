package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>files</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class TableFiles extends Table {

    public static final TableFiles INSTANCE = new TableFiles();

    private TableFiles() {
        super("files"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnFilesId.INSTANCE);
        addColumn(ColumnFilesFilename.INSTANCE);
        addColumn(ColumnFilesLastModified.INSTANCE);
        addColumn(ColumnFilesThumbnail.INSTANCE);
    }
}
