package org.jphototagger.lib.swing;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * A mutable tree node sorting it's children.
 *
 * Compares the user object's strings ignoring the case.
 *
 * @author Elmar Baumann
 */
public final class SortedChildrenTreeNode extends DefaultMutableTreeNode implements Comparable<Object> {

    private static final long serialVersionUID = 1L;
    private static final Collator COLLATOR = Collator.getInstance();
    private boolean sortEnabled = true;

    public SortedChildrenTreeNode() {
    }

    public SortedChildrenTreeNode(Object userObject) {
        super(userObject);
    }

    public SortedChildrenTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @SuppressWarnings("unchecked")
    public void sortChildren() {
        if (sortEnabled && this.children != null) {
            Collections.sort(this.children);
        }
    }

    public void insertUnsorted(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
        sortChildren();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        sortChildren();
    }

    public void addUnsorted(MutableTreeNode newChild) {
        super.add(newChild);
    }

    public void insertAll(Collection<? extends MutableTreeNode> nodes) {
        insertAll(nodes, true);
    }

    public void insertAllUnsorted(Collection<? extends MutableTreeNode> nodes) {
        insertAll(nodes, false);
    }

    @SuppressWarnings("unchecked")
    private void insertAll(Collection<? extends MutableTreeNode> nodes, boolean sortChildren) {
        if (!allowsChildren) {
            throw new IllegalStateException("node does not allow children");
        }
        for (MutableTreeNode node : nodes) {
            if (node == null) {
                throw new NullPointerException("node == null");
            }
            if (isNodeAncestor(node)) {
                throw new IllegalArgumentException("new child is an ancestor");
            }
            MutableTreeNode oldParent = (MutableTreeNode)node.getParent();
            if (oldParent != null) {
                oldParent.remove(node);
            }
            node.setParent(this);
        }
        if (children == null) {
            children = new Vector();
        }
        children.addAll(nodes);
        if (sortChildren) {
            sortChildren();
        }
    }

    // Only used by Collections.sortChildren(this.children) in #insert(), so that
    // a compare of the path is not neccessary
    @Override
    public int compareTo(final Object o) {
        return COLLATOR.compare(this.toString(), o.toString());
    }

    public boolean isSortEnabled() {
        return sortEnabled;
    }

    /**
     * @param sortEnabled Default: true
     */
    public void setSortEnabled(boolean sortEnabled) {
        this.sortEnabled = sortEnabled;
    }
}
