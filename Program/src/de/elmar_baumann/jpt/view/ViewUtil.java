/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public class ViewUtil {

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
                Favorite favoriteDirectory = (Favorite) userObject;
                return new File(favoriteDirectory.getDirectoryName());
            } else if (userObject instanceof File) {
                return (File) userObject;
            }
        }
        return null;
    }

    public static void setDisplayedMnemonicsToLabels(Container container, Character... exclude) {
        List<JLabel>    labels     = ComponentUtil.getAllOf(container, JLabel.class);
        List<Character> mnemonics  = new ArrayList<Character>(labels.size());
        final char      invalidMn  = '\0';

        Collections.addAll(mnemonics, exclude);

        for (JLabel label : labels) {
            char mnemonic = MnemonicUtil.getNotExistingMnemonicChar(label.getText(), mnemonics);

            if (mnemonic != invalidMn) {
                label.setDisplayedMnemonic(mnemonic);
                mnemonics.add(mnemonic);
            }
        }
    }

    public static File chooseFile(String keyCurrentDir, FileFilter filter, Component parent) {
        String       prevCurrentDir = UserSettings.INSTANCE.getSettings().getString(keyCurrentDir);
        File         currentDir     = new File(prevCurrentDir.isEmpty() ? UserSettings.INSTANCE.getSettingsDirectoryName() : prevCurrentDir);
        JFileChooser fc             = new JFileChooser(currentDir);

        if (filter != null) fc.setFileFilter(filter);
        if (parent == null) parent = GUI.INSTANCE.getAppFrame();

        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();
            UserSettings.INSTANCE.getSettings().set(selFile.getAbsolutePath(), keyCurrentDir);
            return selFile;
        }
        return null;
    }

    private ViewUtil() {
    }
}
