package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.comparator.ComparatorFilesNames;
import de.elmar_baumann.lib.comparator.ComparatorTreeNodeLevel;
import de.elmar_baumann.lib.io.DirectoryFilter;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;

/**
 * Tree model for all directories of a file system. All nodes have the type
 * {@link DefaultMutableTreeNode} and their user object is a file with exception
 * of the root directory.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/29
 */
public final class TreeModelAllSystemDirectories extends DefaultTreeModel
        implements TreeWillExpandListener {

    private final DirectoryFilter directoryFilter;
    private final DefaultMutableTreeNode rootNode;
    private final JTree tree;

    /**
     * Constructor.
     *
     * @param tree            tree using this model. When the tree will expand
     *                        new subdirectories of the expanded node will be
     *                        added
     * @param directoryFilter filter, determines which directories are displayed
     */
    public TreeModelAllSystemDirectories(
            JTree tree, Set<DirectoryFilter.Option> directoryFilter) {
        super(new DefaultMutableTreeNode("Root of TreeModelAllSystemDirectories"));
        rootNode = (DefaultMutableTreeNode) getRoot();
        this.tree = tree;
        this.directoryFilter = new DirectoryFilter(directoryFilter);
        addRootDirectories();
        tree.addTreeWillExpandListener(this);
    }

    private void addRootDirectories() {
        File[] roots = File.listRoots();
        if (roots == null) return;
        List<File> rootDirs = Arrays.asList(roots);
        Collections.sort(
                rootDirs, ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE);

        for (File dir : rootDirs) {
            DefaultMutableTreeNode rootDirNode = new DefaultMutableTreeNode(dir);
            insertNodeInto(rootDirNode, rootNode, rootNode.getChildCount());
            addChildren(rootDirNode);
        }
    }

    private void addChildren(DefaultMutableTreeNode parentNode) {
        Object parentUserObject = parentNode.getUserObject();
        File dir = parentUserObject instanceof File
                   ? (File) parentUserObject
                   : null;
        if (dir == null || !dir.isDirectory()) return;
        File[] subdirs = dir.listFiles(directoryFilter);
        if (subdirs == null) return;
        int childCount = parentNode.getChildCount();
        List<File> nodeChildDirs = new ArrayList<File>(childCount);
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object usrerObjectChild = child.getUserObject();
            if (usrerObjectChild instanceof File) {
                nodeChildDirs.add((File) usrerObjectChild);
            }
        }
        for (int i = 0; i < subdirs.length; i++) {
            if (!nodeChildDirs.contains(subdirs[i])) {
                insertNodeInto(new DefaultMutableTreeNode(subdirs[i]),
                        parentNode, childCount++);
            }
        }
    }

    private int removeChildrenWithNotExistingFiles(
            DefaultMutableTreeNode parentNode) {
        int childCount = parentNode.getChildCount();
        List<DefaultMutableTreeNode> nodesToRemove =
                new ArrayList<DefaultMutableTreeNode>();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object userObject = child.getUserObject();
            File file = null;
            if (userObject instanceof File) {
                file = (File) userObject;
            }
            if (file != null && !file.exists()) {
                nodesToRemove.add(child);
            }
        }
        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            removeNodeFromParent(childNodeToRemove);
        }
        return nodesToRemove.size();
    }

    /**
     * Updates this model: Adds nodes for new files, deletes nodes with not
     * existing files.
     */
    public void update() {
        Cursor treeCursor = tree.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        tree.setCursor(waitCursor);
        for (DefaultMutableTreeNode node : getTreeRowNodes()) {
            addChildren(node);
            removeChildrenWithNotExistingFiles(node);
        }
        tree.setCursor(treeCursor);
    }

    private List<DefaultMutableTreeNode> getTreeRowNodes() {
        int rows = tree.getRowCount();
        List<DefaultMutableTreeNode> nodes =
                new ArrayList<DefaultMutableTreeNode>(rows);
        for (int i = 0; i < rows; i++) {
            nodes.add((DefaultMutableTreeNode) tree.getPathForRow(i).
                    getLastPathComponent());
        }
        return nodes;
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws
            ExpandVetoException {
        Cursor treeCursor = tree.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        tree.setCursor(waitCursor);
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        if (node.getChildCount() == 0) {
            addChildren(node);
        }
        for (Enumeration children = node.children(); children.hasMoreElements();) {
            addChildren((DefaultMutableTreeNode) children.nextElement());
        }
        tree.setCursor(treeCursor);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws
            ExpandVetoException {
        // Nothing to do
    }
}
