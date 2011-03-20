package org.jphototagger.program.helper;

import java.util.Comparator;
import org.jphototagger.program.app.AppLogger;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.jphototagger.lib.util.StringUtil;

/**
 * Filters rows in a JTable (at least one column must contain a substring).
 * <p>
 * Usage: Add an instance of this class to a Document as DocumentListener.
 *
 * @author Elmar Baumann
 */
public final class TableTextFilter implements DocumentListener {

    private final JTable table;
    private final TableStringConverter tableStringConverter;

    public TableTextFilter(JTable table, TableStringConverter tableStringConverter) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        if (tableStringConverter == null) {
            throw new NullPointerException("tableStringConverter == null");
        }

        this.table = table;
        this.tableStringConverter = tableStringConverter;
    }

    private void filterText(String text) {
        String trimmedText = text.trim();

        try {
            RowSorter<? extends TableModel> rowSorter = table.getRowSorter();

            if (rowSorter instanceof TableRowSorter<?>) {
                TableRowSorter<?> tableRowSorter = (TableRowSorter<?>) rowSorter;

                tableRowSorter.setStringConverter(tableStringConverter);
                tableRowSorter.setRowFilter(trimmedText.isEmpty()
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
            int row = entry.getIdentifier();
            TableModel model = entry.getModel();
            int columnCount = model.getColumnCount();

            for (int column = 0; column < columnCount; column++) {
                Object value = tableStringConverter.toString(model, row, column);
                String lowerCaseValue = StringUtil.toStringNullToEmptyString(value).toLowerCase();

                if (lowerCaseValue.contains(string)) {
                    return true;
                }
            }

            return false;
        }
    }
}
