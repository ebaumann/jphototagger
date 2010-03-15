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

package de.elmar_baumann.jpt.view.popupmenus;

import de.elmar_baumann.jpt.controller.miscmetadata.DeleteMiscMetadataAction;
import de.elmar_baumann.jpt.controller.miscmetadata.RenameMiscMetadataAction;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTree;
import javax.swing.KeyStroke;

/**
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public final class PopupMenuMiscMetadata extends JPopupMenu {
    private static final long                 serialVersionUID =
        3228757281030616972L;
    public static final PopupMenuMiscMetadata INSTANCE         =
        new PopupMenuMiscMetadata();
    private final JMenuItem itemRename =
        new JMenuItem(new RenameMiscMetadataAction());
    private final JMenuItem itemDelete =
        new JMenuItem(new DeleteMiscMetadataAction());
    private final JMenuItem itemExpandAllSubitems =
        new JMenuItem(
            JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem itemCollapseAllSubitems =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "MouseListenerTreeExpand.ItemCollapse"));
    private static final KeyStroke keyStrokeRename = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
    private static final KeyStroke keyStrokeDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

    private PopupMenuMiscMetadata() {
        addItems();
        setAccelerators();
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    private void addItems() {
        add(itemRename);
        add(itemDelete);
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

        inputMap.put(keyStrokeRename, keyActionRename);
        actionMap.put(keyActionRename, actionRename);

        inputMap.put(keyStrokeDelete, keyActionDelete);
        actionMap.put(keyActionDelete, actionDelete);
    }

    private void setAccelerators() {
        itemRename.setAccelerator(keyStrokeRename);
    }
}
