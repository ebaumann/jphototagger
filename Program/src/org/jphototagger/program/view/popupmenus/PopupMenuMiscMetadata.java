/*
 * @(#)PopupMenuMiscMetadata.java    Created on 2010-03-15
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

package org.jphototagger.program.view.popupmenus;

import org.jphototagger.lib.event.listener.PopupMenuTree;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.EditMetadataPanels;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann
 */
public final class PopupMenuMiscMetadata extends PopupMenuTree {
    private static final long                 serialVersionUID =
        3228757281030616972L;
    public static final PopupMenuMiscMetadata INSTANCE         =
        new PopupMenuMiscMetadata(
            GUI.INSTANCE.getAppPanel().getTreeMiscMetadata());
    private static final List<Column> XMP_COLUMNS = XmpColumns.get();
    private JMenuItem                 itemAddToEditPanel;
    private JMenuItem                 itemCollapseAllSubitems;
    private JMenuItem                 itemDelete;
    private JMenuItem                 itemExpandAllSubitems;
    private JMenuItem                 itemRemoveFromEditPanel;
    private JMenuItem                 itemRename;

    private PopupMenuMiscMetadata(JTree tree) {
        super(tree);
        setAccelerators();
        setExpandAllSubItems(itemExpandAllSubitems);
        setCollapseAllSubItems(itemCollapseAllSubitems);
    }

    private void createMenuItems() {
        itemDelete = new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuMiscMetadata.DisplayName.ItemDelete"), AppLookAndFeel
                        .ICON_DELETE);
        itemExpandAllSubitems = new JMenuItem(
            JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
        itemRename = new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuMiscMetadata.DisplayName.ItemRename"), AppLookAndFeel
                        .ICON_RENAME);
        itemCollapseAllSubitems = new JMenuItem(
            JptBundle.INSTANCE.getString(
                "MouseListenerTreeExpand.ItemCollapse"));
        itemAddToEditPanel = new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuMiscMetadata.DisplayName.ActionAddToEditPanel"));
        itemRemoveFromEditPanel = new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuMiscMetadata.DisplayName.ActionRemoveFromEditPanel"));
    }

    @Override
    protected void setMenuItemsEnabled(List<TreePath> selTreePaths) {
        boolean xmpValues = allNodesXmpValues(selTreePaths);
        boolean editable  = isEditable();

        itemDelete.setEnabled(xmpValues);
        itemRename.setEnabled(xmpValues);
        itemAddToEditPanel.setEnabled(xmpValues && editable);
        itemRemoveFromEditPanel.setEnabled(xmpValues && editable);
    }

    private boolean isEditable() {
        EditMetadataPanels editPanels =
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels();

        return editPanels.isEditable();
    }

    private boolean allNodesXmpValues(List<TreePath> treePaths) {
        for (TreePath treePath : treePaths) {
            if (!isNodeXmpValue(treePath)) {
                return false;
            }
        }

        return true;
    }

    private boolean isNodeXmpValue(TreePath treePath) {
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) treePath.getLastPathComponent();

        return MiscMetadataHelper.isParentUserObjectAColumnOf(node,
                XMP_COLUMNS);
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemAddToEditPanel() {
        return itemAddToEditPanel;
    }

    public JMenuItem getItemRemoveFromEditPanel() {
        return itemRemoveFromEditPanel;
    }

    @Override
    protected void addMenuItems() {
        createMenuItems();
        add(itemRename);
        add(itemDelete);
        add(new Separator());
        add(itemAddToEditPanel);
        add(itemRemoveFromEditPanel);
        add(new Separator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        addActionsToTree();
    }

    private void addActionsToTree() {
        JTree     tree            =
            GUI.INSTANCE.getAppPanel().getTreeMiscMetadata();
        InputMap  inputMap        = tree.getInputMap();
        ActionMap actionMap       = tree.getActionMap();
        Action    actionRename    = itemRename.getAction();
        Action    actionDelete    = itemDelete.getAction();
        String    keyActionRename = "actionRename";
        String    keyActionDelete = "actionDelete";

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                     keyActionRename);
        actionMap.put(keyActionRename, actionRename);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                     keyActionDelete);
        actionMap.put(keyActionDelete, actionDelete);
    }

    private void setAccelerators() {
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                0));
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemAddToEditPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                InputEvent.CTRL_MASK));
        itemRemoveFromEditPanel.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
    }
}
