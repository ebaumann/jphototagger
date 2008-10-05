package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.FastSearchColumns;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;

/**
 * Model mit Spalten für die Schnellsuche.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     de.elmar_baumann.lib.component.CheckList
 */
public class ListModelFastSearchColumns extends DefaultListModel {

    private List<Column> columns = FastSearchColumns.getInstance().getSearchColumns();

    public ListModelFastSearchColumns() {
        addColumns();
    }

    private void addColumns() {
        for (Column searchColumn : columns) {
            addElement(new JCheckBox(searchColumn.getDescription()));
        }
    }

    /**
     * Liefert Tabellenspalten mit bestimmten Indizes.
     * 
     * @param indices Indizes
     * @return        Tabellenspalten
     */
    public List<Column> getTableColumns(List<Integer> indices) {
        List<Column> cols = new ArrayList<Column>();
        for (int index : indices) {
            cols.add(columns.get(index));
        }
        return cols;
    }

    /**
     * Liefert eine Tabellenspalte mit bestimmtem Index.
     * 
     * @param index Index
     * @return      Spalte
     */
    public Column getTableColumnAtIndex(int index) {
        return columns.get(index);
    }
}
