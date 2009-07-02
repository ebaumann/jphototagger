package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.database.DatabaseFavoriteDirectories;
import de.elmar_baumann.imv.event.listener.AppExitListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.model.TreeNodeSortedChildren;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Favorite directories.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/15
 */
public final class TreeModelFavorites extends DefaultTreeModel
        implements TreeWillExpandListener, AppExitListener {

    private static final String KEY_SELECTED_FAV_NAME =
            "TreeModelFavorites.SelFavDir";
    private static final String KEY_SELECTED_DIR = "TreeModelFavorites.SelDir";
    private final DefaultMutableTreeNode rootNode;
    private final DatabaseFavoriteDirectories db;
    private final JTree tree;
    private final Object monitor = new Object();

    public TreeModelFavorites(JTree tree) {
        super(new DefaultMutableTreeNode(
                Bundle.getString("TreeModelFavorites.Root.DisplayName")));
        this.tree = tree;
        rootNode = (DefaultMutableTreeNode) getRoot();
        tree.addTreeWillExpandListener(this);
        db = DatabaseFavoriteDirectories.INSTANCE;
        addDirectories();
        GUI.INSTANCE.getAppFrame().addAppExitListener(this);
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
                        "TreeModelFavorites.ErrorMessage.ParamInsert"));
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
                for (Enumeration children = rootNode.children(); children.
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
                        "TreeModelFavorites.ErrorMessage.ParamDelete"));
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
                        "TreeModelFavorites.ErrorMessage.ParamUpdate"));
            }
        }
    }

    public void moveUpFavorite(FavoriteDirectory favorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveUp = getNode(favorite);
            if (nodeToMoveUp != null) {
                int indexNodeToMoveUp = rootNode.getIndex(nodeToMoveUp);
                boolean isFirstNode = indexNodeToMoveUp == 0;
                if (!isFirstNode) {
                    DefaultMutableTreeNode prevNode =
                            (DefaultMutableTreeNode) rootNode.getChildAt(
                            indexNodeToMoveUp - 1);
                    if (prevNode != null && updateFavoriteDirectory(
                            nodeToMoveUp.getUserObject(), indexNodeToMoveUp - 1) &&
                            updateFavoriteDirectory(prevNode.getUserObject(),
                            indexNodeToMoveUp)) {
                        removeNodeFromParent(prevNode);
                        insertNodeInto(prevNode, rootNode, indexNodeToMoveUp);
                    }
                }
            }
        }
    }

    public void moveDownFavorite(FavoriteDirectory favorite) {
        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveDown = getNode(favorite);
            if (nodeToMoveDown != null) {
                int indexNodeToMoveDown = rootNode.getIndex(nodeToMoveDown);
                boolean isLastNode = indexNodeToMoveDown ==
                        rootNode.getChildCount() - 1;
                if (!isLastNode) {
                    DefaultMutableTreeNode nextNode =
                            (DefaultMutableTreeNode) rootNode.getChildAt(
                            indexNodeToMoveDown + 1);
                    if (nextNode != null && updateFavoriteDirectory(
                            nodeToMoveDown.getUserObject(),
                            indexNodeToMoveDown + 1) &&
                            updateFavoriteDirectory(nextNode.getUserObject(),
                            indexNodeToMoveDown)) {
                        removeNodeFromParent(nextNode);
                        insertNodeInto(nextNode, rootNode, indexNodeToMoveDown);
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
                AppLog.logWarning(TreeModelFavorites.class,
                        Bundle.getString(
                        "TreeModelFavorites.ErrorMessage.DbDirectoryDoesNotExist",
                        directory.getDirectoryName()));
                db.deleteFavoriteDirectory(directory.getFavoriteName());
            }
        }
    }

    private void addDirectory(FavoriteDirectory directory) {
        DefaultMutableTreeNode dirNode = getNode(directory);
        if (dirNode == null) {
            DefaultMutableTreeNode node = new TreeNodeSortedChildren(directory);
            int childCount = rootNode.getChildCount();
            insertNodeInto(node, rootNode, childCount);
            addChildren(node);
            if (childCount == 1) { // Forcing repaint
                setRoot(rootNode);
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
                DefaultMutableTreeNode newChild =
                        new TreeNodeSortedChildren(subdirs[i]);
                parentNode.insert(newChild, childCount++);
                int childIndex = parentNode.getIndex(newChild);
                fireTreeNodesInserted(this, parentNode.getPath(),
                        new int[]{childIndex}, new Object[]{newChild});
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
        for (Enumeration children = rootNode.children(); children.
                hasMoreElements();) {
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
        for (Enumeration children = rootNode.children(); children.
                hasMoreElements();) {
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
                "TreeModelFavorites.ErrorMessage.Template",
                favoriteName, cause),
                Bundle.getString(
                "TreeModelFavorites.ErrorMessage.Template.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Expands the tree to a specific file.
     *
     * @param favoriteName favorite containing this file
     * @param file         file
     * @param select       if true the file node will be selected
     */
    private void expandToFile(String favoriteName, File file, boolean select) {
        Stack<File> filePath = FileUtil.getPathFromRoot(file);
        filePath.pop(); // File's root is in favorite
        DefaultMutableTreeNode node = getFavorite(favoriteName);
        while (node != null && !filePath.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(
                    node, filePath.pop());
            if (node != null && node.getChildCount() <= 0) {
                addChildren(node);
            }
        }
        if (node != null) {
            tree.expandPath(new TreePath(((DefaultMutableTreeNode) node.
                    getParent()).getPath()));
            if (select) {
                tree.setSelectionPath(new TreePath(node.getPath()));
            }
        }
    }

    private DefaultMutableTreeNode getFavorite(String name) {
        for (Enumeration children = rootNode.children();
                children.hasMoreElements();) {
            DefaultMutableTreeNode childNode =
                    (DefaultMutableTreeNode) children.nextElement();
            Object userObject = childNode.getUserObject();
            if (userObject instanceof FavoriteDirectory) {
                FavoriteDirectory fav = (FavoriteDirectory) userObject;
                if (name.equals(fav.getFavoriteName())) return childNode;
            }
        }
        return null;
    }

    public void readFromProperties() {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String favname = properties.getProperty(KEY_SELECTED_FAV_NAME);
        String dirname = properties.getProperty(KEY_SELECTED_DIR);
        if (favname != null && dirname != null && !favname.trim().isEmpty() &&
                !dirname.trim().isEmpty()) {
            expandToFile(favname.trim(), new File(dirname.trim()), true);
        } else if (favname != null) {
            DefaultMutableTreeNode fav = getFavorite(favname.trim());
            if (fav != null) {
                tree.setSelectionPath(new TreePath(fav.getPath()));
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
                if (userObject instanceof FavoriteDirectory) {
                    favname = ((FavoriteDirectory) userObject).getFavoriteName();
                } else if (userObject instanceof File) {
                    File file = ((File) userObject);
                    dirname = file.getAbsolutePath();
                    FavoriteDirectory favDir = getParentFavDir(node);
                    if (favDir != null) {
                        favname = favDir.getFavoriteName();
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
        }
    }

    private FavoriteDirectory getParentFavDir(DefaultMutableTreeNode childNode) {
        TreeNode parentNode = childNode.getParent();
        while (parentNode instanceof DefaultMutableTreeNode &&
                !parentNode.equals(rootNode)) {
            Object userObject =
                    ((DefaultMutableTreeNode) parentNode).getUserObject();
            if (userObject instanceof FavoriteDirectory) {
                return (FavoriteDirectory) userObject;
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
    }

    @Override
    public void appWillExit() {
        writeToProperties();
    }
}
