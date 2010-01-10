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
package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.controller.hierarchicalkeywords.HierarchicalKeywordTreeNodesClipboard;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to mouse events in a {@link KeywordsPanel}'s tree and
 * shows the {@link PopupMenuHierarchicalKeywords} when the popup trigger mouse
 * button is pressed.
 *
 * Also sets the selected tree path.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-29
 */
public final class MouseListenerHierarchicalKeywords extends MouseListenerTree {

    private final PopupMenuHierarchicalKeywords popupMenu = PopupMenuHierarchicalKeywords.INSTANCE;

    public MouseListenerHierarchicalKeywords() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (MouseEventUtil.isPopupTrigger(e)) {
            TreePath path = TreeUtil.getTreePath(e);
            boolean isHkNode =
                    path != null &&
                    !TreeUtil.isRootItemPosition(e) &&
                    path.getLastPathComponent() instanceof DefaultMutableTreeNode;

            popupMenu.setTree((JTree)e.getSource());
            popupMenu.setTreePath(path);
            setMenuItemsEnabled(isHkNode);
            popupMenu.getItemAdd().setEnabled(path != null);
            popupMenu.show((JTree) e.getSource(), e.getX(), e.getY());
        }
    }

    private void setMenuItemsEnabled(boolean hkNode) {
        popupMenu.getItemRemove()             .setEnabled(hkNode);
        popupMenu.getItemRename()             .setEnabled(hkNode);
        popupMenu.getItemToggleReal()         .setEnabled(hkNode);
        popupMenu.getItemAddToEditPanel()     .setEnabled(hkNode);
        popupMenu.getItemRemoveFromEditPanel().setEnabled(hkNode);
        popupMenu.getItemCut()                .setEnabled(hkNode);
        popupMenu.getItemPaste()              .setEnabled(hkNode && !HierarchicalKeywordTreeNodesClipboard.INSTANCE.isEmpty());
        popupMenu.getItemExpandAllSubitems()  .setEnabled(hkNode);
        popupMenu.getItemCollapseAllSubitems().setEnabled(hkNode);
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {
        // ignore
    }
}
