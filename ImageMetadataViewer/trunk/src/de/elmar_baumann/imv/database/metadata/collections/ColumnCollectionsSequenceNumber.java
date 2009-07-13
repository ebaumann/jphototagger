package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>sequence_number</code> der Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public final class ColumnCollectionsSequenceNumber extends Column {

    public static final ColumnCollectionsSequenceNumber INSTANCE = new ColumnCollectionsSequenceNumber();

    private ColumnCollectionsSequenceNumber() {
        super(
            TableCollections.INSTANCE,
            "sequence_number", // NOI18N
            DataType.INTEGER);

        setDescription(Bundle.getString("ColumnCollectionsSequenceNumber.Description")); // NOI18N
    }
}
