package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableXmp extends Table {

    private static TableXmp instance = new TableXmp();

    public static TableXmp getInstance() {
        return instance;
    }

    private TableXmp() {
        super("xmp"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpId.getInstance());
        addColumn(ColumnXmpIdFiles.getInstance());
        addColumn(ColumnXmpDcCreator.getInstance());
        addColumn(ColumnXmpDcDescription.getInstance());
        addColumn(ColumnXmpDcRights.getInstance());
        addColumn(ColumnXmpDcTitle.getInstance());
        addColumn(ColumnXmpIptc4xmpcoreCountrycode.getInstance());
        addColumn(ColumnXmpIptc4xmpcoreLocation.getInstance());
        addColumn(ColumnXmpPhotoshopAuthorsposition.getInstance());
        addColumn(ColumnXmpPhotoshopCaptionwriter.getInstance());
        addColumn(ColumnXmpPhotoshopCategory.getInstance());
        addColumn(ColumnXmpPhotoshopCity.getInstance());
        addColumn(ColumnXmpPhotoshopCountry.getInstance());
        addColumn(ColumnXmpPhotoshopCredit.getInstance());
        addColumn(ColumnXmpPhotoshopHeadline.getInstance());
        addColumn(ColumnXmpPhotoshopInstructions.getInstance());
        addColumn(ColumnXmpPhotoshopSource.getInstance());
        addColumn(ColumnXmpPhotoshopState.getInstance());
        addColumn(ColumnXmpPhotoshopTransmissionReference.getInstance());
    }
}
