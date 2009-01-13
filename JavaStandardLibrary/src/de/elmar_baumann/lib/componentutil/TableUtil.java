package de.elmar_baumann.lib.componentutil;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Werkzeuge für Tabellen.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public final class TableUtil {

    /**
     * Setzt die Spaltenbreiten so, dass auch die größte Zelle vollständig
     * dargestellt wird.
     * 
     * BUG: Die Kopfspalte wird nicht berücksichtigt.
     * 
     * @param table Tabelle
     */
    public static void resizeColumnWidthsToFit(JTable table) {
        if (table == null)
            throw new NullPointerException("table == null");

        TableModel model = table.getModel();
        TableColumnModel colModel = table.getColumnModel();
        int columnCount = model.getColumnCount();
        int longestCell = 0;

        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            TableColumn column = colModel.getColumn(colIndex);

            for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
                Object value = model.getValueAt(rowIndex, colIndex);
                if (value == null) {
                    continue;
                }
                Component cell = table.getDefaultRenderer(
                    model.getColumnClass(colIndex)).getTableCellRendererComponent(
                    table, value, false, false, rowIndex, colIndex);

                int width = cell.getPreferredSize().width;

                if (width > longestCell) {
                    longestCell = width;
                }
            }

            setColumnWidth(longestCell + 2, column);
            longestCell = 0;
        }
    }

    /**
     * Setzt die Breite einer Spalte auf einen festen Wert.
     * 
     * @param width  Breite
     * @param column Spalte
     */
    private static void setColumnWidth(int width, TableColumn column) {
        assert column != null : column;
        column.setPreferredWidth(width);
    }

    private TableUtil() {
    }
}
