package org.jphototagger.program.model;

import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.event.AppWillExitEvent;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.domain.repository.FavoritesRepository;
import org.jphototagger.domain.repository.event.favorites.FavoriteDeletedEvent;
import org.jphototagger.domain.repository.event.favorites.FavoriteInsertedEvent;
import org.jphototagger.domain.repository.event.favorites.FavoriteUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.model.SortedChildrenTreeNode;
import org.jphototagger.lib.util.Bundle;

/**
 * Elements are {@code DefaultMutableTreeNode}s with the user objects listed
 * below.
 *
 * <ul>
 * <li>The root user object is a {@code String}</li>
 * <li>All user objects in the first level below the root are
 *     {@code Favorite}s retrieved through {@code DatabaseFavorites#findAllFavorites()}
 * </li>
 * <li>User objects below the favorites are directory {@code File}s</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class FavoritesTreeModel extends DefaultTreeModel implements TreeWillExpandListener {

    private static final String KEY_SELECTED_DIR = "FavoritesTreeModel.SelDir";
    private static final String KEY_SELECTED_FAV_NAME = "FavoritesTreeModel.SelFavDir";
    private static final long serialVersionUID = -2453748094818942669L;
    private final Object monitor = new Object();
    private transient boolean listenToDb = true;
    private final DefaultMutableTreeNode rootNode;
    private final JTree tree;
    private static final Logger LOGGER = Logger.getLogger(FavoritesTreeModel.class.getName());
    private final FavoritesRepository repo = Lookup.getDefault().lookup(FavoritesRepository.class);

    public FavoritesTreeModel(JTree tree) {
        super(new DefaultMutableTreeNode(Bundle.getString(FavoritesTreeModel.class, "FavoritesTreeModel.Root.DisplayName")));

        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        this.tree = tree;
        rootNode = (DefaultMutableTreeNode) getRoot();
        tree.addTreeWillExpandListener(this);
        addFavorites();
        AnnotationProcessor.process(this);
    }

    public void insert(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        synchronized (monitor) {
            listenToDb = false;
            favorite.setIndex(getNextNewFavoriteIndex());

            if (!existsFavoriteDirectory(favorite)) {
                if (repo.saveOrUpdateFavorite(favorite)) {
                    addFavorite(favorite);
                } else {
                    errorMessage(favorite.getName(), Bundle.getString(FavoritesTreeModel.class, "FavoritesTreeModel.Error.ParamInsert"));
                }
            }

            listenToDb = true;
        }
    }

    public void delete(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        synchronized (monitor) {
            listenToDb = false;

            DefaultMutableTreeNode favNode = getNode(favorite);

            if ((favNode != null) && repo.deleteFavorite(favorite.getName())) {
                removeNodeFromParent(favNode);
                resetFavoriteIndices();
            } else {
                errorMessage(favorite.getName(), Bundle.getString(FavoritesTreeModel.class, "FavoritesTreeModel.Error.ParamDelete"));
            }

            listenToDb = true;
        }
    }

    private void deleteFavorite(Favorite favorite) {
        DefaultMutableTreeNode favNode = getNode(favorite);

        if (favNode != null) {
            removeNodeFromParent(favNode);
            resetFavoriteIndices();
        }
    }

    @SuppressWarnings("unchecked")
    private void resetFavoriteIndices() {
        for (Enumeration<DefaultMutableTreeNode> children = rootNode.children(); children.hasMoreElements();) {
            Object userObject = children.nextElement().getUserObject();
            int newIndex = 0;

            if (userObject instanceof Favorite) {
                Favorite fav = (Favorite) userObject;

                fav.setIndex(newIndex++);
                repo.updateFavorite(fav);
            }
        }
    }

    private void updateNodes(DefaultMutableTreeNode nodeOfFavorite, Favorite newFavorite) {
        removeAllChildren(nodeOfFavorite);

        Object userObject = nodeOfFavorite.getUserObject();

        if (userObject instanceof Favorite) {
            Favorite favorite = (Favorite) userObject;

            favorite.setDirectory(newFavorite.getDirectory());
            favorite.setName(newFavorite.getName());
        }

        addChildren(nodeOfFavorite);
        nodeChanged(nodeOfFavorite);
    }

    @SuppressWarnings("unchecked")
    private void removeAllChildren(DefaultMutableTreeNode node) {
        List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>(node.getChildCount());

        for (Enumeration<DefaultMutableTreeNode> e = node.children(); e.hasMoreElements();) {
            children.add(e.nextElement());
        }

        for (DefaultMutableTreeNode child : children) {
            removeNodeFromParent(child);    // notifies listeners
        }
    }

    public void moveUpFavorite(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveUp = getNode(favorite);

            if (nodeToMoveUp != null) {
                int indexNodeToMoveUp = rootNode.getIndex(nodeToMoveUp);
                boolean isFirstNode = indexNodeToMoveUp == 0;

                if (!isFirstNode) {
                    DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) rootNode.getChildAt(indexNodeToMoveUp
                            - 1);

                    if ((prevNode != null)
                            && updateFavoriteDirectory(nodeToMoveUp.getUserObject(), indexNodeToMoveUp - 1)
                            && updateFavoriteDirectory(prevNode.getUserObject(), indexNodeToMoveUp)) {
                        removeNodeFromParent(prevNode);
                        insertNodeInto(prevNode, rootNode, indexNodeToMoveUp);
                    }
                }
            }
        }
    }

    public void moveDownFavorite(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        synchronized (monitor) {
            DefaultMutableTreeNode nodeToMoveDown = getNode(favorite);

            if (nodeToMoveDown != null) {
                int indexNodeToMoveDown = rootNode.getIndex(nodeToMoveDown);
                boolean isLastNode = indexNodeToMoveDown == rootNode.getChildCount() - 1;

                if (!isLastNode) {
                    DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) rootNode.getChildAt(indexNodeToMoveDown
                            + 1);

                    if ((nextNode != null)
                            && updateFavoriteDirectory(nodeToMoveDown.getUserObject(), indexNodeToMoveDown + 1)
                            && updateFavoriteDirectory(nextNode.getUserObject(), indexNodeToMoveDown)) {
                        removeNodeFromParent(nextNode);
                        insertNodeInto(nextNode, rootNode, indexNodeToMoveDown);
                    }
                }
            }
        }
    }

    private boolean updateFavoriteDirectory(Object userObject, int newIndex) {
        if (userObject instanceof Favorite) {
            Favorite favorite = (Favorite) userObject;

            favorite.setIndex(newIndex);

            return repo.updateFavorite(favorite);
        }

        return false;
    }

    private void addFavorites() {
        List<Favorite> directories = repo.findAllFavorites();

        for (Favorite directory : directories) {
            if (directory.getDirectory().isDirectory()) {
                addFavorite(directory);
            } else {
                errorMessageAddDirectory(directory);
            }
        }
    }

    private void addFavorite(Favorite directory) {
        DefaultMutableTreeNode dirNode = getNode(directory);

        if (dirNode == null) {
            DefaultMutableTreeNode node = new SortedChildrenTreeNode(directory);

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
        File dir = (userObject instanceof File)
                ? (File) userObject
                : (userObject instanceof Favorite)
                ? ((Favorite) userObject).getDirectory()
                : null;

        if ((dir == null) || !dir.isDirectory()) {
            return;
        }

        LOGGER.log(Level.FINEST, "Lese Unterverzeichnisse von ''{0}'' ein'...", dir);

        File[] subdirs = dir.listFiles(new DirectoryFilter(getDirFilterOptionShowHiddenFiles()));

        LOGGER.log(Level.FINEST, "Unterverzeichnisse von ''{0}'' wurden eingelesen", dir);

        if (subdirs == null) {
            return;
        }

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
            File subdir = subdirs[i];

            if (!nodeChildrenDirs.contains(subdir)) {
                DefaultMutableTreeNode newChild = new SortedChildrenTreeNode(subdirs[i]);

                parentNode.insert(newChild, childCount++);

                int childIndex = parentNode.getIndex(newChild);

                fireTreeNodesInserted(this, parentNode.getPath(), new int[]{childIndex}, new Object[]{newChild});
            }
        }
    }

    private DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    /**
     * Removes from a node child nodes with files as user objects if the
     * file does not exist.
     *
     * @param  parentNode parent node
     * @return            count of removed nodes
     */
    private int removeChildrenWithNotExistingFiles(DefaultMutableTreeNode parentNode) {
        int childCount = parentNode.getChildCount();
        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            Object userObject = child.getUserObject();
            File file = null;

            if (userObject instanceof File) {
                file = (File) userObject;
            } else if (userObject instanceof Favorite) {
                file = ((Favorite) userObject).getDirectory();
            }

            if ((file != null) && !file.exists()) {
                nodesToRemove.add(child);
            }
        }

        for (DefaultMutableTreeNode childNodeToRemove : nodesToRemove) {
            Object userObject = childNodeToRemove.getUserObject();

            if (userObject instanceof Favorite) {
                repo.deleteFavorite(((Favorite) userObject).getName());
            }

            removeNodeFromParent(childNodeToRemove);
        }

        return nodesToRemove.size();
    }

    // ROOT.getChildCount() is valid now, but if later there are other user
    // objects than Favorite in nodes below the root, this will not
    // work
    @SuppressWarnings("unchecked")
    private synchronized int getNextNewFavoriteIndex() {
        int index = 0;

        for (Enumeration<DefaultMutableTreeNode> children = rootNode.children(); children.hasMoreElements();) {
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

            if (favoriteDirectory.equals(child.getUserObject())) {
                return child;
            }
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
     * @param  parentNode parent node. If null, nothing will be done.
     * @return            new created directory or null if not created
     */
    public File createNewDirectory(DefaultMutableTreeNode parentNode) {
        File dirOfParentNode = (parentNode == null)
                ? null
                : getDirectory(parentNode);

        if (dirOfParentNode != null) {
            File newDir = TreeFileSystemDirectories.createDirectoryIn(dirOfParentNode);

            if (newDir != null) {
                SortedChildrenTreeNode newDirNode = new SortedChildrenTreeNode(newDir);

                parentNode.add(newDirNode);

                int childIndex = parentNode.getIndex(newDirNode);

                fireTreeNodesInserted(this, parentNode.getPath(), new int[]{childIndex},
                        new Object[]{newDirNode});

                return newDir;
            }
        }

        return null;
    }

    private File getDirectory(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        return (userObject instanceof Favorite)
                ? ((Favorite) userObject).getDirectory()
                : (userObject instanceof File)
                ? (File) userObject
                : null;
    }

    private Stack<File> getFilePathToNode(DefaultMutableTreeNode node, File file) {
        if (node == null) {
            return null;
        }

        Object userObject = node.getUserObject();
        File nodeFile = (userObject instanceof File)
                ? (File) userObject
                : (userObject instanceof Favorite)
                ? ((Favorite) userObject).getDirectory()
                : null;

        if (nodeFile != null) {
            Stack<File> filePath = FileUtil.getPathFromRoot(file);
            File filePathTop = filePath.peek();

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

        if (filePathToFavorite == null) {
            return;
        }

        while ((node != null) && !filePathToFavorite.isEmpty()) {
            node = TreeUtil.findChildNodeWithFile(node, filePathToFavorite.pop());

            if ((node != null) && (node.getChildCount() <= 0)) {
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

                if (name.equals(fav.getName())) {
                    return childNode;
                }
            }
        }

        return null;
    }

    public void readFromProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);
        String favname = storage.getString(KEY_SELECTED_FAV_NAME);
        String dirname = storage.getString(KEY_SELECTED_DIR);

        if ((favname != null) && (dirname != null) && !favname.trim().isEmpty() && !dirname.trim().isEmpty()) {
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

                Preferences storage = Lookup.getDefault().lookup(Preferences.class);

                if (dirname == null) {
                    storage.removeKey(KEY_SELECTED_DIR);
                } else {
                    storage.setString(KEY_SELECTED_DIR, dirname);
                }

                if (favname == null) {
                    storage.removeKey(KEY_SELECTED_FAV_NAME);
                } else {
                    storage.setString(KEY_SELECTED_FAV_NAME, favname);
                }
            }
        } else {
            Preferences storage = Lookup.getDefault().lookup(Preferences.class);

            storage.removeKey(KEY_SELECTED_DIR);
            storage.removeKey(KEY_SELECTED_FAV_NAME);
        }
    }

    private Favorite getParentFavDir(DefaultMutableTreeNode childNode) {
        TreeNode parentNode = childNode.getParent();

        while ((parentNode instanceof DefaultMutableTreeNode) && !parentNode.equals(rootNode)) {
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

    private void updateFavorite(Favorite oldFavorite, Favorite updatedFavorite) {
        DefaultMutableTreeNode nodeOfFavorite = getNode(oldFavorite);

        if (nodeOfFavorite != null) {
            updateNodes(nodeOfFavorite, updatedFavorite);
        }
    }

    @EventSubscriber(eventClass = FavoriteInsertedEvent.class)
    public void favoriteInserted(final FavoriteInsertedEvent evt) {
        if (listenToDb) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    addFavorite(evt.getFavorite());
                }
            });
        }
    }

    @EventSubscriber(eventClass = FavoriteDeletedEvent.class)
    public void favoriteDeleted(final FavoriteDeletedEvent evt) {
        if (listenToDb) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    deleteFavorite(evt.getFavorite());
                }
            });
        }
    }

    @EventSubscriber(eventClass = FavoriteUpdatedEvent.class)
    public void favoriteUpdated(final FavoriteUpdatedEvent evt) {
        if (listenToDb) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    updateFavorite(evt.getOldFavorite(), evt.getUpdatedFavorite());
                }
            });
        }
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
        if (event == null) {
            throw new NullPointerException("event == null");
        }

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
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // ignore
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        writeToProperties();
    }

    private void errorMessage(String favoriteName, String cause) {
        String message = Bundle.getString(FavoritesTreeModel.class, "FavoritesTreeModel.Error.Template", favoriteName, cause);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageAddDirectory(Favorite favorite) {
        LOGGER.log(Level.WARNING, "The favorite ''{0}'' couldn't be read!", favorite.getDirectory());
    }
}
