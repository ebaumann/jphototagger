package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class ColumnExifId extends Column {

    public static final ColumnExifId INSTANCE = new ColumnExifId();

    private ColumnExifId() {
        super(
            TableExif.INSTANCE,
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
