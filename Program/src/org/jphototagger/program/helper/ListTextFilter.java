package org.jphototagger.program.helper;

import org.jdesktop.swingx.JXList;
import org.jphototagger.program.app.AppLogger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Filters rows in a JXList containing a string.
 * <p>
 * Usage: Add an instance of this class to a Document as DocumentListener.
 *
 * @author Elmar Baumann
 */
public final class ListTextFilter implements DocumentListener {
    private final JXList list;

    public ListTextFilter(JXList list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        this.list = list;
    }

    private void filterText(String text) {
        String trimmedText = text.trim();

        try {
            list.setRowFilter(trimmedText.isEmpty()
                              ? null
                              : new ContainsStringRowFilter(trimmedText));
            
        } catch (Throwable throwable) {
            AppLogger.logSevere(ListTextFilter.class, throwable);
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
                    Logger.getLogger(ListTextFilter.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                filterText("");
            }
        }
    }

    private static class ContainsStringRowFilter extends RowFilter<ListModel, Integer> {
        private final String string;

        ContainsStringRowFilter(String string) {
            this.string = string.toLowerCase();
        }

        @Override
        public boolean include(Entry<? extends ListModel, ? extends Integer> entry) {
            int index = entry.getIdentifier();
            ListModel model = entry.getModel();
            Object element = model.getElementAt(index);

            if (element != null) {
                String stringElementLowerCase = element.toString().toLowerCase();

                return stringElementLowerCase.contains(string);
            } else {
                return false;
            }
        }
    }
}
