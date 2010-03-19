/*
 * @(#)MouseListenerMiscMetadataTree.java    Created on 2010-03-15
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

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.XmpColumns;
import de.elmar_baumann.jpt.helper.MiscMetadataHelper;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMiscMetadata;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerMiscMetadataTree extends MouseListenerTree {
    private static final List<Column>   XMP_COLUMNS = XmpColumns.get();
    private final PopupMenuMiscMetadata popup       =
        PopupMenuMiscMetadata.INSTANCE;

    public MouseListenerMiscMetadataTree() {
        listenExpandAllSubItems(popup.getItemExpandAllSubitems(), true);
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {
        popup.setSelPath(path);
        setItemsEnabled(path);
        popup.show(tree, x, y);
    }

    private void setItemsEnabled(TreePath path) {
        DefaultMutableTreeNode selNode =
            (DefaultMutableTreeNode) path.getLastPathComponent();
        boolean enabled =
            MiscMetadataHelper.isParentUserObjectAColumnOf(selNode,
                XMP_COLUMNS);

        popup.getItemDelete().setEnabled(enabled);
        popup.getItemRename().setEnabled(enabled);
    }
}
