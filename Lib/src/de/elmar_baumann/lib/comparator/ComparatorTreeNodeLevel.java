/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.comparator;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Compares tree nodes by their level to the root node.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-28
 */
public final class ComparatorTreeNodeLevel implements
        Comparator<DefaultMutableTreeNode> {

    /**
     * Sorts tree nodes in ascending order: A node with a lower level is before
     * a node with a higher level.
     */
    public static final ComparatorTreeNodeLevel ASCENDING =
            new ComparatorTreeNodeLevel(CompareOrder.ASCENDING);
    /**
     * Sorts tree nodes in descending order: A node with a higher level is
     * before a node with a lower level.
     */
    public static final ComparatorTreeNodeLevel DESCENDING =
            new ComparatorTreeNodeLevel(CompareOrder.DESCENDING);
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
