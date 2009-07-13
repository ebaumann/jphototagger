package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ViewUtil {

    private static final String KEY_TREE_DIRECTORIES_SELECTED_DIR =
            "ViewUtil.TreeDirectories"; // NOI18N

    /**
     * Returns the selected file in a {@link JTree} if the selected node is a
     * {@link DefaultMutableTreeNode} and it's user object is a {@link File} or
     * a {@link FavoriteDirectory}.
     * 
     * @param  tree a tree
     * @return      file or null if no node with a file user object is selected
     */
    public static File getSelectedFile(JTree tree) {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            Object o = path.getLastPathComponent();
            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();
                if (userObject instanceof File) {
                    return (File) userObject;
                } else if (userObject instanceof FavoriteDirectory) {
                    return new File(
                            ((FavoriteDirectory) userObject).getDirectoryName());

                }
            }
        }
        return null;
    }

    /**
     * Returns the selected directory in the tree with favorite directories.
     * 
     * @return directory or null if no directory is selected
     */
    public static File getSelectedDirectoryFromFavoriteDirectories() {
        JTree tree = GUI.INSTANCE.getAppPanel().getTreeFavorites();
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

    public static void writeTreeDirectoriesToProperties() {
        writeTreeToProperties(GUI.INSTANCE.getAppPanel().getTreeDirectories(),
                KEY_TREE_DIRECTORIES_SELECTED_DIR);
    }

    private static void writeTreeToProperties(JTree tree, String key) {
        if (tree.getSelectionCount() > 0) {
            UserSettings.INSTANCE.getSettings().setString(
                    tree.getSelectionPath().getLastPathComponent().toString(),
                    key);
        } else {
            UserSettings.INSTANCE.getProperties().remove(key);
        }
        UserSettings.INSTANCE.writeToFile();
    }

    public static void readTreeDirectoriesFromProperties() {
        JTree treeDirectories = GUI.INSTANCE.getAppPanel().getTreeDirectories();
        String filename = UserSettings.INSTANCE.getSettings().getString(
                KEY_TREE_DIRECTORIES_SELECTED_DIR);

        if (!filename.isEmpty() && FileUtil.existsDirectory(filename)) {
            TreeModel model = treeDirectories.getModel();
            if (model instanceof TreeModelAllSystemDirectories) {
                ((TreeModelAllSystemDirectories) model).expandToFile(
                        new File(filename), true);
            }
        }
    }

    private ViewUtil() {
    }
}
