/*
 * @(#)TreeModelUpdateInfo.java    Created on 2009-06-13
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

import org.jphototagger.lib.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 * Update information of a tree model.
 *
 * @author  Elmar Baumann
 */
public final class TreeModelUpdateInfo {

    /**
     * Contains a node and the indices of it's children
     *
     * @author Elmar Baumann
     */
    public static class NodeAndChildIndices {
        private TreeNode      node;
        private List<Integer> childIndices = new ArrayList<Integer>();

        public NodeAndChildIndices() {}

        /**
         * Constructor.
         *
         * @param  node         node
         * @param  childIndices indices of that node's children
         */
        public NodeAndChildIndices(TreeNode node, List<Integer> childIndices) {
            this.node         = node;
            this.childIndices = childIndices;
        }

        /**
         * Returns the sorted child indices.
         *
         * @return child indices
         */
        public int[] getChildIndices() {
            Collections.sort(childIndices);

            return ArrayUtil.toIntArray(childIndices);
        }

        /**
         * Adds a child index.
         *
         * @param index index
         */
        public void addChildIndex(int index) {
            childIndices.add(index);
        }

        /**
         * Returns the affected node.
         *
         * @return node
         */
        public TreeNode getNode() {
            return node;
        }

        /**
         * Sets the affected node.
         *
         * @param node  node
         */
        public void setNode(TreeNode node) {
            this.node = node;
        }
    }


    /**
     * Update info of a node with one child.
     *
     * Usage in
     * {@link javax.swing.tree.DefaultTreeModel#nodesWereRemoved(javax.swing.tree.TreeNode, int[], java.lang.Object[])}
     * where the node is the not deleted parent.
     *
     * @author Elmar Baumann
     */
    public static class NodeAndChild {
        private TreeNode node;
        private Object[] updatedChild      = new Object[1];
        private int[]    updatedChildIndex = new int[1];

        /**
         * Returns the index of updated child.
         *
         * @return child updated child
         */
        public int[] getUpdatedChildIndex() {
            return updatedChildIndex;
        }

        /**
         * Returns the updated child.
         *
         * @return child updated child
         */
        public Object[] getUpdatedChild() {
            return updatedChild;
        }

        /**
         * Sets the updated child.
         *
         * @param child child
         * @param index index of that child
         */
        public void setUpdatedChild(Object child, int index) {
            updatedChild[0]      = child;
            updatedChildIndex[0] = index;
        }

        /**
         * Returns the affected node.
         *
         * @return affected node
         */
        public TreeNode getNode() {
            return node;
        }

        /**
         * Sets the affected node.
         *
         * @param node affected node
         */
        public void setNode(TreeNode node) {
            this.node = node;
        }
    }


    /**
     * Contains multiple nodes with each can have multiple child indices.
     *
     * @author Elmar Baumann
     */
    public static class NodesAndChildIndices {
        private List<NodeAndChildIndices> info =
            new ArrayList<TreeModelUpdateInfo.NodeAndChildIndices>();

        /**
         * Adds as last element a new node when more than one children were
         * updated.
         *
         * @param node            node
         * @param childrenIndices indices of updated children
         */
        public void addNode(TreeNode node, int[] childrenIndices) {
            info.add(
                new NodeAndChildIndices(
                    node, ArrayUtil.toList(childrenIndices)));
        }

        /**
         * Adds a new node when exactly one child was updated.
         *
         * @param node       node
         * @param childIndex index of the updated child
         */
        public void addNode(TreeNode node, int childIndex) {
            info.add(
                new NodeAndChildIndices(
                    node, Arrays.asList(Integer.valueOf(childIndex))));
        }

        public List<NodeAndChildIndices> getInfo() {
            return info;
        }
    }
}
