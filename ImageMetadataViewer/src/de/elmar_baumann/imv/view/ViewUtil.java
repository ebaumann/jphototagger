package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.io.File;
import javax.swing.JList;
import javax.swing.JTree;
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
     * Returns the selected directory in the list with favorite directories.
     * 
     * @return directory or null if no directory is selected
     */
    public static File getSelectedDirectoryFromFavoriteDirectories() {
        JList list = Panels.getInstance().getAppPanel().getListFavoriteDirectories();
        Object o = list.getSelectedValue();
        if (o instanceof FavoriteDirectory) {
            FavoriteDirectory favoriteDirectory = (FavoriteDirectory) o;
            return new File(favoriteDirectory.getDirectoryName());
        }
        return null;
    }

    /**
     * Sets the directory tree's model to a directory chooser (speed).
     * 
     * @param chooser  directory chooser
     */
    public static void setDirectoryTreeModel(DirectoryChooser chooser) {
        chooser.setModel(Panels.getInstance().getAppPanel().getTreeDirectories().getModel());
    }

    public static void writePersistentTreeDirectories() {
        JTree treeDirectories = Panels.getInstance().getAppPanel().getTreeDirectories();
        PersistentSettings settings = PersistentSettings.getInstance();
        if (treeDirectories.getSelectionCount() > 0) {
            settings.setString(
                treeDirectories.getSelectionPath().getLastPathComponent().toString(), keyTreeDirectories);
        } else {
            settings.getProperties().remove(keyTreeDirectories);
        }
    }

    public static void readPersistentTreeDirectories() {
        JTree treeDirectories = Panels.getInstance().getAppPanel().getTreeDirectories();
        String filename = PersistentSettings.getInstance().getString(keyTreeDirectories);

        if (!filename.isEmpty() && FileUtil.existsDirectory(filename)) {
            TreePath path = TreeUtil.getTreePath(
                new File(filename), treeDirectories.getModel());
            TreeUtil.expandPathCascade(treeDirectories, path);
            treeDirectories.setSelectionPath(path);
        }
    }

    private ViewUtil() {}
}
