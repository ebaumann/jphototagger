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
package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/**
 * Tree model for all directories of a file system. All nodes have the type
 * {@link DefaultMutableTreeNode} and their user object is a file with exception
 * of the root directory.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-29
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
        super(new TreeNodeSortedChildren("Root of TreeModelAllSystemDirectories")); // NOI18N
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
        for (File dir : rootDirs) {
            DefaultMutableTreeNode rootDirNode = new TreeNodeSortedChildren(dir);
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
                DefaultMutableTreeNode newChild =
                        new TreeNodeSortedChildren(subdirs[i]);
                parentNode.add(newChild);
                int childIndex = parentNode.getIndex(newChild);
                fireTreeNodesInserted(this, parentNode.getPath(),
                        new int[]{childIndex}, new Object[]{newChild});
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
     * Creates a new directory as child of a node. Let's the user input the
     * new name and inserts the new created directory.
     *
     * @param parentNode parent node. If null, nothing will be done.
     */
    public void createNewDirectory(DefaultMutableTreeNode parentNode) {
        File dirOfParentNode = parentNode == null
                ? null
                : TreeFileSystemDirectories.getFile(parentNode);
        if (dirOfParentNode != null) {
            File newDir =
                    TreeFileSystemDirectories.createSubDirectory(dirOfParentNode);
            if (newDir != null) {
                TreeNodeSortedChildren newDirNode = new TreeNodeSortedChildren(
                        newDir);
                parentNode.add(newDirNode);
                int childIndex = parentNode.getIndex(newDirNode);
                fireTreeNodesInserted(this, parentNode.getPath(),
                        new int[]{childIndex}, new Object[]{newDirNode});
            }
        }
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
        while (node != null && !filePath.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(
                    node, filePath.pop());
            if (node != null && node.getChildCount() <= 0) {
                addChildren(node);
            }
        }
        if (node != null && node.getParent() != null) {
            DefaultMutableTreeNode parent =
                    (DefaultMutableTreeNode) node.getParent();
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
        // ignore
    }
}
