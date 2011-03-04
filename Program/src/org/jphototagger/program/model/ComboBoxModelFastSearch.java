package org.jphototagger.program.model;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.FastSearchColumns;

import javax.swing.DefaultComboBoxModel;

/**
 * Elements are the columns for the fast search - instances of
 * {@link org.jphototagger.program.database.metadata.Column} - and a string.
 *
 * The elements retrieved through {@link FastSearchColumns#get()}. The string is
 * {@link #ALL_DEFINED_COLUMNS} and means, the fast search shall search
 * in all columns, else only in the selected column.
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelFastSearch extends DefaultComboBoxModel {
    public static final String ALL_DEFINED_COLUMNS = "AllDefined";
    private static final long serialVersionUID = -705435864208734028L;

    public ComboBoxModelFastSearch() {
        addElements();
    }

    private void addElements() {
        addElement(ALL_DEFINED_COLUMNS);

        for (Column column : FastSearchColumns.get()) {
            addElement(column);
        }
    }
}
