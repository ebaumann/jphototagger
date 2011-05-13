package org.jphototagger.lib.comparator;

import java.io.Serializable;
import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Compares tree nodes by their level to the root node.
 *
 * @author Elmar Baumann
 */
public final class ComparatorTreeNodeLevel implements Comparator<DefaultMutableTreeNode>, Serializable {

    /**
     * Sorts tree nodes in ascending order: A node with a lower level is before
     * a node with a higher level.
     */
    public static final ComparatorTreeNodeLevel ASCENDING = new ComparatorTreeNodeLevel(CompareOrder.ASCENDING);

    /**
     * Sorts tree nodes in descending order: A node with a higher level is
     * before a node with a lower level.
     */
    public static final ComparatorTreeNodeLevel DESCENDING = new ComparatorTreeNodeLevel(CompareOrder.DESCENDING);
    private static final long serialVersionUID = 1955019986650441963L;

    /**
     * Sort order.
     */
    private final CompareOrder order;

    private ComparatorTreeNodeLevel(CompareOrder order) {
        this.order = order;
    }

    @Override
    public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
        int o1Level = o1.getLevel();
        int o2Level = o2.getLevel();
        boolean equals = o1Level == o2Level;
        boolean greater = o1Level > o2Level;

        return equals
               ? 0
               : greater
                 ? 1
                 : -1;
    }
}
