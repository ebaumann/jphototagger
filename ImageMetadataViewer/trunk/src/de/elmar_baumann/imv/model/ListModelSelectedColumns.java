package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.metadata.Column;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;

/**
 * Model mit Spaltenauswahlen. Gültiges Model für
 * {@link de.elmar_baumann.lib.component.CheckList}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     de.elmar_baumann.lib.component.CheckList
 */
public final class ListModelSelectedColumns extends DefaultListModel {

    private List<Column> allColumns;

    public ListModelSelectedColumns(List<Column> allColumns) {
        this.allColumns = allColumns;
        addElements();
    }

    private void addElements() {
        for (Column searchColumn : allColumns) {
            addElement(new JCheckBox(searchColumn.getDescription()));
        }
    }

    /**
     * Liefert eine Tabellenspalte mit bestimmtem Index.
     * 
     * @param index Index
     * @return      Spalte
     */
    public Column getColumnAtIndex(int index) {
        return allColumns.get(index);
    }
}
