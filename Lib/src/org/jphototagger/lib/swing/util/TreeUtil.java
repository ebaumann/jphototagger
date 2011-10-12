package org.jphototagger.lib.swing.util;

import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Elmar Baumann
 */
public final class TreeUtil {

    private TreeUtil() {
    }

    /**
     *     Returns a node with a specific user object in the path below a node.
     *     Uses equals of the user objects.
     *
     *     @param parent     parent node
     *     @param userObject searched user object. If null null will be
     *                       returned.
     *     @return           found node or null if not found
     */
    @SuppressWarnings("unchecked")
    public static DefaultMutableTreeNode findNodeWithUserObject(DefaultMutableTreeNode parent, Object userObject) {
        if (parent == null) {
            throw new NullPointerException("parent == null");
        }

        if (userObject == null) {
            return null;
        }

        for (Enumeration<Object> nodes = parent.preorderEnumeration(); nodes.hasMoreElements();) {
            Object node = nodes.nextElement();

            if (node instanceof DefaultMutableTreeNode) {
                Object userObjectNode = ((DefaultMutableTreeNode) node).getUserObject();

                if ((userObjectNode != null) && userObject.equals(userObjectNode)) {
                    return (DefaultMutableTreeNode) node;
                }
            }
        }

        return null;
    }

    /**
     * Liefert, ob der Mauszeiger über dem Wurzelitem steht.
     *
     * @param  evt  Mausereigenis
     * @return true, falls der Zeiger über dem Wurzelitem steht
     */
    public static boolean isRootItemPosition(MouseEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (evt.getSource() instanceof JTree) {
            JTree tree = (JTree) evt.getSource();
            TreePath mousePath = tree.getPathForLocation(evt.getX(), evt.getY());

            if (mousePath != null) {
                Object root = tree.getModel().getRoot();
                Object mouseItem = mousePath.getLastPathComponent();

                if ((root != null) && (mouseItem != null)) {
                    return root.equals(mouseItem);
                }
            }
        }

        return false;
    }

    /**
     * Deselektiert alle Treeitems von mehreren Trees.
     *
     * @param trees Trees
     */
    public static void clearSelection(List<JTree> trees) {
        if (trees == null) {
            throw new NullPointerException("trees == null");
        }

        for (JTree tree : trees) {
            if (tree.getSelectionCount() > 0) {
                tree.clearSelection();
            }
        }
    }

    /**
     * Liefert aus einem Model den Pfad anhand eines Strings, der diesen
     * spezifiziert.
     *
     * @param treeModel     Model
     * @param pathString    String mit Pfad
     * @param pathSeparator Separator zwischen den einzelnen Pfadbestandteilen
     * @return              Pfad oder null, wenn nicht gefunden
     */
    public static TreePath getTreePath(TreeModel treeModel, String pathString, String pathSeparator) {
        if (treeModel == null) {
            throw new NullPointerException("treeModel == null");
        }

        if (pathString == null) {
            throw new NullPointerException("pathString == null");
        }

        if (pathSeparator == null) {
            throw new NullPointerException("pathSeparator == null");
        }

        StringTokenizer tokenizer = new StringTokenizer(pathString, pathSeparator);
        int tokenCount = tokenizer.countTokens();
        int tokenNumber = 1;
        int tokenFoundCount = 0;
        Object[] path = new Object[(tokenCount > 0)
                ? tokenCount
                : 1];

        if (tokenCount > 0) {
            path[0] = treeModel.getRoot();
            tokenizer.nextToken();

            Object currentElement = treeModel.getRoot();
            Object childElement = null;
            boolean appended = true;

            while (appended && (tokenNumber < tokenCount)) {
                int childCount = treeModel.getChildCount(currentElement);
                String pathToken = tokenizer.nextToken();
                boolean found = false;

                appended = false;

                for (int index = 0; (index < childCount) && !found; index++) {
                    childElement = treeModel.getChild(currentElement, index);
                    found = childElement.toString().equals(pathToken);

                    if (found) {
                        path[tokenNumber] = childElement;
                        currentElement = childElement;
                        appended = true;
                        tokenFoundCount++;
                    }
                }

                tokenNumber++;
            }
        }

        return ((tokenCount > 0) && (tokenCount - 1 == tokenFoundCount))
                ? new TreePath(path)
                : null;
    }

    /**
     * Öffnet den Tree auch, wenn das letzte Element eines Pfads ein Blatt ist.
     *
     * @param tree Tree
     * @param path Pfad
     */
    public static void expandPath(JTree tree, TreePath path) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (path == null) {
            throw new NullPointerException("path == null");
        }

        TreePath expandPath = path;

        if (tree.getModel().isLeaf(path.getLastPathComponent())) {
            expandPath = path.getParentPath();
        }

        tree.expandPath(expandPath);
    }

    /**
     * Expands a path step by step. First the path with not parents will be
     * expanded, then the child path of this, then the child path of the
     * child path until the last path component is reached.
     *
     * @param tree tree
     * @param path path to expand in <code>tree</code>
     */
    public static void expandPathCascade(JTree tree, TreePath path) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (path == null) {
            throw new NullPointerException("path == null");
        }

        Stack<TreePath> stack = new Stack<TreePath>();
        TreePath parent = path;

        while (parent != null) {
            stack.push(parent);
            parent = parent.getParentPath();
        }

        while (!stack.isEmpty()) {
            tree.expandPath(stack.pop());
        }
    }

    /**
     * Checks whether a path is below a node.
     *
     * All children below <code>parentNode</code> have to be
     * <code>DefaultMutableTreeNode</code>s. The child nodes
     * <code>toString()</code> method strings will be compared against the
     * <code>toStrings</code>.
     *
     * @param parentNode parent node, not included in <code>toStrings</code>
     * @param toStrings  first element is the <code>toString()</code> of the
     *                   first child node, second element is the
     *                   <code>toString()</code> of the child's child etc.
     *                   <em>All strings have to be not null!</em>
     * @param ignoreCase true if the strings shall be compared case insensitive
     * @return           true if the path was found
     */
    public static boolean existsPathBelow(DefaultMutableTreeNode parentNode, List<String> toStrings,
            boolean ignoreCase) {
        if (parentNode == null) {
            throw new NullPointerException("parentNode == null");
        }

        if (toStrings == null) {
            throw new NullPointerException("toStrings == null");
        }

        int size = toStrings.size();
        DefaultMutableTreeNode node = parentNode;

        for (int i = 0; i < size; i++) {
            String string = toStrings.get(i);

            if (string == null) {
                throw new NullPointerException("string == null. Element index: " + i);
            }

            node = getChild(node, string, ignoreCase);

            if (node == null) {
                return false;
            }
        }

        return true;
    }

    public static DefaultMutableTreeNode getBestMatchingNodeBelow(DefaultMutableTreeNode parentNode,
            List<String> toStrings, boolean ignoreCase) {
        if (parentNode == null) {
            throw new NullPointerException("parentNode == null");
        }

        if (toStrings == null) {
            throw new NullPointerException("toStrings == null");
        }

        int size = toStrings.size();
        DefaultMutableTreeNode node = parentNode;
        DefaultMutableTreeNode latest = parentNode;

        for (int i = 0; (i < size) && (node != null); i++) {
            String string = toStrings.get(i);

            if (string == null) {
                throw new NullPointerException("string == null. Element index: " + i);
            }

            node = getChild(node, string, ignoreCase);

            if (node != null) {
                latest = node;
            }
        }

        return latest;
    }

    private static DefaultMutableTreeNode getChild(DefaultMutableTreeNode parentNode, String toString,
            boolean ignoreCase) {
        assert toString != null;

        for (Enumeration<?> e = parentNode.children(); e.hasMoreElements();) {
            Object child = e.nextElement();

            if (child instanceof DefaultMutableTreeNode) {
                if (child.toString() == null) {
                    return null;
                }

                boolean exists = ignoreCase
                        ? child.toString().equalsIgnoreCase(toString)
                        : child.toString().equals(toString);

                if (exists) {
                    return (DefaultMutableTreeNode) child;
                }
            }
        }

        return null;
    }

    /**
     * Returns the tree path below a mouse position got by a mouse event.
     *
     * @param  evt mouse event within a {@code JTree}
     * @return   tree path below the mouse position or null if below the mouse
     *           position isn't a tree path
     */
    public static TreePath getTreePath(MouseEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        Object source = evt.getSource();

        if (source instanceof JTree) {
            int mousePosX = evt.getX();
            int mousePosY = evt.getY();

            return ((JTree) source).getPathForLocation(mousePosX, mousePosY);
        }

        return null;
    }

    /**
     * Returns the tree path of a file, each path component is a parent
     * (child) file of a file.
     *
     * <em>Expects that the nodes of the tree having the type
     * {@code DefaultMutableTreeNode}!</em>
     *
     * @param  file   file
     * @param  model  model when the root is not a file, else null
     * @return path
     */
    public static TreePath getTreePath(File file, TreeModel model) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (model == null) {
            throw new NullPointerException("model == null");
        }

        Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
        File parentFile = file;

        while (parentFile != null) {
            stack.push(new DefaultMutableTreeNode(parentFile));
            parentFile = parentFile.getParentFile();
        }

        List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>(stack.size() + 1);

        nodes.add((DefaultMutableTreeNode) model.getRoot());

        while (!stack.isEmpty()) {
            nodes.add(stack.pop());
        }

        return new TreePath(nodes.toArray());
    }

    /**
     * Adds all nodes with a specific user object of a node and it's children.
     *
     * @param foundNodes container to add found nodes
     * @param rootNode   node to search
     * @param userObject user object to compare with equals
     * @param maxCount   maximum count of nodes to add
     */
    @SuppressWarnings("unchecked")
    public static void addNodesUserWithObject(Collection<? super DefaultMutableTreeNode> foundNodes,
            DefaultMutableTreeNode rootNode, Object userObject, int maxCount) {
        if (foundNodes == null) {
            throw new NullPointerException("foundNodes == null");
        }

        if (rootNode == null) {
            throw new NullPointerException("rootNode == null");
        }

        if (userObject == null) {
            throw new NullPointerException("userObject == null");
        }

        if (maxCount < 0) {
            throw new IllegalArgumentException("Negative max count: " + maxCount);
        }

        int foundNodeCount = foundNodes.size();

        for (Enumeration<DefaultMutableTreeNode> children = rootNode.children();
                children.hasMoreElements() && (foundNodeCount <= maxCount);) {
            DefaultMutableTreeNode child = children.nextElement();

            if (userObject.equals(child.getUserObject())) {
                foundNodes.add(child);
            } else {

                // recursive
                addNodesUserWithObject(foundNodes, child, userObject, maxCount);
            }
        }
    }

    /**
     * Searches the child of a node which user object is a specific file.
     *
     * <em>All children have to be DefaultMutableTreeNode instances!</em>
     *
     * @param  parentNode parent node
     * @param  file       file to find
     * @return            the first matching child node containing that file
     */
    public static DefaultMutableTreeNode findChildNodeWithFile(DefaultMutableTreeNode parentNode, File file) {
        if (parentNode == null) {
            throw new NullPointerException("parentNode == null");
        }

        if (file == null) {
            throw new NullPointerException("file == null");
        }

        int childCount = parentNode.getChildCount();

        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = parentNode.getChildAt(i);

            if (childNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) childNode;
                Object userObject = node.getUserObject();

                if (userObject instanceof File) {
                    File f = (File) userObject;

                    if (f.equals(file)) {
                        return node;
                    }
                }
            }
        }

        return null;
    }

    // Code: http://www.exampledepot.com/egs/javax.swing.tree/ExpandAll.html
    /**
     * If expand is true, expands all nodes in the tree.
     * Otherwise, collapses all nodes in the tree.
     *
     * @param tree   tree
     * @param expand true if expand all nodes
     */
    public static void expandAll(JTree tree, boolean expand) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    /**
     * Expands or collapses a path and all it's sub paths.
     *
     * @param tree   tree
     * @param parent parent path
     * @param expand true if expand, false if collapse
     *
     */
    @SuppressWarnings("unchecked")
    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (parent == null) {
            throw new NullPointerException("parent == null");
        }

        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
                TreeNode n = e.nextElement();
                TreePath path = parent.pathByAddingChild(n);

                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * Expands or collapses tree paths and all it's sub paths.
     *
     * @param tree        tree
     * @param parentPaths parent paths
     * @param expand      true if expand, false if collapse
     *
     */
    public static void expandAll(JTree tree, Collection<? extends TreePath> parentPaths, boolean expand) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (parentPaths == null) {
            throw new NullPointerException("parentPaths == null");
        }

        for (TreePath treePath : parentPaths) {
            expandAll(tree, treePath, expand);
        }
    }

    public static void deselectAll(JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        TreeSelectionModel m = tree.getSelectionModel();

        assert m != null;

        if (m != null) {
            m.clearSelection();
        }
    }

    /**
     * Returns whether a node is obove another node (one of it's parents).
     * <p>
     * Moves up from <code>below</code> to all parents until <code>above</code>
     * is found or a parent node above has no parent.
     * <p>
     * Compares the nodes object <strong>references</strong>
     *
     * @param  above node that shall be obove <code>below</code>
     * @param  below node that shall be below <code>above</code>
     * @return       true if <code>above</code> is one of <code>below</code>'s
     *               parents
     */
    public static boolean isAbove(TreeNode above, TreeNode below) {
        if (above == null) {
            throw new NullPointerException("above == null");
        }

        if (below == null) {
            throw new NullPointerException("below == null");
        }

        TreeNode parent = below.getParent();

        while (parent != null) {
            if (parent == above) {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }

    public static boolean isMouseOverTreePath(MouseEvent evt, JTree tree) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        return tree.getRowForLocation(evt.getX(), evt.getY()) > 0;
    }

    /**
     * Removes from a tree model node all it's children and notifies model
     * listeners.
     *
     * @param model model
     * @param node  a node of that model
     */
    public static void removeAllChildren(final DefaultTreeModel model, final DefaultMutableTreeNode node) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        if (node == null) {
            throw new NullPointerException("node == null");
        }

        synchronized (model) {
            int count = node.getChildCount();
            List<TreeNode> children = new ArrayList<TreeNode>(count);
            int[] indices = new int[count];

            for (int i = 0; i < count; i++) {
                children.add(node.getChildAt(i));
                indices[i] = i;
            }

            node.removeAllChildren();
            model.nodesWereRemoved(node, indices, children.toArray());
        }
    }

    public static List<TreeNode> getTreeNodeChildren(TreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        List<TreeNode> children = new ArrayList<TreeNode>(node.getChildCount());

        for (Enumeration<?> enumeration = node.children(); enumeration.hasMoreElements();) {
            Object nextElement = enumeration.nextElement();

            if (nextElement instanceof TreeNode) {
                children.add((DefaultMutableTreeNode) nextElement);
            }
        }

        return children;
    }

    public static List<DefaultMutableTreeNode> getDefaultMutableTreeNodeChildren(TreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>(node.getChildCount());

        for (Enumeration<?> enumeration = node.children(); enumeration.hasMoreElements();) {
            Object nextElement = enumeration.nextElement();

            if (nextElement instanceof DefaultMutableTreeNode) {
                children.add((DefaultMutableTreeNode) nextElement);
            }
        }

        return children;
    }
}
