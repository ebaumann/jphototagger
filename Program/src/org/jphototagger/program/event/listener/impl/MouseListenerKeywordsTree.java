/*
 * @(#)MouseListenerKeywordsTree.java    Created on 2009-07-29
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

import org.jphototagger.program.controller.keywords.tree.KeywordTreeNodesClipboard;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Do not use this class! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * Listens to mouse events in a {@link KeywordsPanel}'s tree and
 * shows the {@link PopupMenuKeywordsTree} when the popup trigger mouse
 * button is pressed.
 *
 * Also sets the selected tree path.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerKeywordsTree extends MouseListenerTree {
    private final PopupMenuKeywordsTree popupMenu =
        PopupMenuKeywordsTree.INSTANCE;

    public MouseListenerKeywordsTree() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            TreePath mouseCursorPath = TreeUtil.getTreePath(evt);
            boolean  isHkNode        =
                (mouseCursorPath != null) &&!TreeUtil.isRootItemPosition(evt)
                && (mouseCursorPath.getLastPathComponent()
                    instanceof DefaultMutableTreeNode);
            JTree tree = (JTree) evt.getSource();

            popupMenu.setTree(tree);
            setTreePathsToPopupMenu(tree, mouseCursorPath);
            setMenuItemsEnabled(isHkNode);
            popupMenu.getItemAdd().setEnabled(mouseCursorPath != null);
            popupMenu.show(tree, evt.getX(), evt.getY());
        }
    }

    private void setTreePathsToPopupMenu(JTree tree, TreePath mouseCursorPath) {
        popupMenu.setTreePath(mouseCursorPath);

        if (mouseCursorPath == null) {
            popupMenu.setTreePaths(null);

            return;
        }

        TreePath[] selPaths = tree.getSelectionPaths();

        popupMenu.setTreePaths((selPaths == null)
                               ? new TreePath[] { mouseCursorPath }
                               : selPaths);
    }

    private void setMenuItemsEnabled(boolean hkNode) {
        popupMenu.getItemRemove().setEnabled(hkNode);
        popupMenu.getItemRename().setEnabled(hkNode);
        popupMenu.getItemToggleReal().setEnabled(hkNode);
        popupMenu.getItemAddToEditPanel().setEnabled(hkNode);
        popupMenu.getItemRemoveFromEditPanel().setEnabled(hkNode);
        popupMenu.getItemCut().setEnabled(hkNode);
        popupMenu.getItemPaste().setEnabled(hkNode
                &&!KeywordTreeNodesClipboard.INSTANCE.isEmpty());
        popupMenu.getItemExpandAllSubitems().setEnabled(hkNode);
        popupMenu.getItemCollapseAllSubitems().setEnabled(hkNode);
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {

        // ignore
    }
}
