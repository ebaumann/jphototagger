package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_xmp</code> der Tabelle <code>xmp_dc_creators</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnXmpDcCreatorsIdXmp extends Column {

    private static ColumnXmpDcCreatorsIdXmp instance = new ColumnXmpDcCreatorsIdXmp();

    public static ColumnXmpDcCreatorsIdXmp getInstance() {
        return instance;
    }

    private ColumnXmpDcCreatorsIdXmp() {
        super(
            TableXmpDcCreators.getInstance(),
            "id_xmp", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnXmpId.getInstance());

    }
}
