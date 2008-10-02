package de.elmar_baumann.imagemetadataviewer.database.metadata.savedsearches;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>name</code> der Tabelle <code>saved_searches</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public class ColumnSavedSearchesName extends Column {
    
    private static ColumnSavedSearchesName instance = new ColumnSavedSearchesName();
    
    public static ColumnSavedSearchesName getInstance() {
        return instance;
    }
    
    private ColumnSavedSearchesName() {
        super(
            TableSavedSearches.getInstance(),
            "name", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnSavedSearchesName.Description"));
    }

}
