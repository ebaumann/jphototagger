package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>source</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcSource extends Column {

    private static ColumnIptcSource instance = new ColumnIptcSource();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcSource() {
        super(
            TableIptc.getInstance(),
            "source", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcSource.Description"));
    }
}
