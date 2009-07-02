package de.elmar_baumann.lib.componentutil;

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
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Werkzeuge für Trees.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TreeUtil {

    /**
     * Liefert, ob der Mauszeiger über einem selektierten Item steht.
     * 
     * @param  e  Mausereigenis
     * @return true, falls der Zeiger über einem selektierten Item steht
     */
    public static boolean isSelectedItemPosition(MouseEvent e) {
        if (e == null)
            throw new NullPointerException("e == null");

        if (e.getSource() instanceof JTree) {
            JTree tree = (JTree) e.getSource();
            TreePath mousePath = tree.getPathForLocation(e.getX(), e.getY());
            if (mousePath != null && tree.getSelectionPath() != null) {
                Object selectedItem = tree.getSelectionPath().
                        getLastPathComponent();
                Object mouseItem = mousePath.getLastPathComponent();
                if (selectedItem != null && mouseItem != null) {
                    return selectedItem.equals(mouseItem);
                }
            }
        }
        return false;
    }

    /**
     * Liefert, ob der Mauszeiger über dem Wurzelitem steht.
     * 
     * @param  e  Mausereigenis
     * @return true, falls der Zeiger über dem Wurzelitem steht
     */
    public static boolean isRootItemPosition(MouseEvent e) {
        if (e == null)
            throw new NullPointerException("e == null");

        if (e.getSource() instanceof JTree) {
            JTree tree = (JTree) e.getSource();
            TreePath mousePath = tree.getPathForLocation(e.getX(), e.getY());
            if (mousePath != null) {
                Object root = tree.getModel().getRoot();
                Object mouseItem = mousePath.getLastPathComponent();
                if (root != null && mouseItem != null) {
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
        if (trees == null)
            throw new NullPointerException("trees == null");

        for (JTree tree : trees) {
            if (tree.getSelectionCount() > 0) {
                tree.clearSelection();
            }
        }
    }

    /**
     * Liefert aus einem Model den Pfad anhand eines Strings, der diesen spezifiziert.
     * 
     * @param treeModel     Model
     * @param pathString    String mit Pfad
     * @param pathSeparator Separator zwischen den einzelnen Pfadbestandteilen
     * @return              Pfad oder null, wenn nicht gefunden
     */
    public static TreePath getTreePath(TreeModel treeModel, String pathString,
            String pathSeparator) {
        if (treeModel == null)
            throw new NullPointerException("treeModel == null");
        if (pathString == null)
            throw new NullPointerException("pathString == null");
        if (pathSeparator == null)
            throw new NullPointerException("pathSeparator == null");

        StringTokenizer tokenizer = new StringTokenizer(pathString,
                pathSeparator);
        int tokenCount = tokenizer.countTokens();
        int tokenNumber = 1;
        int tokenFoundCount = 0;
        Object[] path = new Object[tokenCount > 0
                                   ? tokenCount
                                   : 1];
        if (tokenCount > 0) {
            path[0] = treeModel.getRoot();
            tokenizer.nextToken();
            Object currentElement = treeModel.getRoot();
            Object childElement = null;
            boolean appended = true;

            while (appended && tokenNumber < tokenCount) {
                int childCount = treeModel.getChildCount(currentElement);
                String pathToken = tokenizer.nextToken();
                boolean found = false;
                appended = false;
                for (int index = 0; index < childCount && !found; index++) {
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
        return tokenCount > 0 && tokenCount - 1 == tokenFoundCount
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
        if (tree == null)
            throw new NullPointerException("tree == null");
        if (path == null)
            throw new NullPointerException("path == null");

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
        if (tree == null)
            throw new NullPointerException("tree == null");
        if (path == null)
            throw new NullPointerException("path == null");

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
     * Returns the tree path of a file, each path component is a parent
     * (child) file of a file.
     *
     * <em>Expects that the nodes of the tree having the type
     * {@link DefaultMutableTreeNode}!</em>
     * 
     * @param  file   file
     * @param  model  model when the root is not a file, else null
     * @return path
     */
    public static TreePath getTreePath(File file, TreeModel model) {
        if (file == null)
            throw new NullPointerException("file == null");
        if (model == null)
            throw new NullPointerException("model == null");

        Stack<DefaultMutableTreeNode> stack =
                new Stack<DefaultMutableTreeNode>();
        while (file != null) {
            stack.push(new DefaultMutableTreeNode(file));
            file = file.getParentFile();
        }
        List<DefaultMutableTreeNode> nodes =
                new ArrayList<DefaultMutableTreeNode>(stack.size() + 1);
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
    public static void addNodesUserWithObject(
            Collection<? super DefaultMutableTreeNode> foundNodes,
            DefaultMutableTreeNode rootNode, Object userObject, int maxCount) {
        if (foundNodes == null)
            throw new NullPointerException("foundNodes == null");
        if (rootNode == null)
            throw new NullPointerException("rootNode == null");
        if (userObject == null)
            throw new NullPointerException("userObject == null");

        int foundNodeCount = foundNodes.size();
        for (Enumeration children = rootNode.children();
                children.hasMoreElements() && foundNodeCount <= maxCount;) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.
                    nextElement();
            if (userObject.equals(child.getUserObject())) {
                foundNodes.add(child);
            } else {
                addNodesUserWithObject(foundNodes, child, userObject, maxCount); // recursive
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
    public static DefaultMutableTreeNode findChildNodeWithFile(
            DefaultMutableTreeNode parentNode, File file) {
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

    private TreeUtil() {
    }
}
