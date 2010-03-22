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

import org.jphototagger.lib.event.listener.PopupMenuTree;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann
 */
public abstract class ControllerMiscMetadata extends Controller
        implements PopupMenuTree.Listener {
    private static final List<Column>   XMP_COLUMNS = XmpColumns.get();
    private final PopupMenuMiscMetadata popup       =
        PopupMenuMiscMetadata.INSTANCE;
    private final JTree                 tree        =
        GUI.INSTANCE.getAppPanel().getTreeMiscMetadata();

    protected ControllerMiscMetadata() {
        listenToKeyEventsOf(tree);
    }

    protected abstract void action(Column column, String value);

    private void action(List<Pair<Column, String>> pairs) {
        for (Pair<Column, String> pair : pairs) {
            action(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    protected void action(ActionEvent evt) {
        assert false;    // should not be triggered
    }

    @Override
    public void action(JTree tree, List<TreePath> treePaths) {
        action(colValuesFrom(treePaths));
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return false;
    }

    @Override
    protected void action(KeyEvent evt) {
        action(colValuesFrom(Arrays.asList(tree.getSelectionPaths())));
    }

    protected List<Pair<Column, String>> colValuesFrom(List<TreePath> paths) {
        List<Pair<Column, String>> values = new ArrayList<Pair<Column,
                                                String>>(paths.size());

        if (paths == null) {
            return values;
        }

        for (TreePath path : paths) {
            Pair<Column, String> value = colValueFrom(path);

            if (value != null) {
                values.add(value);
            }
        }

        return values;
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
