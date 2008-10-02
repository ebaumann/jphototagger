package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>keyword</code> der Tabelle <code>iptc_keywords</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcKeywordsKeyword extends Column {

    private static ColumnIptcKeywordsKeyword instance = new ColumnIptcKeywordsKeyword();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcKeywordsKeyword() {
        super(
            TableIptcKeywords.getInstance(),
            "keyword", // NOI18N
            DataType.string);

        setLength(64);
        setDescription(Bundle.getString("ColumnIptcKeywordsKeyword.Description"));
    }
}
