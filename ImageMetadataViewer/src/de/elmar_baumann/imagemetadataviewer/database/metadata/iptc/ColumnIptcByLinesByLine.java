package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>byline</code> der Tabelle <code>iptc_bylines</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcByLinesByLine extends Column {

    private static ColumnIptcByLinesByLine instance = new ColumnIptcByLinesByLine();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcByLinesByLine() {
        super(
            TableIptcByLines.getInstance(),
            "byline", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcByLinesByLine.Description"));
    }
}
