package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpId extends Column {

    private static final ColumnXmpId instance = new ColumnXmpId();

    public static ColumnXmpId getInstance() {
        return instance;
    }

    private ColumnXmpId() {
        super(
            TableXmp.getInstance(),
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
