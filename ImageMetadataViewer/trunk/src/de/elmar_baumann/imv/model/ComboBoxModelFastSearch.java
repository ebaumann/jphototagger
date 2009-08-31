package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.FastSearchColumns;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import javax.swing.DefaultComboBoxModel;

/**
 * Contains the columns for the fast search.
 *
 * The elements are the columns where a fast search should look for or
 * {@link #ALL_DEFINED_COLUMNS}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-31
 */
public final class ComboBoxModelFastSearch extends DefaultComboBoxModel {

    public static final String ALL_DEFINED_COLUMNS = "AllDefined";

    public ComboBoxModelFastSearch() {
        addElements();
    }

    private void addElements() {
        addElement(ALL_DEFINED_COLUMNS);
        for (Column column : FastSearchColumns.get()) {
            if (isSearchColumn(column)) {
                addElement(column);
            }
        }
    }

    private boolean isSearchColumn(Column column) {
        return !column.equals(ColumnXmpPhotoshopCategory.INSTANCE) &&
                !column.equals(
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
    }
}
