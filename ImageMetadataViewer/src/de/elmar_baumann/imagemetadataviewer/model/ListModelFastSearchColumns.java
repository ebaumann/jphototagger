package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.FastSearchColumns;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;

/**
 * Model mit Spalten für die Schnellsuche.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/30
 * @see     de.elmar_baumann.lib.component.CheckList
 */
public class ListModelFastSearchColumns extends DefaultListModel {

    private Vector<Column> columns = FastSearchColumns.getInstance().getSearchColumns();

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
    public Vector<Column> getTableColumns(Vector<Integer> indices) {
        Vector<Column> cols = new Vector<Column>();
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
