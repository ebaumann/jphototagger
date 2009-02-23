package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class TableXmp extends Table {

    public static final TableXmp INSTANCE = new TableXmp();

    private TableXmp() {
        super("xmp"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpId.INSTANCE);
        addColumn(ColumnXmpIdFiles.INSTANCE);
        addColumn(ColumnXmpDcCreator.INSTANCE);
        addColumn(ColumnXmpDcDescription.INSTANCE);
        addColumn(ColumnXmpDcRights.INSTANCE);
        addColumn(ColumnXmpDcTitle.INSTANCE);
        addColumn(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
        addColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        addColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        addColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        addColumn(ColumnXmpPhotoshopCategory.INSTANCE);
        addColumn(ColumnXmpPhotoshopCity.INSTANCE);
        addColumn(ColumnXmpPhotoshopCountry.INSTANCE);
        addColumn(ColumnXmpPhotoshopCredit.INSTANCE);
        addColumn(ColumnXmpPhotoshopHeadline.INSTANCE);
        addColumn(ColumnXmpPhotoshopInstructions.INSTANCE);
        addColumn(ColumnXmpPhotoshopSource.INSTANCE);
        addColumn(ColumnXmpPhotoshopState.INSTANCE);
        addColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
    }
}
