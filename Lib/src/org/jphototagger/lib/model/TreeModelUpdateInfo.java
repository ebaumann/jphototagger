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
 * @author Elmar Baumann
 */
public final class TreeModelUpdateInfo {

    /**
     * Contains a node and the indices of it's children
     *
     * @author Elmar Baumann
     */
    public static class NodeAndChildIndices {
        private TreeNode node;
        private List<Integer> childIndices = new ArrayList<Integer>();

        public NodeAndChildIndices() {}

        /**
         * Constructor.
         *
         * @param  node         node
         * @param  childIndices indices of that node's children
         */
        public NodeAndChildIndices(TreeNode node, List<Integer> childIndices) {
            this.node = node;
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
     * {@link javax.swing.tree.DefaultTreeModel#nodesWereRemoved(TreeNode, int[], Object[])}
     * where the node is the not deleted parent.
     *
     * @author Elmar Baumann
     */
    public static class NodeAndChild {
        private TreeNode node;
        private Object[] updatedChildren = new Object[1];
        private int[] updatedChildIndices = new int[1];

        public int[] getUpdatedChildIndex() {
            return Arrays.copyOf(updatedChildIndices, updatedChildIndices.length);
        }

        public Object[] getUpdatedChild() {
            return Arrays.copyOf(updatedChildren, updatedChildren.length);
        }

        /**
         * Sets the updated child.
         *
         * @param child child
         * @param index index of that child
         */
        public void setUpdatedChild(Object child, int index) {
            updatedChildren[0] = child;
            updatedChildIndices[0] = index;
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
        private List<NodeAndChildIndices> info = new ArrayList<TreeModelUpdateInfo.NodeAndChildIndices>();

        /**
         * Adds as last element a new node when more than one children were
         * updated.
         *
         * @param node            node
         * @param childrenIndices indices of updated children
         */
        public void addNode(TreeNode node, int[] childrenIndices) {
            info.add(new NodeAndChildIndices(node, ArrayUtil.toList(childrenIndices)));
        }

        /**
         * Adds a new node when exactly one child was updated.
         *
         * @param node       node
         * @param childIndex index of the updated child
         */
        public void addNode(TreeNode node, int childIndex) {
            info.add(new NodeAndChildIndices(node, Arrays.asList(Integer.valueOf(childIndex))));
        }

        public List<NodeAndChildIndices> getInfo() {
            return Collections.unmodifiableList(info);
        }
    }
}
