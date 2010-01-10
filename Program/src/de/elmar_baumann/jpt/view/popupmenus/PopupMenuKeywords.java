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
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * Popup menu for a (flat) keywords list.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-07
 */
public final class PopupMenuKeywords extends JPopupMenu {

    private static final long              serialVersionUID = -552638878495121120L;
    private final        JMenuItem         itemRename       = new JMenuItem(Bundle.getString("PopupMenuKeywords.DisplayName.Action.Rename"), AppLookAndFeel.getIcon("icon_rename.png"));
    private final        JMenuItem         itemDelete       = new JMenuItem(Bundle.getString("PopupMenuKeywords.DisplayName.Action.Delete"), AppLookAndFeel.getIcon("icon_delete.png"));
    private              int               selIndex;
    private              JList             list;
    public static final  PopupMenuKeywords INSTANCE         = new PopupMenuKeywords();

    private PopupMenuKeywords() {
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

    private void addItems() {
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    }
}
