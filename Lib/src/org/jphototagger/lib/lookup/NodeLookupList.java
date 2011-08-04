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
import org.jphototagger.api.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * List which puts selected list items into a Lookup. The item type
 *
 * @author Elmar Baumann
 */
public final class NodeLookupList extends JXList {

    private static final long serialVersionUID = 1L;
    private final InstanceContent content = new InstanceContent();
    private Lookup lookup = new AbstractLookup(content);

    public NodeLookupList() {
        super(new DefaultListModel());
        addListSelectionListener(selectionListener);
    }

    public void addNode(Node node) {
        ListModel model = getModel();

        if (model instanceof DefaultListModel) {
            DefaultListModel defaultListModel = (DefaultListModel) model;

            defaultListModel.addElement(node);
        } else {
            throw new IllegalStateException("Unexpected data model: " + model.getClass());
        }

    }

    public Lookup getLookup() {
        return lookup;
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

        private Collection<Node> getSelectedElements() {
            Object[] selectedValues = getSelectedValues();

            if (selectedValues.length == 0) {
                return Collections.emptyList();
            }

            List<Node> selectedNodes = new ArrayList<Node>(selectedValues.length);

            for (Object selectedVaue : selectedValues) {
                Node node = (Node) selectedVaue;
                selectedNodes.add(node);
            }

            return selectedNodes;
        }
    };
}
