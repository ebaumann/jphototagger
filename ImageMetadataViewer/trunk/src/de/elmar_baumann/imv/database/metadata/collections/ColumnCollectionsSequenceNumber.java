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

    private static final ColumnCollectionsSequenceNumber instance = new ColumnCollectionsSequenceNumber();

    public static ColumnCollectionsSequenceNumber getInstance() {
        return instance;
    }

    private ColumnCollectionsSequenceNumber() {
        super(
            TableCollections.getInstance(),
            "sequence_number", // NOI18N
            DataType.Integer);

        setDescription(Bundle.getString("ColumnCollectionsSequenceNumber.Description"));
    }
}
