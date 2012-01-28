package org.jphototagger.lib.lookup;

import java.util.Collection;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class SelectedListItemsLookup implements Lookup.Provider {

    private final ModifiableLookup lookup = new ModifiableLookup();

    public SelectedListItemsLookup(JList list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        list.addListSelectionListener(listSelectionListener);
    }
    private final ListSelectionListener listSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                Collection<?> content = LookupUtil.createContentOfSelectedValues(list);
                lookup.set(content);
            }
        }
    };

    @Override
    public Lookup getLookup() {
        return lookup.getLookup();
    }
}
