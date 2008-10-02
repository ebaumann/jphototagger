package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_by_lines</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcByLinesId extends Column {

    private static ColumnIptcByLinesId instance = new ColumnIptcByLinesId();

    public static ColumnIptcByLinesId getInstance() {
        return instance;
    }

    private ColumnIptcByLinesId() {
        super(
            TableIptcByLines.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
