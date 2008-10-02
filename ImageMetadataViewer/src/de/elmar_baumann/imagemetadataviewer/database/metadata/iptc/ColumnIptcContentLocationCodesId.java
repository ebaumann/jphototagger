package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_content_location_codes</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcContentLocationCodesId extends Column {

    private static ColumnIptcContentLocationCodesId instance = new ColumnIptcContentLocationCodesId();

    public static ColumnIptcContentLocationCodesId getInstance() {
        return instance;
    }

    private ColumnIptcContentLocationCodesId() {
        super(
            TableIptcContentLocationCodes.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
