package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>byline_title</code> der Tabelle <code>iptc_by_lines_titles</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcByLinesTitlesByLineTitle extends Column {

    private static ColumnIptcByLinesTitlesByLineTitle instance = new ColumnIptcByLinesTitlesByLineTitle();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcByLinesTitlesByLineTitle() {
        super(
            TableIptcByLinesTitles.getInstance(),
            "byline_title", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcByLinesTitlesByLineTitle.Description"));
    }
}
