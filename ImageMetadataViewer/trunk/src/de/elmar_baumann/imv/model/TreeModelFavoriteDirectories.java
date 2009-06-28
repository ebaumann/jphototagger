package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.database.DatabaseFavoriteDirectories;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.comparator.ComparatorTreeNodeLevel;
import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Favorite directories.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/15
 */
public final class TreeModelFavoriteDirectories extends DefaultTreeModel
        implements FocusListener {

    private static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode(
            Bundle.getString("TreeModelFavoriteDirectories.Root.DisplayName"));
    private final DatabaseFavoriteDirectories db;
    private final Object monitor = new Object();

    public TreeModelFavoriteDirectories() {
        super(ROOT);
        db = DatabaseFavoriteDirectories.INSTANCE;
        addDirectories();
        GUI.INSTANCE.getAppPanel().getTreeFavoriteDirectories().addFocusListener(
                this);
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent.equals(ROOT)) return super.getChildCount(parent);
        addChildren((DefaultMutableTreeNode) parent);
        return super.getChildCount(parent);
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node.equals(ROOT)) return super.isLeaf(node);
        addChildren((DefaultMutableTreeNode) node);
        return super.isLeaf(node);
    }

    public void insertFavorite(FavoriteDirectory favoriteDirectory) {
        synchronized (monitor) {
            favoriteDirectory.setIndex(getNextNewFavoriteIndex());
            if (!existsFavoriteDirectory(favoriteDirectory) &&
                    db.insertOrUpdateFavoriteDirectory(favoriteDirectory)) {
                addDirectory(favoriteDirectory);
            } else {
                errorMessage(favoriteDirectory.getFavoriteName(), Bundle.
                        getString(
                        "TreeModelFavoriteDirectories.ErrorMessage.ParamInsert"));
            }
        }
    }

    public void deleteFavorite(FavoriteDirectory favoriteDirctory) {
        synchronized (monitor) {
            DefaultMutableTreeNode favNode = getNode(favoriteDirctory);
            if (favNode != null &&
                    db.deleteFavoriteDirectory(
                    favoriteDirctory.getFavoriteName())) {
                removeNodeFromParent(favNode);
                for (Enumeration children = ROOT.children(); children.
                        hasMoreElements();) {
                    Object userObject = ((DefaultMutableTreeNode) children.
                            nextElement()).getUserObject();
                    int newIndex = 0;
                    if (userObject instanceof FavoriteDirectory) {
                        FavoriteDirectory fav = (FavoriteDirectory) userObject;
                        fav.setIndex(newIndex++);
                        db.updateFavoriteDirectory(fav.getFavoriteName(), fav);
                    }
                }
            } else {
                errorMessage(favoriteDirctory.getFavoriteName(), Bundle.
                        getString(
                        "TreeModelFavoriteDirectories.ErrorMessage.ParamDelete"));
            }
        }
    }

    public void replaceFavorite(FavoriteDirectory oldFavorite,
            FavoriteDirectory newFavorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode oldNode = getNode(oldFavorite);
            if (oldNode != null &&
                    db.updateFavoriteDirectory(oldFavorite.getFavoriteName(),
                    newFavorite)) {
                oldFavorite.setDirectoryName(newFavorite.getDirectoryName());
                oldFavorite.setFavoriteName(newFavorite.getFavoriteName());
                nodeChanged(oldNode);
            } else {
                errorMessage(oldFavorite.getFavoriteName(), Bundle.getString(
                        "TreeModelFavoriteDirectories.ErrorMessage.ParamUpdate"));
            }
        }
    }

    public void moveUpFavorite(FavoriteDirectory favorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveUp = getNode(favorite);
            if (nodeToMoveUp != null) {
                int indexNodeToMoveUp = ROOT.getIndex(nodeToMoveUp);
                boolean isFirstNode = indexNodeToMoveUp == 0;
                if (!isFirstNode) {
                    DefaultMutableTreeNode prevNode =
                            (DefaultMutableTreeNode) ROOT.getChildAt(
                            indexNodeToMoveUp - 1);
                    if (prevNode != null && updateFavoriteDirectory(
                            nodeToMoveUp.getUserObject(), indexNodeToMoveUp - 1) &&
                            updateFavoriteDirectory(prevNode.getUserObject(),
                            indexNodeToMoveUp)) {
                        removeNodeFromParent(prevNode);
                        insertNodeInto(prevNode, ROOT, indexNodeToMoveUp);
                    }
                }
            }
        }
    }

    public void moveDownFavorite(FavoriteDirectory favorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveDown = getNode(favorite);
            if (nodeToMoveDown != null) {
                int indexNodeToMoveDown = ROOT.getIndex(nodeToMoveDown);
                boolean isLastNode = indexNodeToMoveDown ==
                        ROOT.getChildCount() - 1;
                if (!isLastNode) {
                    DefaultMutableTreeNode nextNode =
                            (DefaultMutableTreeNode) ROOT.getChildAt(
                            indexNodeToMoveDown + 1);
                    if (nextNode != null && updateFavoriteDirectory(
                            nodeToMoveDown.getUserObject(),
                            indexNodeToMoveDown + 1) &&
                            updateFavoriteDirectory(nextNode.getUserObject(),
                            indexNodeToMoveDown)) {
                        removeNodeFromParent(nextNode);
                        insertNodeInto(nextNode, ROOT, indexNodeToMoveDown);
                    }
                }
            }
        }
    }

    private boolean updateFavoriteDirectory(Object userObject, int newIndex) {
        if (userObject instanceof FavoriteDirectory) {
            FavoriteDirectory favoriteDirectory = (FavoriteDirectory) userObject;
            favoriteDirectory.setIndex(newIndex);
            return db.updateFavoriteDirectory(
                    favoriteDirectory.getFavoriteName(),
                    favoriteDirectory);
        }
        return false;
    }

    private void addDirectories() {
        List<FavoriteDirectory> directories = db.getFavoriteDirectories();
        for (FavoriteDirectory directory : directories) {
            if (FileUtil.existsDirectory(directory.getDirectoryName())) {
                addDirectory(directory);
            } else {
                AppLog.logWarning(TreeModelFavoriteDirectories.class,
                        Bundle.getString(
                        "TreeModelFavoriteDirectories.ErrorMessage.DbDirectoryDoesNotExist",
                        directory.getDirectoryName()));
                db.deleteFavoriteDirectory(directory.getFavoriteName());
            }
        }
    }

    private void addDirectory(FavoriteDirectory directory) {
        DefaultMutableTreeNode dirNode = getNode(directory);
        if (dirNode == null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(directory);
            int childCount = ROOT.getChildCount();
            insertNodeInto(node, ROOT, childCount);
            addChildren(node);
            if (childCount == 1) { // Forcing repaint
                setRoot(ROOT);
            }
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
                   : userObject instanceof FavoriteDirectory
                     ? new File(((FavoriteDirectory) userObject).
                getDirectoryName())
                     : null;
        if (dir == null || !dir.isDirectory()) return;
        File[] subdirs = dir.listFiles(
                new DirectoryFilter(
                UserSettings.INSTANCE.getDefaultDirectoryFilterOptions()));
        if (subdirs == null) return;
        int childCount = parentNode.getChildCount();
        List<File> nodeChildrenDirs = new ArrayList<File>(childCount);
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object usrObj = child.getUserObject();
            if (usrObj instanceof File) {
                nodeChildrenDirs.add((File) usrObj);
            }
        }
        for (int i = 0; i < subdirs.length; i++) {
            if (!nodeChildrenDirs.contains(subdirs[i])) {
                insertNodeInto(new DefaultMutableTreeNode(subdirs[i]),
                        parentNode, parentNode.getChildCount());
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
            } else if (userObject instanceof FavoriteDirectory) {
                file = new File(
                        ((FavoriteDirectory) userObject).getDirectoryName());

            }
            if (file != null && !file.exists()) {
                nodesToRemove.add(child);
            }
        }
        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            Object userObject = childNodeToRemove.getUserObject();
            if (userObject instanceof FavoriteDirectory) {
                db.deleteFavoriteDirectory(
                        ((FavoriteDirectory) userObject).getDirectoryName());
            }
            removeNodeFromParent(childNodeToRemove);
        }
        return nodesToRemove.size();
    }

    // ROOT.getChildCount() is valid now, but if later there are other user
    // objects than FavoriteDirectory in nodes below the root, this will not
    // work
    private synchronized int getNextNewFavoriteIndex() {
        int index = 0;
        for (Enumeration children = ROOT.children(); children.hasMoreElements();) {
            Object userObject =
                    ((DefaultMutableTreeNode) children.nextElement()).
                    getUserObject();
            if (userObject instanceof FavoriteDirectory) {
                index++;
            }
        }
        return index;
    }

    private DefaultMutableTreeNode getNode(FavoriteDirectory favoriteDirectory) {
        for (Enumeration children = ROOT.children(); children.hasMoreElements();) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.
                    nextElement();
            if (favoriteDirectory.equals(child.getUserObject())) {
                return child;
            }
        }
        return null;
    }

    private boolean existsFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        return getNode(favoriteDirectory) != null;
    }

    private void errorMessage(String favoriteName, String cause) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString(
                "TreeModelFavoriteDirectories.ErrorMessage.Template",
                favoriteName, cause),
                Bundle.getString(
                "TreeModelFavoriteDirectories.ErrorMessage.Template.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates this model: Adds nodes for new files, deletes nodes with not
     * existing files.
     */
    public void update() {
        updateAdd();
        updateRemove();
    }

    private void updateAdd() {
        List<DefaultMutableTreeNode> nodes = getAllNodes();
        for (DefaultMutableTreeNode node : nodes) {
            addChildren(node);
        }
    }

    private void updateRemove() {
        List<DefaultMutableTreeNode> nodes = getAllNodes();
        Collections.sort(nodes, ComparatorTreeNodeLevel.INSTANCE_DESCENDING);
        for (DefaultMutableTreeNode node : nodes) {
            removeChildrenWithNotExistingFiles(node);
        }
    }

    private List<DefaultMutableTreeNode> getAllNodes() {
        List<DefaultMutableTreeNode> nodes =
                new LinkedList<DefaultMutableTreeNode>();
        for (Enumeration e = ROOT.depthFirstEnumeration(); e.hasMoreElements();) {
            nodes.add((DefaultMutableTreeNode) e.nextElement());
        }
        return nodes;
    }

    @Override
    public void focusGained(FocusEvent e) {
        update();
    }

    @Override
    public void focusLost(FocusEvent e) {
        // nothing to do
    }
}
