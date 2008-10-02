package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>special_instructions</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcSpecialInstructions extends Column {

    private static ColumnIptcSpecialInstructions instance = new ColumnIptcSpecialInstructions();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcSpecialInstructions() {
        super(
            TableIptc.getInstance(),
            "special_instructions", // NOI18N
            DataType.string);

        setLength(256);
        setDescription(Bundle.getString("ColumnIptcSpecialInstructions.Description"));
    }
}
