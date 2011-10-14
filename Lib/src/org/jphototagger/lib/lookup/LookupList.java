package org.jphototagger.lib.lookup;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * List which puts selected list items into it's a Lookup.
 *
 * @author Elmar Baumann
 */
public final class LookupList extends JXList {

    private static final long serialVersionUID = 1L;
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup = new AbstractLookup(content);

    public LookupList() {
        super(new DefaultListModel());
        addListSelectionListener(selectionListener);
    }

    public Lookup getLookup() {
        return lookup;
    }
    private ListSelectionListener selectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                Object[] selectedValues = getSelectedValues();
                content.set(Arrays.asList(selectedValues), null);
            }
        }
    };
}
