package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_by_lines</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcByLinesIdIptc extends Column {

    private static ColumnIptcByLinesIdIptc instance = new ColumnIptcByLinesIdIptc();

    public static ColumnIptcByLinesIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcByLinesIdIptc() {
        super(
            TableIptcByLines.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
