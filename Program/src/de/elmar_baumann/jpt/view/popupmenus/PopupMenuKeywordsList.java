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

import de.elmar_baumann.jpt.resource.JptBundle;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;

/**
 * Popup menu for a keywords list, such as
 * {@link de.elmar_baumann.jpt.view.panels.KeywordsPanel#getList()}.
 *
 * @author  Elmar Baumann
 * @version 2010-01-07
 */
public final class PopupMenuKeywordsList extends JPopupMenu {
    private static final long                 serialVersionUID =
        -552638878495121120L;
    public static final PopupMenuKeywordsList INSTANCE         =
        new PopupMenuKeywordsList();
    private final JMenuItem itemInsert =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsList.DisplayName.Action.Insert"));
    private final JMenuItem itemRename =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsList.DisplayName.Action.Rename"));
    private final JMenuItem itemEditSynonyms =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsList.DisplayName.Action.EditSynonyms"));
    private final JMenuItem itemDisplayImages =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsList.DisplayName.Action.DisplayImages"));
    private final JMenuItem itemDelete =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsList.DisplayName.Action.Delete"));
    private JList list;
    private int   selIndex;

    private PopupMenuKeywordsList() {
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

    public JMenuItem getItemDisplayImages() {
        return itemDisplayImages;
    }

    public JMenuItem getItemEditSynonyms() {
        return itemEditSynonyms;
    }

    public JMenuItem getItemInsert() {
        return itemInsert;
    }

    private void addItems() {
        add(itemInsert);
        add(itemRename);
        add(itemDelete);
        add(new Separator());
        add(itemEditSynonyms);
        add(new Separator());
        add(itemDisplayImages);
    }

    private void setAccelerators() {
        itemInsert.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                InputEvent.CTRL_MASK));
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                0));
        itemEditSynonyms.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.ALT_MASK | InputEvent.CTRL_MASK));
    }
}
