package org.jphototagger.api.windows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Elmar Baumann
 */
public final class SelectionItemSelectedEvent {

    private final Object source;
    private final Collection<?> selectedItems;

    public SelectionItemSelectedEvent(Object source, Collection<?> selectedItems) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (selectedItems == null) {
            throw new NullPointerException("selectedItems == null");
        }

        this.source = source;
        this.selectedItems = new ArrayList<Object>(selectedItems);
    }

    public Collection<?> getSelectedItems() {
        return Collections.unmodifiableCollection(selectedItems);
    }

    public Object getSource() {
        return source;
    }
}
