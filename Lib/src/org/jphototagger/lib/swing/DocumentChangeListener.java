package org.jphototagger.lib.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Calls in a subclass {@link #documentChanged(javax.swing.event.DocumentEvent)}
 * for each of the possible DocumentEvents.
 *
 * @author Elmar Baumann
 */
public abstract class DocumentChangeListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    public abstract void documentChanged(DocumentEvent e);
}
