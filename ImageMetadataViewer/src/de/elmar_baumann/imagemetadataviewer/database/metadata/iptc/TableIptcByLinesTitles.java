package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_by_lines_titles</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcByLinesTitles extends Table {

    private static TableIptcByLinesTitles instance = new TableIptcByLinesTitles();

    public static TableIptcByLinesTitles getInstance() {
        return instance;
    }

    private TableIptcByLinesTitles() {
        super("iptc_by_lines_titles"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcByLinesTitlesId.getInstance());
        addColumn(ColumnIptcBylinesTitlesIdIptc.getInstance());
        addColumn(ColumnIptcByLinesTitlesByLineTitle.getInstance());
    }
}
