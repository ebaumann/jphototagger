package de.elmar_baumann.imv.database.metadata.savedsearches;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>name</code> der Tabelle <code>saved_searches</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-17
 */
public final class ColumnSavedSearchesName extends Column {
    
    public static final ColumnSavedSearchesName INSTANCE = new ColumnSavedSearchesName();
    
    private ColumnSavedSearchesName() {
        super(
            TableSavedSearches.INSTANCE,
            "name", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnSavedSearchesName.Description")); // NOI18N
    }

}
