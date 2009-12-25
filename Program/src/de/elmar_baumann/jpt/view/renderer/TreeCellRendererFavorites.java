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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.FavoriteDirectory;
import de.elmar_baumann.jpt.data.Timeline;
import java.awt.Component;
import java.io.File;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for {@link de.elmar_baumann.jpt.data.Timeline} nodes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeCellRendererFavorites extends DefaultTreeCellRenderer {

    private final FileSystemView fileSystemView =
            FileSystemView.getFileSystemView();
    private int popupHighLightRow = -1;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                row, hasFocus);

        assert value instanceof DefaultMutableTreeNode :
                "Not a DefaultMutableTreeNode: " + value;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        render(userObject, row);
        return this;
    }

    private void render(Object userObject, int row) {
        File file = null;
        if (userObject instanceof FavoriteDirectory) {
            FavoriteDirectory favoriteDirectory =
                    (FavoriteDirectory) userObject;
            file = favoriteDirectory.getDirectory();
            setText(favoriteDirectory.getFavoriteName());
        } else if (userObject instanceof File) {
            file = (File) userObject;
            setText(file != null
                    ? getDirectoryName(file)
                    : "");
        }
        if (file != null) {
            if (file.exists()) {
                synchronized (fileSystemView) {
                    try {
                        setIcon(fileSystemView.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLog.logSevere(TreeCellRendererFavorites.class, ex);
                    }
                }
            }
        }
        setOpaque(row == popupHighLightRow);
        if (row == popupHighLightRow) {
            setForeground(AppLookAndFeel.COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE);
            setBackground(AppLookAndFeel.COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE);
        }
    }

    private String getDirectoryName(File file) {
        String name = file.getName();

        // Windows drive letters
        if (name.isEmpty()) {
            name = file.getAbsolutePath();
            if (name.endsWith("\\")) {
                name = name.substring(0, name.length() - 2) + ":";
            }
        }
        return name;
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
