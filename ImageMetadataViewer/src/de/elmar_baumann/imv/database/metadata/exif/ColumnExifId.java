package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>exif</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnExifId extends Column {

    private static ColumnExifId instance = new ColumnExifId();

    public static ColumnExifId getInstance() {
        return instance;
    }

    private ColumnExifId() {
        super(
            TableExif.getInstance(),
            "id", // NOI18N
            DataType.Bigint);

        setIsPrimaryKey(true);
    }
}
