package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_content_location_codes</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcContentLocationCodesIdIptc extends Column {

    private static ColumnIptcContentLocationCodesIdIptc instance = new ColumnIptcContentLocationCodesIdIptc();

    public static ColumnIptcContentLocationCodesIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcContentLocationCodesIdIptc() {
        super(
            TableIptcContentLocationCodes.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
