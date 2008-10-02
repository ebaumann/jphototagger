package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_content_location_names</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcContentLocationNamesId extends Column {

    private static ColumnIptcContentLocationNamesId instance = new ColumnIptcContentLocationNamesId();

    public static ColumnIptcContentLocationNamesId getInstance() {
        return instance;
    }

    private ColumnIptcContentLocationNamesId() {
        super(
            TableIptcContentLocationNames.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
