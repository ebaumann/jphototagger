package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_writers_editors</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcWritersEditors extends Table {

    private static TableIptcWritersEditors instance = new TableIptcWritersEditors();

    public static TableIptcWritersEditors getInstance() {
        return instance;
    }

    private TableIptcWritersEditors() {
        super("iptc_writers_editors"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcWritersEditorsId.getInstance());
        addColumn(ColumnIptcWritersEditorsIdIptc.getInstance());
        addColumn(ColumnIptcWritersEditorsWriterEditor.getInstance());
    }
}
