package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_writers_editors</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcWritersEditorsId extends Column {

    private static ColumnIptcWritersEditorsId instance = new ColumnIptcWritersEditorsId();

    public static ColumnIptcWritersEditorsId getInstance() {
        return instance;
    }

    private ColumnIptcWritersEditorsId() {
        super(
            TableIptcWritersEditors.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
