package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_keywords</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcKeywordsId extends Column {

    private static ColumnIptcKeywordsId instance = new ColumnIptcKeywordsId();

    public static ColumnIptcKeywordsId getInstance() {
        return instance;
    }

    private ColumnIptcKeywordsId() {
        super(
            TableIptcKeywords.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
