package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_content_location_names</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcContentLocationNamesIdIptc extends Column {

    private static ColumnIptcContentLocationNamesIdIptc instance = new ColumnIptcContentLocationNamesIdIptc();

    public static ColumnIptcContentLocationNamesIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcContentLocationNamesIdIptc() {
        super(
            TableIptcContentLocationNames.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
