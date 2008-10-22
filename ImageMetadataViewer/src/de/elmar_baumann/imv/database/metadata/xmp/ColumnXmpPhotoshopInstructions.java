package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_instructions</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopInstructions extends Column {

    private static ColumnXmpPhotoshopInstructions instance = new ColumnXmpPhotoshopInstructions();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopInstructions() {
        super(
            TableXmp.getInstance(),
            "photoshop_instructions", // NOI18N
            DataType.String);

        setLength(256);
        setDescription(Bundle.getString("ColumnXmpPhotoshopInstructions.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopInstructions.LongerDescription"));
    }
}
