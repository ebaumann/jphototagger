package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.MessagePopupPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.io.File;
import javax.swing.JTree;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
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
                    return ((FavoriteDirectory) userObject).getDirectory();
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

        File directory = new File(filename);
        if (!filename.isEmpty() && FileUtil.existsDirectory(directory)) {
            TreeModel model = treeDirectories.getModel();
            if (model instanceof TreeModelAllSystemDirectories) {
                ((TreeModelAllSystemDirectories) model).expandToFile(
                        directory, true);
            }
        }
    }

    public static void showMessagePopup(
            final String message, final long milliseconds) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                PopupFactory factory = PopupFactory.getSharedInstance();
                MessagePopupPanel messagePanel = new MessagePopupPanel(message);
                ThumbnailsPanel tnPanel =
                        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
                int x = tnPanel.getLocationOnScreen().x +
                        tnPanel.getWidth() / 2 - messagePanel.getWidth() / 2;
                int y = 100;
                Popup popup = factory.getPopup(tnPanel, messagePanel, x, y);
                popup.show();
                Thread thread = new Thread(new HidePopup(popup, milliseconds));
                thread.setName("Hiding message popup @ " + getClass().getName()); // NOI18N
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("Showing message popup @ " + ViewUtil.class.getName()); // NOI18N
        thread.start();
    }

    private static class HidePopup implements Runnable {

        private final Popup popup;
        private final long milliseconds;

        public HidePopup(Popup popup, long milliseconds) {
            this.popup = popup;
            this.milliseconds = milliseconds;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException ex) {
                AppLog.logSevere(ViewUtil.class, ex);
            }
            popup.hide();
        }
    }

    private ViewUtil() {
    }
}
