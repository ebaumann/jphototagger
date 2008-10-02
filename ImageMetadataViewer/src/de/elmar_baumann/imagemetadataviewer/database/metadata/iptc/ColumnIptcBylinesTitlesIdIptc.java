package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_by_lines_titles</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcBylinesTitlesIdIptc extends Column {

    private static ColumnIptcBylinesTitlesIdIptc instance = new ColumnIptcBylinesTitlesIdIptc();

    public static ColumnIptcBylinesTitlesIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcBylinesTitlesIdIptc() {
        super(
            TableIptcByLinesTitles.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
