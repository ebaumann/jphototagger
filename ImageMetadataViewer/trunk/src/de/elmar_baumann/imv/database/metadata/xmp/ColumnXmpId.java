package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class ColumnXmpId extends Column {

    public static final ColumnXmpId INSTANCE = new ColumnXmpId();

    private ColumnXmpId() {
        super(
            TableXmp.INSTANCE,
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
