package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_keywords</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcKeywordsIdIptc extends Column {

    private static ColumnIptcKeywordsIdIptc instance = new ColumnIptcKeywordsIdIptc();

    public static ColumnIptcKeywordsIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcKeywordsIdIptc() {
        super(
            TableIptcKeywords.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
