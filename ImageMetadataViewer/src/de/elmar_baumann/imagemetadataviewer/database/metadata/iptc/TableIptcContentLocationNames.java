package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_content_location_names</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcContentLocationNames extends Table {

    private static TableIptcContentLocationNames instance = new TableIptcContentLocationNames();

    public static TableIptcContentLocationNames getInstance() {
        return instance;
    }

    private TableIptcContentLocationNames() {
        super("iptc_content_location_names"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcContentLocationNamesId.getInstance());
        addColumn(ColumnIptcContentLocationNamesIdIptc.getInstance());
        addColumn(
            ColumnIptcContentLocationNamesContentLocationName.getInstance());
    }
}
