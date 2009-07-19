package de.elmar_baumann.lib.model;

import java.util.Collections;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * A mutable tree node sorting it's children.
 *
 * Compares the user object's strings ignoring the case.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-02
 */
public final class TreeNodeSortedChildren extends DefaultMutableTreeNode
        implements Comparable {

    public TreeNodeSortedChildren() {
    }

    public TreeNodeSortedChildren(Object userObject) {
        super(userObject);
    }

    public TreeNodeSortedChildren(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insert(final MutableTreeNode newChild, final int childIndex) {
        super.insert(newChild, childIndex);
        Collections.sort(this.children);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(final MutableTreeNode newChild) {
        super.add(newChild);
        Collections.sort(this.children);
    }

    @Override
    public int compareTo(final Object o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }
}
