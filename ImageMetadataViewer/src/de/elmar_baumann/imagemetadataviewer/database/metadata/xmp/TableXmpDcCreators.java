package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>xmp_dc_creators</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableXmpDcCreators extends Table {

    private static TableXmpDcCreators instance = new TableXmpDcCreators();

    public static TableXmpDcCreators getInstance() {
        return instance;
    }

    private TableXmpDcCreators() {
        super("xmp_dc_creators"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnXmpDcCreatorsId.getInstance());
        addColumn(ColumnXmpDcCreatorsIdXmp.getInstance());
        addColumn(ColumnXmpDcCreatorsCreator.getInstance());
    }
}
