package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptc extends Table {

    private static TableIptc instance = new TableIptc();

    public static TableIptc getInstance() {
        return instance;
    }

    private TableIptc() {
        super("iptc"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcId.getInstance());
        addColumn(ColumnIptcIdFiles.getInstance());
        addColumn(ColumnIptcCopyrightNotice.getInstance());
        addColumn(ColumnIptcCreationDate.getInstance());
        addColumn(ColumnIptcCaptionAbstract.getInstance());
        addColumn(ColumnIptcObjectName.getInstance());
        addColumn(ColumnIptcHeadline.getInstance());
        addColumn(ColumnIptcCategory.getInstance());
        addColumn(ColumnIptcCity.getInstance());
        addColumn(ColumnIptcProvinceState.getInstance());
        addColumn(ColumnIptcCountryPrimaryLocationName.getInstance());
        addColumn(ColumnIptcOriginalTransmissionReference.getInstance());
        addColumn(ColumnIptcSpecialInstructions.getInstance());
        addColumn(ColumnIptcCredit.getInstance());
        addColumn(ColumnIptcSource.getInstance());
    }
}
