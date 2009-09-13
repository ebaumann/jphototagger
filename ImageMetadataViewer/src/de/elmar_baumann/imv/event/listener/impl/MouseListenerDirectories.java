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
package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Reagiert auf Mausaktionen in der Treeview, die die Verzeichnisse darstellt.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-24
 */
public final class MouseListenerDirectories extends MouseAdapter {

    private final PopupMenuDirectories popupMenu = PopupMenuDirectories.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        TreePath path = TreeUtil.getTreePath(e);
        if (path == null) return;
        if (MouseEventUtil.isPopupTrigger(e)) {
            if (!TreeUtil.isRootItemPosition(e)) {
                Object lastPathComponent = path.getLastPathComponent();
                if (lastPathComponent instanceof DefaultMutableTreeNode) {
                    Object usrOb = ((DefaultMutableTreeNode) lastPathComponent).
                            getUserObject();
                    if (usrOb instanceof File) {
                        File dir = (File) usrOb;
                        popupMenu.setDirectoryName(dir.getAbsolutePath());
                        popupMenu.setTreePath(path);
                    }
                }
            }
            popupMenu.show((JTree) e.getSource(), e.getX(), e.getY());
        }
    }
}
