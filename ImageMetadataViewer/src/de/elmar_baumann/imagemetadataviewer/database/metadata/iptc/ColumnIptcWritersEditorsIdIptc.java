package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_writers_editors</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcWritersEditorsIdIptc extends Column {

    private static ColumnIptcWritersEditorsIdIptc instance = new ColumnIptcWritersEditorsIdIptc();

    public static ColumnIptcWritersEditorsIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcWritersEditorsIdIptc() {
        super(
            TableIptcWritersEditors.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
