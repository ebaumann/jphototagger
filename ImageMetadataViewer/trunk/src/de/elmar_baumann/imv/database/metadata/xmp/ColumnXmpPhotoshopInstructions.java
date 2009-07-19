package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_instructions</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpPhotoshopInstructions extends Column {

    public static final ColumnXmpPhotoshopInstructions INSTANCE = new ColumnXmpPhotoshopInstructions();

    private ColumnXmpPhotoshopInstructions() {
        super(
            TableXmp.INSTANCE,
            "photoshop_instructions", // NOI18N
            DataType.STRING);

        setLength(256);
        setDescription(Bundle.getString("ColumnXmpPhotoshopInstructions.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopInstructions.LongerDescription")); // NOI18N
    }
}
