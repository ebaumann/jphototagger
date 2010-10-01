/*
 * @(#)TreeModelWait.java    Created on 2010-10-01
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.program.resource.JptBundle;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Contains exactly one tree node below the root with a "wait" text and is a
 * substitute as * long as a large tree model will be created.
 *
 * @author Elmar Baumann
 */
public final class TreeModelWait extends DefaultTreeModel {
    private static final long         serialVersionUID = -6456827464935791978L;
    private static final String       ITEM_TEXT =
        JptBundle.INSTANCE.getString("TreeModelWait.ItemText");
    public static final TreeModelWait INSTANCE         = new TreeModelWait();

    public TreeModelWait() {
        super(new DefaultMutableTreeNode(ITEM_TEXT));
        ((DefaultMutableTreeNode) getRoot()).add(
            new DefaultMutableTreeNode(ITEM_TEXT));
    }
}
