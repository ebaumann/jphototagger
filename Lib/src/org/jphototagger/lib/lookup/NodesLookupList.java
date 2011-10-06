package org.jphototagger.lib.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import org.jphototagger.api.nodes.Node;

/**
 * List which puts selected list items into a Lookup. The item type
 *
 * @author Elmar Baumann
 */
public final class NodesLookupList extends JXList {

    private static final long serialVersionUID = 1L;
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup = new AbstractLookup(content);

    public NodesLookupList() {
        super(new DefaultListModel());
        addListSelectionListener(selectionListener);
    }

    public Lookup getLookup() {
        return lookup;
    }

    public void setModel(NodesListModel model) {
        super.setModel(model);
    }

    /**
     * <em>Don't call this method!</em>.
     * @param model
     * @throws UnsupportedOperationException
     */
    @Override
    public void setModel(ListModel model) {
        throw new UnsupportedOperationException("Forbidden as long as models can contain elements of aribitrary type");
    }

    /**
     * <em>Don't call this method!</em>.
     * @param listData
     * @throws UnsupportedOperationException
     */
    @Override
    public void setListData(Object[] listData) {
        throw new UnsupportedOperationException("Forbidden as long as models can contain elements of aribitrary type");
    }

    /**
     * <em>Don't call this method!</em>.
     * @param listData
     * @throws UnsupportedOperationException
     */
    @Override
    public void setListData(Vector<?> listData) {
        throw new UnsupportedOperationException("Forbidden as long as models can contain elements of aribitrary type");
    }

    private ListSelectionListener selectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                content.set(getSelectedElements(), null);
            }
        }

        private Collection<?> getSelectedElements() {
            Object[] selectedValues = getSelectedValues();

            if (selectedValues.length == 0) {
                return Collections.emptyList();
            }

            List<Object> selectedNodesContent = new ArrayList<Object>(selectedValues.length);

            for (Object selectedVaue : selectedValues) {
                Node node = (Node) selectedVaue;
                Collection<?> nodeContent = node.getContent();
                selectedNodesContent.addAll(nodeContent);
            }

            return selectedNodesContent;
        }
    };
}
