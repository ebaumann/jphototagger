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

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;

/**
 * Popup menu for {@link MetadataTemplate}s.
 *
 * @author  Elmar Baumann
 * @version 2010-01-08
 */
public final class PopupMenuMetadataTemplates extends JPopupMenu {
    private static final long serialVersionUID   = 5476440706471574353L;
    private final JMenuItem   itemSetToSelImages =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuMetadataTemplates.DisplayName.Action.SetToSelImages"), AppLookAndFeel
                        .getIcon("icon_image.png"));
    private final JMenuItem itemEdit =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuMetadataTemplates.DisplayName.Action.Edit"), AppLookAndFeel
                        .getIcon("icon_edit.png"));
    private final JMenuItem itemAdd =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuMetadataTemplates.DisplayName.Action.Add"));
    private final JMenuItem itemRename =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuMetadataTemplates.DisplayName.Action.Rename"));
    private final JMenuItem itemDelete =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuMetadataTemplates.DisplayName.Action.Delete"), AppLookAndFeel
                        .getIcon("icon_delete.png"));
    private int                                    selIndex;
    private JList                                  list;
    public static final PopupMenuMetadataTemplates INSTANCE =
        new PopupMenuMetadataTemplates();

    private PopupMenuMetadataTemplates() {
        addItems();
        setAccelerators();
    }

    public int getSelIndex() {
        return selIndex;
    }

    public void setSelIndex(int selIndex) {
        this.selIndex = selIndex;
    }

    public JList getList() {
        return list;
    }

    public void setList(JList list) {
        this.list = list;
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemAdd() {
        return itemAdd;
    }

    public JMenuItem getItemEdit() {
        return itemEdit;
    }

    public JMenuItem getItemSetToSelImages() {
        return itemSetToSelImages;
    }

    private void addItems() {
        add(itemSetToSelImages);
        add(new Separator());
        add(itemAdd);
        add(new Separator());
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemSetToSelImages.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        itemAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                InputEvent.CTRL_MASK));
        itemEdit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                InputEvent.CTRL_MASK));
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                0));
    }
}
