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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
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
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);

        TreePath path = TreeUtil.getTreePath(evt);

        if (path == null) {
            return;
        }

        if (MouseEventUtil.isPopupTrigger(evt)) {
            if (!TreeUtil.isRootItemPosition(evt)) {
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

            popupMenu.show((JTree) evt.getSource(), evt.getX(), evt.getY());
        }
    }
}
