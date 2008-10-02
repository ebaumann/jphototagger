package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>creation_date</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcCreationDate extends Column {

    private static ColumnIptcCreationDate instance = new ColumnIptcCreationDate();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcCreationDate() {
        super(
            TableIptc.getInstance(),
            "creation_date", // NOI18N
            DataType.date);

        setDescription(Bundle.getString("ColumnIptcCreationDate.Description"));
    }
}
