/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.database.DatabaseFavorites;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import de.elmar_baumann.lib.model.TreeNodeSortedChildren;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Elements are {@link DefaultMutableTreeNode}s with the user objects listed below.
 * 
 * <ul>
 * <li>The root user object is a {@link String}</li>
 * <li>All user objects in the first level below the root are
 *     {@link Favorite}s retrieved through {@link DatabaseFavorites#getAll()}
 * </li>
 * <li>User objects below the favorites are directory {@link File}s</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-15
 */
public final class TreeModelFavorites extends DefaultTreeModel
        implements TreeWillExpandListener, AppExitListener {

    private static final String                      KEY_SELECTED_FAV_NAME = "TreeModelFavorites.SelFavDir";
    private static final String                      KEY_SELECTED_DIR      = "TreeModelFavorites.SelDir";
    private static final long                        serialVersionUID      = -2453748094818942669L;
    private final        DefaultMutableTreeNode      rootNode;
    private final        DatabaseFavorites db;
    private final        JTree                       tree;
    private final        Object                      monitor               = new Object();

    public TreeModelFavorites(JTree tree) {
        super(new DefaultMutableTreeNode(Bundle.getString("TreeModelFavorites.Root.DisplayName")));
        this.tree = tree;
        rootNode  = (DefaultMutableTreeNode) getRoot();
        tree.addTreeWillExpandListener(this);
        db = DatabaseFavorites.INSTANCE;
        addDirectories();
        AppLifeCycle.INSTANCE.addAppExitListener(this);
    }

    public void insert(Favorite favoriteDirectory) {
        synchronized (monitor) {
            favoriteDirectory.setIndex(getNextNewFavoriteIndex());
            if (!existsFavoriteDirectory(favoriteDirectory) &&
                 db.insertOrUpdate(favoriteDirectory)) {
                addDirectory(favoriteDirectory);
            } else {
                errorMessage(favoriteDirectory.getName(), Bundle.getString("TreeModelFavorites.Error.ParamInsert"));
            }
        }
    }

    public void delete(Favorite favoriteDirctory) {
        synchronized (monitor) {
            DefaultMutableTreeNode favNode = getNode(favoriteDirctory);
            if (favNode != null && db.delete(favoriteDirctory.getName())) {
                removeNodeFromParent(favNode);
                for (@SuppressWarnings("unchecked")Enumeration<DefaultMutableTreeNode> children = rootNode.children(); children.hasMoreElements();) {
                    Object userObject = children.nextElement().getUserObject();
                    int newIndex = 0;
                    if (userObject instanceof Favorite) {
                        Favorite fav = (Favorite) userObject;
                        fav.setIndex(newIndex++);
                        db.update(fav.getName(), fav);
                    }
                }
            } else {
                errorMessage(favoriteDirctory.getName(), Bundle.getString("TreeModelFavorites.Error.ParamDelete"));
            }
        }
    }

    public void update(Favorite oldFavorite, Favorite newFavorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode oldNode = getNode(oldFavorite);
            if (oldNode != null && db.update(oldFavorite.getName(), newFavorite)) {
                oldFavorite.setDirectoryName(newFavorite.getDirectoryName());
                oldFavorite.setName(newFavorite.getName());
                nodeChanged(oldNode);
            } else {
                errorMessage(oldFavorite.getName(), Bundle.getString("TreeModelFavorites.Error.ParamUpdate"));
            }
        }
    }

    public void moveUpFavorite(Favorite favorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveUp = getNode(favorite);
            if (nodeToMoveUp != null) {
                int     indexNodeToMoveUp = rootNode.getIndex(nodeToMoveUp);
                boolean isFirstNode       = indexNodeToMoveUp == 0;
                if (!isFirstNode) {
                    DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) rootNode.getChildAt(indexNodeToMoveUp - 1);
                    if (prevNode != null &&
                        updateFavoriteDirectory(nodeToMoveUp.getUserObject(), indexNodeToMoveUp - 1) &&
                        updateFavoriteDirectory(prevNode.getUserObject(), indexNodeToMoveUp)
                        ) {
                        removeNodeFromParent(prevNode);
                        insertNodeInto(prevNode, rootNode, indexNodeToMoveUp);
                    }
                }
            }
        }
    }

    public void moveDownFavorite(Favorite favorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveDown = getNode(favorite);
            if (nodeToMoveDown != null) {
                int indexNodeToMoveDown = rootNode.getIndex(nodeToMoveDown);
                boolean isLastNode = indexNodeToMoveDown == rootNode.getChildCount() - 1;
                if (!isLastNode) {
                    DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)
                                                      rootNode.getChildAt(indexNodeToMoveDown + 1);
                    if (nextNode != null && 
                        updateFavoriteDirectory(nodeToMoveDown.getUserObject(), indexNodeToMoveDown + 1) &&
                        updateFavoriteDirectory(nextNode.getUserObject(),
                        indexNodeToMoveDown)
                        ) {
                        removeNodeFromParent(nextNode);
                        insertNodeInto(nextNode, rootNode, indexNodeToMoveDown);
                    }
                }
            }
        }
    }

    private boolean updateFavoriteDirectory(Object userObject, int newIndex) {
        if (userObject instanceof Favorite) {
            Favorite favoriteDirectory = (Favorite) userObject;
            favoriteDirectory.setIndex(newIndex);
            return db.update(
                    favoriteDirectory.getName(),
                    favoriteDirectory);
        }
        return false;
    }

    private void addDirectories() {
        List<Favorite> directories = db.getAll();
        for (Favorite directory : directories) {
            if (FileUtil.existsDirectory(directory.getDirectory())) {
                addDirectory(directory);
            } else {
                errorMessageAddDirectory(directory);
                db.delete(directory.getName());
            }
        }
    }

    private void addDirectory(Favorite directory) {
        DefaultMutableTreeNode dirNode = getNode(directory);
        if (dirNode == null) {
            DefaultMutableTreeNode node = new TreeNodeSortedChildren(directory);
            insertNodeInto(node, rootNode, rootNode.getChildCount());
            addChildren(node);
            tree.expandPath(new TreePath(rootNode.getPath()));
        }
    }

    /**
     * Adds to a parent node not existing children where the user object is
     * a directory if the user object of the node is a directory or a favorite
     * directory (wich refers to a directory). The children are child
     * directories of the directory (user object).
     *
     * @param parentNode parent note which gets the new children
     */
    private void addChildren(DefaultMutableTreeNode parentNode) {
        Object userObject = parentNode.getUserObject();
        File dir = userObject instanceof File
                   ? (File) userObject
                   : userObject instanceof Favorite
                   ? ((Favorite) userObject).getDirectory()
                   : null;
        if (dir == null || !dir.isDirectory()) return;
        File[] subdirs = dir.listFiles(new DirectoryFilter(UserSettings.INSTANCE.getDefaultDirectoryFilterOptions()));
        if (subdirs == null) return;
        int childCount = parentNode.getChildCount();
        List<File> nodeChildrenDirs = new ArrayList<File>(childCount);
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object usrObj = child.getUserObject();
            if (usrObj instanceof File) {
                nodeChildrenDirs.add((File) usrObj);
            }
        }
        for (int i = 0; i < subdirs.length; i++) {
            if (!nodeChildrenDirs.contains(subdirs[i])) {
                DefaultMutableTreeNode newChild = new TreeNodeSortedChildren(subdirs[i]);
                parentNode.insert(newChild, childCount++);
                int childIndex = parentNode.getIndex(newChild);
                fireTreeNodesInserted(this, parentNode.getPath(), new int[]{childIndex}, new Object[]{newChild});
            }
        }
    }

    /**
     * Removes from a node child nodes with files as user objects when the
     * file does not exist.
     *
     * @param  parentNode parent node
     * @return            count of removed nodes
     */
    private int removeChildrenWithNotExistingFiles(DefaultMutableTreeNode parentNode) {
        int                          childCount    = parentNode.getChildCount();
        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child      = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object                 userObject = child.getUserObject();
            File                   file       = null;

            if (userObject instanceof File) {
                file = (File) userObject;
            } else if (userObject instanceof Favorite) {
                file = ((Favorite) userObject).getDirectory();
            }
            if (file != null && !file.exists()) {
                nodesToRemove.add(child);
            }
        }
        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            Object userObject = childNodeToRemove.getUserObject();
            if (userObject instanceof Favorite) {
                db.delete(((Favorite) userObject).getDirectoryName());
            }
            removeNodeFromParent(childNodeToRemove);
        }
        return nodesToRemove.size();
    }

    // ROOT.getChildCount() is valid now, but if later there are other user
    // objects than Favorite in nodes below the root, this will not
    // work
    private synchronized int getNextNewFavoriteIndex() {
        int index = 0;
        for (@SuppressWarnings("unchecked")Enumeration<DefaultMutableTreeNode> children = rootNode.children(); children.hasMoreElements();) {
            Object userObject = children.nextElement().getUserObject();
            if (userObject instanceof Favorite) {
                index++;
            }
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode getNode(Favorite favoriteDirectory) {
        for (Enumeration<DefaultMutableTreeNode> children = rootNode.children(); children.hasMoreElements();) {
            DefaultMutableTreeNode child = children.nextElement();

            if (favoriteDirectory.equals(child.getUserObject())) return child;
            
        }
        return null;
    }

    private boolean existsFavoriteDirectory(Favorite favoriteDirectory) {
        return getNode(favoriteDirectory) != null;
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
                               : getDirectory(parentNode);
        if (dirOfParentNode != null) {
            File newDir = TreeFileSystemDirectories.createSubDirectory(dirOfParentNode);
            if (newDir != null) {
                TreeNodeSortedChildren newDirNode = new TreeNodeSortedChildren(newDir);
                parentNode.add(newDirNode);
                int childIndex = parentNode.getIndex(newDirNode);
                fireTreeNodesInserted(this, parentNode.getPath(), new int[]{childIndex}, new Object[]{newDirNode});
            }
        }
    }

    private File getDirectory(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        return node == null
               ? null
               : userObject instanceof Favorite
               ? ((Favorite) userObject).getDirectory()
               : userObject instanceof File
               ? (File) userObject
               : null;
    }

    private Stack<File> getFilePathToNode(DefaultMutableTreeNode node, File file) {
        if (node == null) return null;
        Object userObject = node.getUserObject();
        File   nodeFile   = userObject instanceof File
                            ? (File) userObject
                            : userObject instanceof Favorite
                            ? ((Favorite) userObject).getDirectory()
                            : null;
        if (nodeFile != null) {
            Stack<File> filePath    = FileUtil.getPathFromRoot(file);
            File        filePathTop = filePath.peek();
            while (!filePath.isEmpty() && !nodeFile.equals(filePathTop)) {
                filePathTop = filePath.pop();
            }
            return filePath;
        }
        return null;
    }

    /**
     * Expands the tree to a specific file.
     *
     * @param favoriteName favorite containing this file
     * @param file         file
     * @param select       if true the file node will be selected
     */
    private void expandToFile(String favoriteName, File file, boolean select) {
        DefaultMutableTreeNode node = getFavorite(favoriteName);
        Stack<File> filePathToFavorite = getFilePathToNode(node, file);
        if (filePathToFavorite == null) return;
        while (node != null && !filePathToFavorite.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(node,
                    filePathToFavorite.pop());
            if (node != null && node.getChildCount() <= 0) {
                addChildren(node);
            }
        }
        if (node != null) {
            TreePath nodeParentsPath = new TreePath(((DefaultMutableTreeNode) node.getParent()).getPath());
            tree.expandPath(nodeParentsPath);
            TreePath nodePath = new TreePath(node.getPath());
            if (select) {
                tree.setSelectionPath(nodePath);
            }
            tree.scrollPathToVisible(nodePath);
        }
    }

    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode getFavorite(String name) {
        for (Enumeration<DefaultMutableTreeNode> children = rootNode.children(); children.hasMoreElements();) {
            DefaultMutableTreeNode childNode = children.nextElement();
            Object userObject = childNode.getUserObject();
            if (userObject instanceof Favorite) {
                Favorite fav = (Favorite) userObject;
                if (name.equals(fav.getName())) return childNode;
            }
        }
        return null;
    }

    public void readFromProperties() {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String favname = properties.getProperty(KEY_SELECTED_FAV_NAME);
        String dirname = properties.getProperty(KEY_SELECTED_DIR);
        if (favname != null && dirname != null && !favname.trim().isEmpty() &&
           !dirname.trim().isEmpty()
           ) {
            expandToFile(favname.trim(), new File(dirname.trim()), true);
        } else if (favname != null) {
            DefaultMutableTreeNode fav = getFavorite(favname.trim());
            if (fav != null) {
                TreePath path = new TreePath(fav.getPath());
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
            }
        }
    }

    private void writeToProperties() {
        if (tree.getSelectionCount() > 0) {
            TreePath path = tree.getSelectionPath();
            Object o = path.getLastPathComponent();
            if (o instanceof DefaultMutableTreeNode) {
                String favname = null;
                String dirname = null;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                Object userObject = node.getUserObject();
                if (userObject instanceof Favorite) {
                    favname = ((Favorite) userObject).getName();
                } else if (userObject instanceof File) {
                    File file = ((File) userObject);
                    dirname = file.getAbsolutePath();
                    Favorite favDir = getParentFavDir(node);
                    if (favDir != null) {
                        favname = favDir.getName();
                    }
                }
                Properties properties = UserSettings.INSTANCE.getProperties();
                if (dirname == null) {
                    properties.remove(KEY_SELECTED_DIR);
                } else {
                    properties.setProperty(KEY_SELECTED_DIR, dirname);
                }
                if (favname == null) {
                    properties.remove(KEY_SELECTED_FAV_NAME);
                } else {
                    properties.setProperty(KEY_SELECTED_FAV_NAME, favname);
                }
            }
        } else {
            Properties properties = UserSettings.INSTANCE.getProperties();
            properties.remove(KEY_SELECTED_DIR);
            properties.remove(KEY_SELECTED_FAV_NAME);
        }
        UserSettings.INSTANCE.writeToFile();
    }

    private Favorite getParentFavDir(DefaultMutableTreeNode childNode) {
        TreeNode parentNode = childNode.getParent();
        while (parentNode instanceof DefaultMutableTreeNode &&
              !parentNode.equals(rootNode)
              ) {
            Object userObject = ((DefaultMutableTreeNode) parentNode).getUserObject();
            if (userObject instanceof Favorite) {
                return (Favorite) userObject;
            }
            parentNode = parentNode.getParent();
        }
        return null;
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
        if (node.getChildCount() == 0) {
            addChildren(node);
        }
        for (Enumeration<DefaultMutableTreeNode> children = node.children(); children.hasMoreElements();) {
            addChildren(children.nextElement());
        }
        tree.setCursor(treeCursor);
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws
            ExpandVetoException {
    }

    @Override
    public void appWillExit() {
        writeToProperties();
    }

    private void errorMessage(String favoriteName, String cause) {
        MessageDisplayer.error(
                null,
                "TreeModelFavorites.Error.Template",
                favoriteName, cause);
    }

    private void errorMessageAddDirectory(Favorite directory) {
        AppLogger.logWarning(
                TreeModelFavorites.class,
                "TreeModelFavorites.Error.DbDirectoryDoesNotExist",
                directory.getDirectoryName());
    }
}
