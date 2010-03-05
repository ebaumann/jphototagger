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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Favorite;
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

    private static final long           serialVersionUID  = 4280765256503091379L;
    private final        FileSystemView fileSystemView    = FileSystemView.getFileSystemView();
    private              int            tempSelRow  = -1;

    public TreeCellRendererFavorites() {
        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
        DefaultMutableTreeNode node       = (DefaultMutableTreeNode) value;
        Object                 userObject = node.getUserObject();
        render(userObject, row);
        return this;
    }

    private void render(Object userObject, int row) {
        File file = null;
        if (userObject instanceof Favorite) {
            Favorite favoriteDirectory = (Favorite) userObject;
            file = favoriteDirectory.getDirectory();
            setText(favoriteDirectory.getName());
        } else if (userObject instanceof File) {
            file = (File) userObject;
            setText(getDirectoryName(file));
        }
        if (file != null) {
            if (file.exists()) {
                synchronized (fileSystemView) {
                    try {
                        setIcon(fileSystemView.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLogger.logSevere(TreeCellRendererFavorites.class, ex);
                    }
                }
            }
        }
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow  = row == tempSelRow;

        setForeground(isTempSelRow || selected && !tempSelExists
                ? AppLookAndFeel.getTreeSelectionForeground()
                : AppLookAndFeel.getTreeTextForeground()
                );

        setBackground(isTempSelRow || selected && !tempSelExists
                ? AppLookAndFeel.getTreeSelectionBackground()
                : AppLookAndFeel.getTreeTextBackground()
                );
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

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
