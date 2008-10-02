package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>headline</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcHeadline extends Column {

    private static ColumnIptcHeadline instance = new ColumnIptcHeadline();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcHeadline() {
        super(
            TableIptc.getInstance(),
            "headline", // NOI18N
            DataType.string);

        setLength(256);
        setDescription(Bundle.getString("ColumnIptcHeadline.Description"));
    }
}
