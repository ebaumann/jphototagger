package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.util.StringUtil;
import java.awt.Component;
import java.util.List;
import javax.swing.JLabel;
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

    private static final String HTML_LINE_BREAK = "<br />";

    /**
     * Embeds text into HTML, breaks the text into multiple lines if necessary
     * and sets it to the label. Sets the heigt of the label's row to fit it's
     * content.
     *
     * @param table           the label's table
     * @param row             the label's row
     * @param label           label, gets the HTML formatted text
     * @param text            text to set
     * @param maxCharsPerLine count of maximum characters per line
     * @param style           a CSS style, e.g.
     *                        <code>"padding-left:5px;padding-right:3px;"</code>
     */
    public static void embedTableCellTextInHtml(JTable table, int row,
            JLabel label, String text, int maxCharsPerLine, String style) {
        if (table == null)
            throw new NullPointerException("table == null");
        if (label == null)
            throw new NullPointerException("label == null");

        List<String> lines = StringUtil.wrapWords(text, maxCharsPerLine);
        StringBuilder sb =
                new StringBuilder(
                "<html><style type=\"text/css\">body { " + style +
                " }</style></head><body>");
        int lineCount = lines.size();
        for (int i = 0; i < lineCount; i++) {
            sb.append(i == 0
                      ? lines.get(i)
                      : HTML_LINE_BREAK + lines.get(i));
        }
        sb.append("</body></html>");
        System.out.println(sb.toString());
        label.setText(sb.toString());
        int preferredHeight = label.getPreferredSize().height;
        if (preferredHeight > table.getRowHeight(row)) {
            table.setRowHeight(row, preferredHeight);
        }
    }

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
                        model.getColumnClass(colIndex)).
                        getTableCellRendererComponent(
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
