package org.jphototagger.lib.swingx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.jdesktop.swingx.JXList;

/**
 * Filters rows in a JXList containing a string.
 *
 * @author Elmar Baumann
 */
public final class ListTextFilter {

    private final JXList list;
    private ListModelStringRowFilter filter = new ContainsStringRowFilter("");

    public ListTextFilter(JXList list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        this.list = list;
        list.setRowFilter(filter);
    }

    private void filterText(String text) {
        String trimmedText = text.trim();

        try {
            list.setRowFilter(trimmedText.isEmpty()
                    ? null
                    : filter.createNewInstance(trimmedText));
        } catch (Throwable throwable) {
            Logger.getLogger(ListTextFilter.class.getName()).log(Level.SEVERE, null, throwable);
        }
    }

    /**
     *
     * @param rowFilter The default filter filters strings containing a substring ignoring case
     */
    public synchronized void setRowFilter(ListModelStringRowFilter rowFilter) {
        if (rowFilter == null) {
            throw new NullPointerException("rowFilter == null");
        }

        this.filter = rowFilter;
        list.setRowFilter(rowFilter);
    }

    public void filterOnActionPerformed(AbstractButton buttonFiresAction, Document documentWithContentToFilter) {
        if (buttonFiresAction == null) {
            throw new NullPointerException("buttonFiresAction == null");
        }

        if (documentWithContentToFilter == null) {
            throw new NullPointerException("documentWithContentToFilter == null");
        }

        buttonFiresAction.addActionListener(new ActionListenerImpl(documentWithContentToFilter));
    }

    public void filterOnDocumentChanges(Document document) {
        if (document == null) {
            throw new NullPointerException("document == null");
        }

        document.addDocumentListener(new DocumentListenerImpl());
    }

    private static class ContainsStringRowFilter extends ListModelStringRowFilter {

        private final String string;

        private ContainsStringRowFilter(String string) {
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

        @Override
        RowFilter<ListModel, Integer> createNewInstance(String string) {
            return new ContainsStringRowFilter(string);
        }
    }

    private class ActionListenerImpl implements ActionListener {

        private final Document document;

        private ActionListenerImpl(Document document) {
            this.document = document;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            filterDocument(document);
        }
    }

    private class DocumentListenerImpl implements DocumentListener {

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
}
