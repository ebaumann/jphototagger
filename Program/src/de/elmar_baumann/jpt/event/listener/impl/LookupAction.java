package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.app.JptSelectionLookup;
import de.elmar_baumann.lib.util.Lookup;

import java.util.Collection;

import javax.swing.AbstractAction;

/**
 *
 *
 * @param <T> type of content in lookup to listen for
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public abstract class LookupAction<T> extends AbstractAction
        implements Lookup.Listener {
    private static final long serialVersionUID = 7448042271259811319L;
    private final Class<T>    contentClass;

    protected LookupAction(Class<T> contentClass) {
        this.contentClass = contentClass;
        setEnabled(false);
        JptSelectionLookup.INSTANCE.addListener(contentClass, this);
    }

    @Override
    public void contentAdded(Collection<? extends Object> content) {
        setEnabled();
    }

    @Override
    public void contentRemoved(Collection<? extends Object> content) {
        setEnabled();
    }

    private void setEnabled() {
        setEnabled(!JptSelectionLookup.INSTANCE.lookupAll(contentClass).isEmpty());
    }
}
