package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.NoMetadataColumns;
import javax.swing.DefaultListModel;

/**
 * Contains the columns where no metadata can be stored in the database
 * (can be null).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-06
 */
public final class ListModelNoMetadata extends DefaultListModel {

    public ListModelNoMetadata() {
        addColumns();
    }

    private void addColumns() {
        for (Column column : NoMetadataColumns.get()) {
            addElement(column);
        }
    }
}
