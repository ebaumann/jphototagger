/*
 * @(#)ControllerMiscMetadata.java    Created on 2010-03-15
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

package org.jphototagger.program.controller.miscmetadata;

import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;
import org.jphototagger.lib.generics.Pair;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann
 */
public abstract class ControllerMiscMetadata extends Controller {
    private static final List<Column>   XMP_COLUMNS = XmpColumns.get();
    private final PopupMenuMiscMetadata popup       =
        PopupMenuMiscMetadata.INSTANCE;
    private final JTree                 tree        =
        GUI.INSTANCE.getAppPanel().getTreeMiscMetadata();

    protected ControllerMiscMetadata() {
        listenToKeyEventsOf(tree);
    }

    protected abstract void action(Column column, String value);

    private void action(Pair<Column, String> pair) {
        if (pair != null) {
            action(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    protected void action(ActionEvent evt) {
        action(colValueFrom(popup.getSelPath()));
    }

    @Override
    protected void action(KeyEvent evt) {
        action(colValueFrom(tree.getSelectionPath()));
    }

    protected Pair<Column, String> colValueFrom(TreePath path) {
        if (path != null) {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) path.getLastPathComponent();

            if (MiscMetadataHelper.isParentUserObjectAColumnOf(node,
                    XMP_COLUMNS)) {
                String                 value      =
                    node.getUserObject().toString();
                DefaultMutableTreeNode parentNode =
                    (DefaultMutableTreeNode) node.getParent();
                Column column = (Column) parentNode.getUserObject();

                return new Pair<Column, String>(column, value);
            }
        }

        return null;
    }
}
