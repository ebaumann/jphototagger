/*
 * @(#)TreeNodeSortedChildren.java    Created on 2009-07-02
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
