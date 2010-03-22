/*
 * @(#)MiscMetadataHelper.java    Created on 2010-03-15
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

package org.jphototagger.program.helper;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.resource.GUI;

import java.util.Collection;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class MiscMetadataHelper {
    private MiscMetadataHelper() {}

    /**
     * Returns wether the parent's user object of a specific node is column
     * contained in a collection of columns.
     *
     * @param  node    node
     * @param  columns columns
     * @return         true if the parent's user object is a column contained in
     *                 <code>columns</code>
     */
    public static boolean isParentUserObjectAColumnOf(
            DefaultMutableTreeNode node, Collection<? extends Column> columns) {
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode) node.getParent();
        Object userObject = parent.getUserObject();

        if (userObject instanceof Column) {
            return columns.contains((Column) userObject);
        }

        return false;
    }

    private static DefaultTreeModel getModel() {
        return (DefaultTreeModel) GUI.INSTANCE.getAppPanel()
            .getTreeMiscMetadata().getModel();
    }

    /**
     * Returns the first node with a specific column as user object.
     *
     * @param  column column
     * @return        node with that column as user object
     */
    public static DefaultMutableTreeNode findNodeContains(Column column) {
        DefaultMutableTreeNode root =
            (DefaultMutableTreeNode) getModel().getRoot();

        for (Enumeration<?> e = root.depthFirstEnumeration();
                e.hasMoreElements(); ) {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) e.nextElement();
            Object userObject = node.getUserObject();

            if ((userObject instanceof Column)
                    && ((Column) userObject).equals(column)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Removes from the model a node with a string value as user object from a
     * parent containing a specific column as user object.
     *
     * @param column column of parent
     * @param value  value of child
     */
    public static void removeChildValueFrom(Column column, String value) {
        DefaultMutableTreeNode node = findNodeContains(column);

        if (node != null) {
            int count = node.getChildCount();

            for (int i = 0; i < count; i++) {
                DefaultMutableTreeNode childNode =
                    (DefaultMutableTreeNode) node.getChildAt(i);
                Object uo = (childNode).getUserObject();

                if ((uo instanceof String) && ((String) uo).equals(value)) {
                    getModel().removeNodeFromParent(childNode);

                    return;
                }
            }
        }
    }
}
