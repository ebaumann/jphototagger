package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_bylines</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcByLines extends Table {

    private static TableIptcByLines instance = new TableIptcByLines();

    public static TableIptcByLines getInstance() {
        return instance;
    }

    private TableIptcByLines() {
        super("iptc_bylines"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcByLinesId.getInstance());
        addColumn(ColumnIptcByLinesIdIptc.getInstance());
        addColumn(ColumnIptcByLinesByLine.getInstance());
    }
}
