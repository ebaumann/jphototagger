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
package de.elmar_baumann.jpt.view;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.resource.GUI;
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
 * @version 2008-10-26
 */
public class ViewUtil {

    private static final String KEY_TREE_DIRECTORIES_SELECTED_DIR =
            "ViewUtil.TreeDirectories";

    /**
     * Returns the selected file in a {@link JTree} if the selected node is a
     * {@link DefaultMutableTreeNode} and it's user object is a {@link File} or
     * a {@link Favorite}.
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
                } else if (userObject instanceof Favorite) {
                    return ((Favorite) userObject).getDirectory();
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
            if (userObject instanceof Favorite) {
                Favorite favoriteDirectory =
                        (Favorite) userObject;
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
    private ViewUtil() {
    }
}
