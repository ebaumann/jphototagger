package org.jphototagger.lib.lookup;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;

import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Action for objects in a Lookup.
 *
 * @author Elmar Baumann
 * @param <T> This Action listens for the presence of that objects in the Lookup and uses them for performing
 */
public abstract class LookupAction<T> extends AbstractAction implements LookupListener {

    private final Class<? extends T> lookupResultClass;
    private final Lookup lookup;
    private Lookup.Result<? extends T> lookupResult;

    /**
     *
     * @param lookupResultClass Objects classe's required for performing this action
     * @param lookup  Lookup which can continious change it's contents, sometimes (or always)
     *                this Lookup contains objects of {@code lookupResultClass}
     */
    protected LookupAction(Class<? extends T> lookupResultClass, Lookup lookup) {
        if (lookupResultClass == null) {
            throw new NullPointerException("lookupResultClass == null");
        }

        if (lookup == null) {
            throw new NullPointerException("lookup == null");
        }

        this.lookupResultClass = lookupResultClass;
        this.lookup = lookup;
        setLookupResult();
    }

    /**
     * Subclasses can decide whether this action is enabled based on the Lookup's content.
     *
     * @param lookupContent
     * @return b
     */
    protected abstract boolean isEnabled(Collection<? extends T> lookupContent);

    private void setLookupResult() {
        if (lookupResult == null) {
            lookupResult = lookup.lookupResult(lookupResultClass);
            lookupResult.addLookupListener(this);
            resultChanged(null);
        }
    }

    @Override
    public void resultChanged(LookupEvent evt) {
        setEnabledInDispatchThread();
    }

    private void setEnabledInDispatchThread() {
        final boolean isEnabled = isEnabled(lookupResult.allInstances());

        if (EventQueue.isDispatchThread()) {
            setEnabled(isEnabled);
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setEnabled(isEnabled);
                }
            });
        }
    }

    protected Collection<? extends T> getLookupContent() {
        return (lookupResult == null)
                ? Collections.<T>emptyList()
                : lookupResult.allInstances();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setLookupResult();
        actionPerformed(lookupResult.allInstances());
    }

    /**
     * Will be called through {@code #actionPerformed(java.awt.event.ActionEvent)} with the Lookup's content.
     *
     * @param lookupContent
     */
    public abstract void actionPerformed(Collection<? extends T> lookupContent);
}
