package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_content_location_codes</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcContentLocationCodes extends Table {

    private static TableIptcContentLocationCodes instance = new TableIptcContentLocationCodes();

    public static TableIptcContentLocationCodes getInstance() {
        return instance;
    }

    private TableIptcContentLocationCodes() {
        super("iptc_content_location_codes"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcContentLocationCodesId.getInstance());
        addColumn(ColumnIptcContentLocationCodesIdIptc.getInstance());
        addColumn(
            ColumnIptcContentLocationCodesContentLocationCode.getInstance());
    }
}
