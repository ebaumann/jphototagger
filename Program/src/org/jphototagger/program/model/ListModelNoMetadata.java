package org.jphototagger.program.model;

import org.jphototagger.domain.Column;
import org.jphototagger.program.database.metadata.selections.NoMetadataColumns;
import javax.swing.DefaultListModel;

/**
 * Elements are {@link Column}s retrieved through {@link NoMetadataColumns#get()}.
 *
 * @author Elmar Baumann
 */
public final class ListModelNoMetadata extends DefaultListModel {
    private static final long serialVersionUID = -1610826692746882410L;

    public ListModelNoMetadata() {
        addColumns();
    }

    private void addColumns() {
        for (Column column : NoMetadataColumns.get()) {
            addElement(column);
        }
    }
}
