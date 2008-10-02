package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>copyright_notice</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcCopyrightNotice extends Column {

    private static ColumnIptcCopyrightNotice instance = new ColumnIptcCopyrightNotice();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcCopyrightNotice() {
        super(
            TableIptc.getInstance(),
            "copyright_notice", // NOI18N
            DataType.string);

        setLength(128);
        setDescription(Bundle.getString("ColumnIptcCopyrightNotice.Description"));
    }
}
