package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;

import java.awt.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.RowSorter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Filters rows in a JTable (at least one column must contain a substring).
 * <p>
 * Usage: Add an instance of this class to a Document as DocumentListener.
 *
 * @author Elmar Baumann
 */
public final class TableTextFilter implements DocumentListener {
    private final JTable table;

    public TableTextFilter(JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        this.table = table;
    }

    private void filterText(String text) {
        String trimmedText = text.trim();

        try {
            RowSorter<? extends TableModel> rowSorter = table.getRowSorter();

            if (rowSorter instanceof TableRowSorter<?>) {
                ((TableRowSorter<?>) rowSorter).setRowFilter(trimmedText.isEmpty()
                        ? null
                        : new ContainsStringRowFilter(trimmedText));
            }
        } catch (Throwable throwable) {
            AppLogger.logSevere(TableTextFilter.class, throwable);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        filterDocument(e.getDocument());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        filterDocument(e.getDocument());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        filterDocument(e.getDocument());
    }

    private void filterDocument(Document document) {
        if (document != null) {
            int length = document.getLength();

            if (length > 0) {
                try {
                    String text = document.getText(0, length);

                    filterText(text);
                } catch (BadLocationException ex) {
                    Logger.getLogger(TableTextFilter.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                filterText("");
            }
        }
    }

    private class ContainsStringRowFilter extends RowFilter<TableModel, Integer> {
        private final String string;

        ContainsStringRowFilter(String string) {
            this.string = string.toLowerCase();
        }

        @Override
        public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
            final int row = entry.getIdentifier();
            TableModel model = entry.getModel();
            int columnCount = model.getColumnCount();

            for (int column = 0; column < columnCount; column++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Object element = model.getValueAt(row, column);
                String lowerCaseValue = (element == null)
                                        ? ""
                                        : element.toString().toLowerCase();

                if ((cellRenderer != null) && (element != null)) {
                    Component c = cellRenderer.getTableCellRendererComponent(table, element, false, false, row, column);

                    if (c instanceof JLabel) {
                        lowerCaseValue = ((JLabel) c).getText().toLowerCase();
                    }
                }

                if (lowerCaseValue.contains(string)) {
                    return true;
                }
            }

            return false;
        }
    }
}
