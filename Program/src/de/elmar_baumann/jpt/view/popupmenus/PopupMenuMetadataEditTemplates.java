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
package de.elmar_baumann.jpt.view.popupmenus;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.MetadataEditTemplate;
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * Popup menu for {@link MetadataEditTemplate}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class PopupMenuMetadataEditTemplates extends JPopupMenu {

    private final       JMenuItem                      itemSetToSelImages = new JMenuItem(Bundle.getString("PopupMenuMetadataEditTemplates.DisplayName.Action.SetToSelImages"), AppLookAndFeel.getIcon("icon_image.png"));
    private final       JMenuItem                      itemEdit           = new JMenuItem(Bundle.getString("PopupMenuMetadataEditTemplates.DisplayName.Action.Edit"          ), AppLookAndFeel.getIcon("icon_edit.png"));
    private final       JMenuItem                      itemAdd            = new JMenuItem(Bundle.getString("PopupMenuMetadataEditTemplates.DisplayName.Action.Add"           ), AppLookAndFeel.getIcon("icon_add.png"));
    private final       JMenuItem                      itemRename         = new JMenuItem(Bundle.getString("PopupMenuMetadataEditTemplates.DisplayName.Action.Rename"        ), AppLookAndFeel.getIcon("icon_rename.png"));
    private final       JMenuItem                      itemDelete         = new JMenuItem(Bundle.getString("PopupMenuMetadataEditTemplates.DisplayName.Action.Delete"        ), AppLookAndFeel.getIcon("icon_delete.png"));
    private             int                            selIndex;
    private             JList                          list;
    public static final PopupMenuMetadataEditTemplates INSTANCE           = new PopupMenuMetadataEditTemplates();

    private PopupMenuMetadataEditTemplates() {
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
        add(new JSeparator());
        add(itemAdd);
        add(new JSeparator());
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemSetToSelImages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        itemAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        itemEdit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    }
}
