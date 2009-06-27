package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.database.DatabaseFavoriteDirectories;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.comparator.ComparatorFilesNames;
import de.elmar_baumann.lib.io.DirectoryFilter;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
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
public final class TreeModelFavoriteDirectories extends DefaultTreeModel {

    private static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode(
            Bundle.getString("TreeModelFavoriteDirectories.Root.DisplayName"));
    private final DatabaseFavoriteDirectories db;
    private final ScanDirectories updateScanner;
    private final Object monitor = new Object();

    public TreeModelFavoriteDirectories() {
        super(ROOT);
        db = DatabaseFavoriteDirectories.INSTANCE;
        addDirectories();
        updateScanner = new ScanDirectories(ROOT);
        //updateScanner.start();
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
            if (childCount == 1) { // Forcing repaint
                setRoot(ROOT);
            }
        }
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

    private class ScanDirectories extends Thread {

        private static final long SEARCH_INTERVAL_MILLISEC = 2000;
        private final DefaultMutableTreeNode root;

        public ScanDirectories(DefaultMutableTreeNode root) {
            this.root = root;
            setName("Scanning subdirectories of favorite directories for updates @ " + // NOI18N
                    TreeModelFavoriteDirectories.class.getName());
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(SEARCH_INTERVAL_MILLISEC);
                } catch (Exception ex) {
                    AppLog.logWarning(TreeModelFavoriteDirectories.class, ex);
                }
                synchronized (monitor) {
                    try {
                        scan(root);
                        checkFavoriteDirectoryDeleted();
                    } catch (Exception ex) {
                        AppLog.logWarning(TreeModelFavoriteDirectories.class, ex);

                    }
                }
            }
        }

        private void checkFavoriteDirectoryDeleted() {
            for (Enumeration children = root.children(); children.
                    hasMoreElements();) {
                Object userObject = ((DefaultMutableTreeNode) children.
                        nextElement()).getUserObject();
                if (userObject instanceof FavoriteDirectory) {
                    FavoriteDirectory favoriteDirectory =
                            (FavoriteDirectory) userObject;
                    File dir = new File(favoriteDirectory.getDirectoryName());
                    if (!dir.exists()) {
                        deleteFavorite(favoriteDirectory);
                    }
                }
            }
        }

        private void scan(DefaultMutableTreeNode node) {
            for (Enumeration children = node.children();
                    children.hasMoreElements();) {
                DefaultMutableTreeNode child =
                        (DefaultMutableTreeNode) children.nextElement();
                addNewSubDirectories(child);
                removeNotExistingSubDirecotries(child);
                scan(child); // recursive
            }
        }

        private void removeNotExistingSubDirecotries(
                DefaultMutableTreeNode parentNode) {
            for (Enumeration children = parentNode.children(); children.
                    hasMoreElements();) {
                DefaultMutableTreeNode child =
                        (DefaultMutableTreeNode) children.nextElement();
                Object userObject = child.getUserObject();
                assert userObject instanceof File : userObject;
                if (userObject instanceof File) {
                    File file = (File) userObject;
                    if (!file.exists()) {
                        removeNodeFromParent(child);
                    }
                }
            }
        }

        private void addNewSubDirectories(DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            if (userObject instanceof FavoriteDirectory) {
                FavoriteDirectory favoriteDirectory =
                        (FavoriteDirectory) userObject;
                File dir = new File(favoriteDirectory.getDirectoryName());
                if (dir.exists()) {
                    addNewSubDirectories(node, dir);
                }
            } else if (userObject instanceof File) {
                addNewSubDirectories(node, (File) userObject);
            }
        }

        private void addNewSubDirectories(DefaultMutableTreeNode node,
                File dirOfNode) {
            assert dirOfNode.isDirectory() : dirOfNode;
            File[] subDirArray =
                    dirOfNode.listFiles(UserSettings.INSTANCE.
                    isAcceptHiddenDirectories()
                                        ? DirectoryFilter.ACCEPT_HIDDEN_FILES
                                        : DirectoryFilter.REJECT_HIDDEN_FILES);
            List<File> subDirs = Arrays.asList(subDirArray);
            Collections.sort(subDirs,
                    ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE);
            for (File subDir : subDirs) {
                if (!existsSubDirectory(node, subDir)) {
                    insertNodeInto(new DefaultMutableTreeNode(subDir), node,
                            node.getChildCount());
                }
            }
        }

        private boolean existsSubDirectory(DefaultMutableTreeNode node, File dir) {
            for (Enumeration children = node.children(); children.
                    hasMoreElements();) {
                Object userObject = ((DefaultMutableTreeNode) children.
                        nextElement()).getUserObject();
                assert userObject instanceof File;
                if (userObject instanceof File) {
                    File subdir = (File) userObject;
                    if (subdir.equals(dir)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
