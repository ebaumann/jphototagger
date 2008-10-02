package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcId extends Column {

    private static ColumnIptcId instance = new ColumnIptcId();

    public static ColumnIptcId getInstance() {
        return instance;
    }

    private ColumnIptcId() {
        super(
            TableIptc.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
