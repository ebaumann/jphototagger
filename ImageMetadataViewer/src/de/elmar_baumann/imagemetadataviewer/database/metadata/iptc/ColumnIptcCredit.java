package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>credit</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcCredit extends Column {

    private static ColumnIptcCredit instance = new ColumnIptcCredit();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcCredit() {
        super(
            TableIptc.getInstance(),
            "credit", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcCredit.Description"));
    }
}
