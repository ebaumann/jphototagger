package org.jphototagger.lib.model;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.util.StringUtil;

import java.awt.Cursor;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Stack;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/**
 * Tree model for all directories of a file system. All nodes have the type
 * {@link DefaultMutableTreeNode} and their user object is a file with exception
 * of the root directory.
 *
 * @author Elmar Baumann
 */
public final class TreeModelAllSystemDirectories extends DefaultTreeModel implements TreeWillExpandListener {
    private static final long serialVersionUID = 8297582930734874242L;
    private final transient DirectoryFilter directoryFilter;
    private final DefaultMutableTreeNode rootNode;
    private final JTree tree;
    private final List<File> excludeRootDirectories = new ArrayList<File>();
    private static final Logger LOGGER = Logger.getLogger(TreeModelAllSystemDirectories.class.getName());

    /**
     * Constructor.
     *
     * @param tree            tree using this model. When the tree will expand
     *                        new subdirectories of the expanded node will be
     *                        added
     * @param directoryFilter filterFiles, determines which directories are displayed
     */
    public TreeModelAllSystemDirectories(JTree tree, DirectoryFilter.Option... directoryFilter) {
        this(tree, Collections.<File>emptyList(), directoryFilter);
    }

    public TreeModelAllSystemDirectories(JTree tree, Collection<? extends File> excludeRootDirectories,
            DirectoryFilter.Option... directoryFilter) {
        super(new TreeNodeSortedChildren("Root of TreeModelAllSystemDirectories"));
        rootNode = (DefaultMutableTreeNode) getRoot();
        this.tree = tree;
        this.directoryFilter = new DirectoryFilter(directoryFilter);
        this.excludeRootDirectories.addAll(excludeRootDirectories);
        addRootDirectories();
        tree.addTreeWillExpandListener(this);
    }

    private void addRootDirectories() {
        LOGGER.log(Level.FINEST, "Reading root directories...");

        File[] roots = File.listRoots();

        LOGGER.log(Level.FINEST, "Root directories have been read: {0}", StringUtil.toString(roots));
        LOGGER.log(Level.FINEST, "Root directories to exclude: {0}: ", excludeRootDirectories);

        if (roots == null) {
            return;
        }

        List<File> existingRootDirectories = Arrays.asList(roots);

        for (File existingRootDirectory : existingRootDirectories) {
            boolean isExclude = excludeRootDirectories.contains(existingRootDirectory);

            if (!isExclude) {
            DefaultMutableTreeNode rootDirectoryNode = new TreeNodeSortedChildren(existingRootDirectory);

            insertNodeInto(rootDirectoryNode, rootNode, rootNode.getChildCount());
            addChildren(rootDirectoryNode);
        }
    }
    }

    private void addChildren(DefaultMutableTreeNode parentNode) {
        Object parentNodeUserObject = parentNode.getUserObject();
        File parentDirectory = (parentNodeUserObject instanceof File)
                   ? (File) parentNodeUserObject
                   : null;

        if ((parentDirectory == null) ||!parentDirectory.isDirectory()) {
            return;
        }

        LOGGER.log(Level.FINEST, "Reading subdirectories of ''{0}''...", parentDirectory);

        File[] existingSubdirectories = parentDirectory.listFiles(directoryFilter);

        LOGGER.log(Level.FINEST, "Subdirectories of ''{0}'' have been read: {1}", 
                new Object[] { parentDirectory, StringUtil.toString(existingSubdirectories) });

        if (existingSubdirectories == null) {
            return;
        }

        int childCount = parentNode.getChildCount();
        List<File> parentNodeChildDirectories = new ArrayList<File>(childCount);

        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object childNodeUserObject = childNode.getUserObject();

            if (childNodeUserObject instanceof File) {
                parentNodeChildDirectories.add((File) childNodeUserObject);
            }
        }

        for (int i = 0; i < existingSubdirectories.length; i++) {
            final File existingSubdirectory = existingSubdirectories[i];

            if (!parentNodeChildDirectories.contains(existingSubdirectory)) {
                LOGGER.log(Level.FINEST, "Adding subdirectory ''{0}'' to node ''{1}''",
                           new Object[] { existingSubdirectory, parentNode });

                DefaultMutableTreeNode newChildNode = new TreeNodeSortedChildren(existingSubdirectory);

                parentNode.add(newChildNode);

                int newChildIndex = parentNode.getIndex(newChildNode);

                fireTreeNodesInserted(this, parentNode.getPath(), new int[] { newChildIndex }, new Object[] { newChildNode });
                LOGGER.log(Level.FINEST, "Subdirectory ''{0}'' has been added to node ''{1}''", 
                           new Object[] { existingSubdirectory, parentNode });
            }
        }
    }

    private int removeChildrenWithNotExistingFiles(DefaultMutableTreeNode parentNode) {
        int childCount = parentNode.getChildCount();
        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object userObject = child.getUserObject();
            File file = null;

            if (userObject instanceof File) {
                file = (File) userObject;
            }

            if ((file != null) && !file.exists()) {
                nodesToRemove.add(child);
            }
        }

        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            LOGGER.log(Level.FINEST, "Removing child node ''{0}'' from parent node ''{1}''...",
                       new Object[] { childNodeToRemove, parentNode });
            removeNodeFromParent(childNodeToRemove);
            LOGGER.log(Level.FINEST, "Child node ''{0}'' has been removed from parent node ''{1}''",
                       new Object[] { childNodeToRemove, parentNode });
        }

        return nodesToRemove.size();
    }

    /**
     * Creates a new directory as child of a node. Let's the user input the
     * new name and inserts the new created directory.
     *
     * @param  parentNode parent node. If null, nothing will be done.
     * @return            created directory or null if not created
     */
    public File createDirectoryIn(DefaultMutableTreeNode parentNode) {
        File parentNodeDirectory = (parentNode == null)
                         ? null
                         : TreeFileSystemDirectories.getFile(parentNode);

        if (parentNodeDirectory != null) {
            File createdDirectory = TreeFileSystemDirectories.createDirectoryIn(parentNodeDirectory);

            if (createdDirectory != null) {
                TreeNodeSortedChildren createdDirNode = new TreeNodeSortedChildren(createdDirectory);

                parentNode.add(createdDirNode);

                int childIndex = parentNode.getIndex(createdDirNode);

                fireTreeNodesInserted(this, parentNode.getPath(), new int[] { childIndex }, new Object[] { createdDirNode });

                return createdDirectory;
            }
        }

        return null;
    }

    /**
     * Expands the tree to a specific file.
     *
     * @param file   file
     * @param select if true the file node will be selected
     */
    public void expandToFile(File file, boolean select) {
        Stack<File> filePath = FileUtil.getPathFromRoot(file);
        DefaultMutableTreeNode node = rootNode;

        while ((node != null) &&!filePath.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(node, filePath.pop());

            if ((node != null) && (node.getChildCount() <= 0)) {
                addChildren(node);
            }
        }

        if ((node != null) && (node.getParent() != null)) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

            if (parent.getPath() != null) {
                tree.expandPath(new TreePath(parent.getPath()));

                if (select) {
                    TreePath path = new TreePath(node.getPath());

                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            }
        }
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
        List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>(rows);

        for (int i = 0; i < rows; i++) {
            nodes.add((DefaultMutableTreeNode) tree.getPathForRow(i).getLastPathComponent());
        }

        return nodes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        Cursor treeCursor = tree.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

        tree.setCursor(waitCursor);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

        LOGGER.log(Level.FINEST, "Node ''{0}'' has been expanded, adding children...", node);

        if (node.getChildCount() == 0) {
            addChildren(node);
        }

        List<DefaultMutableTreeNode> children = TreeUtil.getDefaultMutableTreeNodeChildren(node);
        
        for (DefaultMutableTreeNode child : children) {
            addChildren(child);
        }

        LOGGER.log(Level.FINEST, "Children were added to node ''{0}'' after expanding", node);
        tree.setCursor(treeCursor);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

        // ignore
    }
}
