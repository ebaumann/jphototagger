/*
 * @(#)MouseListenerDirectories.java    Created on 2008-09-24
 *
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

package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;

import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Reagiert auf Mausaktionen in der Treeview, die die Verzeichnisse darstellt.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerDirectories extends MouseListenerTree {
    private final PopupMenuDirectories popupMenu =
        PopupMenuDirectories.INSTANCE;

    public MouseListenerDirectories() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        TreePath path = TreeUtil.getTreePath(e);

        if (path == null) {
            return;
        }

        if (MouseEventUtil.isPopupTrigger(e)) {
            if (!TreeUtil.isRootItemPosition(e)) {
                Object lastPathComponent = path.getLastPathComponent();

                if (lastPathComponent instanceof DefaultMutableTreeNode) {
                    Object usrOb =
                        ((DefaultMutableTreeNode) lastPathComponent)
                            .getUserObject();

                    if (usrOb instanceof File) {
                        File dir = (File) usrOb;

                        popupMenu.setDirectory(dir);
                        popupMenu.setTreePath(path);
                    }
                }
            }

            popupMenu.show((JTree) e.getSource(), e.getX(), e.getY());
        }
    }
}
