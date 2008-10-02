package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>original_transmission_reference</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcOriginalTransmissionReference extends Column {

    private static ColumnIptcOriginalTransmissionReference instance = new ColumnIptcOriginalTransmissionReference();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcOriginalTransmissionReference() {
        super(
            TableIptc.getInstance(),
            "original_transmission_reference", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcOriginalTransmissionReference.Description"));
    }
}
