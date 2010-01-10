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
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * Popupmenü für den Tree mit Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-08
 */
public final class PopupMenuImageCollections extends JPopupMenu {

    private static final long                      serialVersionUID = -3446852358941591602L;
    private final        JMenuItem                 itemDelete       = new JMenuItem(Bundle.getString("PopupMenuImageCollections.DisplayName.Action.Delete"), AppLookAndFeel.getIcon("icon_remove.png"));
    private final        JMenuItem                 itemRename       = new JMenuItem(Bundle.getString("PopupMenuImageCollections.DisplayName.Action.Rename"), AppLookAndFeel.getIcon("icon_rename.png"));
    private final        JMenuItem                 itemCreate       = new JMenuItem(Bundle.getString("PopupMenuImageCollections.DisplayName.Action.Create"), AppLookAndFeel.getIcon("icon_add.png"));
    public               int                       itemIndex;
    public static final  PopupMenuImageCollections INSTANCE         = new PopupMenuImageCollections();

    private PopupMenuImageCollections() {
        init();
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemCreate() {
        return itemCreate;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
    }
}
