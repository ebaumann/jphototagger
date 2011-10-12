package org.jphototagger.lib.swing;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Extended Table model.
 *
 * @author Elmar Baumann
 */
public class TableModelExt extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    public TableModelExt(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public TableModelExt(Vector data, Vector columnNames) {
        super(data, columnNames);
    }

    public TableModelExt(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public TableModelExt(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public TableModelExt(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public TableModelExt() {
    }

    public void removeAllRows() {
        Vector rows = getDataVector();
        int size = rows.size();

        if (size > 0) {
            rows.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
}
