package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ViewUtil {

    private static final String keyTreeDirectories = "ViewUtil.TreeDirectories";

    /**
     * Returns the selected directory in the directories tree.
     * 
     * @param  treeDirectories  directories tree
     * @return directory or null if no directory is selected
     */
    public static File getSelectedDirectory(JTree treeDirectories) {
        File directory = null;
        TreePath path = treeDirectories.getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof File) {
                return (File) o;
            }
        }
        return directory;
    }

    /**
     * Returns the selected directory in the tree with favorite directories.
     * 
     * @return directory or null if no directory is selected
     */
    public static File getSelectedDirectoryFromFavoriteDirectories() {
        JTree tree = GUI.INSTANCE.getAppPanel().getTreeFavoriteDirectories();
        Object o = tree.getLastSelectedPathComponent();
        if (o instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
            Object userObject = node.getUserObject();
            if (userObject instanceof FavoriteDirectory) {
                FavoriteDirectory favoriteDirectory =
                        (FavoriteDirectory) userObject;
                return new File(favoriteDirectory.getDirectoryName());
            } else if (userObject instanceof File) {
                return (File) userObject;
            }
        }
        return null;
    }

    /**
     * Sets the directory tree's model to a directory chooser (speed).
     * 
     * @param chooser  directory chooser
     */
    public static void setDirectoryTreeModel(DirectoryChooser chooser) {
        chooser.setModel(GUI.INSTANCE.getAppPanel().getTreeDirectories().
                getModel());
    }

    public static void writeTreeDirectoriesToProperties() {
        JTree treeDirectories = GUI.INSTANCE.getAppPanel().getTreeDirectories();
        if (treeDirectories.getSelectionCount() > 0) {
            UserSettings.INSTANCE.getSettings().setString(
                    treeDirectories.getSelectionPath().getLastPathComponent().
                    toString(), keyTreeDirectories);
        } else {
            UserSettings.INSTANCE.getProperties().remove(keyTreeDirectories);
        }
    }

    public static void readTreeDirectoriesFromProperties() {
        JTree treeDirectories = GUI.INSTANCE.getAppPanel().getTreeDirectories();
        String filename = UserSettings.INSTANCE.getSettings().getString(
                keyTreeDirectories);

        if (!filename.isEmpty() && FileUtil.existsDirectory(filename)) {
            TreePath path = TreeUtil.getTreePath(
                    new File(filename), treeDirectories.getModel());
            TreeUtil.expandPathCascade(treeDirectories, path);
            treeDirectories.setSelectionPath(path);
        }
    }

    private ViewUtil() {
    }
}
