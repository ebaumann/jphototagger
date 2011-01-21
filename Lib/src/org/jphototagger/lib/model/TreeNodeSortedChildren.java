package org.jphototagger.lib.model;

import java.text.Collator;

import java.util.Collections;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * A mutable tree node sorting it's children.
 *
 * Compares the user object's strings ignoring the case.
 *
 * @author Elmar Baumann
 */
public final class TreeNodeSortedChildren extends DefaultMutableTreeNode
        implements Comparable<Object> {
    private static final long     serialVersionUID = 5429135948886700418L;
    private static final Collator collator         = Collator.getInstance();

    public TreeNodeSortedChildren() {}

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

    // Only used by Collections.sort(this.children) in #insert(), so that
    // a compare of the path is not neccessary
    @Override
    public int compareTo(final Object o) {
        return collator.compare(this.toString(), o.toString());
    }
}
