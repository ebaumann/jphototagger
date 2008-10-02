package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_by_lines_titles</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcByLinesTitlesId extends Column {

    private static ColumnIptcByLinesTitlesId instance = new ColumnIptcByLinesTitlesId();

    public static ColumnIptcByLinesTitlesId getInstance() {
        return instance;
    }

    private ColumnIptcByLinesTitlesId() {
        super(
            TableIptcByLinesTitles.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
