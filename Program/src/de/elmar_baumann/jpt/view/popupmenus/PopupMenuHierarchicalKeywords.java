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
import de.elmar_baumann.jpt.view.panels.HierarchicalKeywordsPanel;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * Popup menu for the tree in a {@link HierarchicalKeywordsPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-29
 */
public final class PopupMenuHierarchicalKeywords extends JPopupMenu {

    private final        JMenuItem                     menuItemAdd                 = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionAddKeyword")         , AppLookAndFeel.getIcon("icon_add.png"));
    private final        JMenuItem                     menuItemAddToEditPanel      = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionAddToEditPanel")     , AppLookAndFeel.getIcon("icon_edit.png"));
    private final        JMenuItem                     menuItemRemove              = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionRemoveKeyword")      , AppLookAndFeel.getIcon("icon_remove.png"));
    private final        JMenuItem                     menuItemRename              = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionRenameKeyword")      , AppLookAndFeel.getIcon("icon_rename.png"));
    private final        JMenuItem                     menuItemToggleReal          = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionToggleReal")         , AppLookAndFeel.getIcon("icon_keyword_real_helper.png"));
    private final        JMenuItem                     menuItemRemoveFromEditPanel = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionRemoveFromEditPanel"), AppLookAndFeel.getIcon("icon_delete.png"));
    private final        JMenuItem                     menuItemCopy                = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionCopy")               , AppLookAndFeel.getIcon("icon_copy_to_clipboard.png"));
    private final        JMenuItem                     menuItemCut                 = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionCut")                , AppLookAndFeel.getIcon("icon_cut_to_clipboard.png"));
    private final        JMenuItem                     menuItemPaste               = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionPaste")              , AppLookAndFeel.getIcon("icon_paste_from_clipboard.png"));
    private final        JMenuItem                     menuItemDisplayImages       = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionDisplayImages")      , AppLookAndFeel.getIcon("icon_thumbnails.png"));
    private final        JMenuItem                     menuItemDisplayImagesKw     = new JMenuItem(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.ActionDisplayImagesKw")    , AppLookAndFeel.getIcon("icon_thumbnails.png"));
    private final        JMenuItem                     menuItemExpandAllSubitems   = new JMenuItem(Bundle.getString("MouseListenerTreeExpand.ItemExpand"));
    private final        JMenuItem                     menuItemCollapseAllSubitems = new JMenuItem(Bundle.getString("MouseListenerTreeExpand.ItemCollapse"));
    private              JTree                         tree;
    private              TreePath                      path;
    public static final  PopupMenuHierarchicalKeywords INSTANCE                    = new PopupMenuHierarchicalKeywords();

    public JMenuItem getMenuItemAdd() {
        return menuItemAdd;
    }

    public JMenuItem getMenuItemRemove() {
        return menuItemRemove;
    }

    public JMenuItem getMenuItemRename() {
        return menuItemRename;
    }

    public JMenuItem getMenuItemToggleReal() {
        return menuItemToggleReal;
    }

    public JMenuItem getMenuItemAddToEditPanel() {
        return menuItemAddToEditPanel;
    }

    public JMenuItem getMenuItemRemoveFromEditPanel() {
        return menuItemRemoveFromEditPanel;
    }

    public JMenuItem getMenuItemCut() {
        return menuItemCut;
    }

    public JMenuItem getMenuItemCopy() {
        return menuItemCopy;
    }

    public JMenuItem getMenuItemPaste() {
        return menuItemPaste;
    }

    public JMenuItem getMenuItemDisplayImages() {
        return menuItemDisplayImages;
    }

    public JMenuItem getMenuItemDisplayImagesKw() {
        return menuItemDisplayImagesKw;
    }

    public JMenuItem getMenuItemCollapseAllSubitems() {
        return menuItemCollapseAllSubitems;
    }

    public JMenuItem getMenuItemExpandAllSubitems() {
        return menuItemExpandAllSubitems;
    }

    private PopupMenuHierarchicalKeywords() {
        init();
    }

    public void setTreePath(TreePath path) {
        this.path = path;
    }

    public TreePath getTreePath() {
        return path;
    }

    public TreePath getPath() {
        return path;
    }

    public void setPath(TreePath path) {
        this.path = path;
    }

    public JTree getTree() {
        return tree;
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(menuItemAddToEditPanel);
        add(menuItemRemoveFromEditPanel);

        JMenu menuEdit = new JMenu(Bundle.getString("PopupMenuHierarchicalKeywords.DisplayName.MenuEdit"));
        menuEdit.add(menuItemAdd);
        menuEdit.add(menuItemRemove);
        menuEdit.add(menuItemRename);
        menuEdit.add(menuItemToggleReal);
        menuEdit.addSeparator();
        menuEdit.add(menuItemCopy);
        menuEdit.add(menuItemCut);
        menuEdit.add(menuItemPaste);

        addSeparator();
        add(menuEdit);

        addSeparator();
        add(menuItemExpandAllSubitems);
        add(menuItemCollapseAllSubitems);

        addSeparator();
        add(menuItemDisplayImages);
        add(menuItemDisplayImagesKw);
    }

    private void setAccelerators() {
        menuItemAdd                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        menuItemRemove             .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItemRename             .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        menuItemToggleReal         .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        menuItemAddToEditPanel     .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
        menuItemRemoveFromEditPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
        menuItemCopy               .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuItemCut                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuItemPaste              .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
    }
}
