package org.jphototagger.lib.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;

import org.jphototagger.api.nodes.Node;

/**
 * @author Elmar Baumann
 */
public final class NodesListModel extends AbstractListModel {

    private static final long serialVersionUID = 1L;
    private final List<Node> nodes = new ArrayList<Node>();

    public NodesListModel() {
    }

    public NodesListModel(Collection<? extends Node> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }
        this.nodes.addAll(nodes);
    }

    public void addNodes(Collection<? extends Node> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }
        if (!nodes.isEmpty()) {
            int addedFromIndex = this.nodes.size();
            int addedToIndex = addedFromIndex + nodes.size() - 1;
            this.nodes.addAll(nodes);
            fireIntervalAdded(this, addedFromIndex, addedToIndex);
        }
    }

    @Override
    public int getSize() {
        return nodes.size();
    }

    @Override
    public Object getElementAt(int index) {
        return nodes.get(index);
    }
}
