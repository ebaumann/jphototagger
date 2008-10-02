package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>category</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcCategory extends Column {

    private static ColumnIptcCategory instance = new ColumnIptcCategory();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcCategory() {
        super(
            TableIptc.getInstance(),
            "category", // NOI18N
            DataType.string);

        setLength(3);
        setDescription(Bundle.getString("ColumnIptcCategory.Description"));
    }
}
