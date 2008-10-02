package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp_dc_creators</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnXmpDcCreatorsId extends Column {

    private static ColumnXmpDcCreatorsId instance = new ColumnXmpDcCreatorsId();

    public static ColumnXmpDcCreatorsId getInstance() {
        return instance;
    }

    private ColumnXmpDcCreatorsId() {
        super(
            TableXmpDcCreators.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
