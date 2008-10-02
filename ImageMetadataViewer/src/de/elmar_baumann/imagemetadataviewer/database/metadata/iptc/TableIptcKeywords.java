package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_keywords</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcKeywords extends Table {

    private static TableIptcKeywords instance = new TableIptcKeywords();

    public static TableIptcKeywords getInstance() {
        return instance;
    }

    private TableIptcKeywords() {
        super("iptc_keywords"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcKeywordsId.getInstance());
        addColumn(ColumnIptcKeywordsIdIptc.getInstance());
        addColumn(ColumnIptcKeywordsKeyword.getInstance());
    }
}
