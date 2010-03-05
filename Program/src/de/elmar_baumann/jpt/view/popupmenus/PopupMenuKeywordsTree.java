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
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * Popup menu for the tree in a {@link KeywordsPanel}.
 *
 * @author  Elmar Baumann
 * @version 2009-07-29
 */
public final class PopupMenuKeywordsTree extends JPopupMenu {
    private static final long serialVersionUID = 2140903704744267916L;
    private final JMenuItem   itemAdd          =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionAddKeyword"));
    private final JMenuItem itemAddToEditPanel =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionAddToEditPanel"));
    private final JMenuItem itemRemove =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionRemoveKeyword"));
    private final JMenuItem itemRename =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionRenameKeyword"));
    private final JMenuItem itemToggleReal =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionToggleReal"));
    private final JMenuItem itemRemoveFromEditPanel =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionRemoveFromEditPanel"));
    private final JMenuItem itemCopy =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionCopy"));
    private final JMenuItem itemCut =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionCut"));
    private final JMenuItem itemPaste =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionPaste"));
    private final JMenuItem itemDisplayImages =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionDisplayImages"));
    private final JMenuItem itemDisplayImagesKw =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "PopupMenuKeywordsTree.DisplayName.ActionDisplayImagesKw"));
    private final JMenuItem itemExpandAllSubitems =
        new JMenuItem(
            JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem itemCollapseAllSubitems =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "MouseListenerTreeExpand.ItemCollapse"));
    private JTree                             tree;
    private TreePath                          treePath;
    private TreePath[]                        treePaths;
    public static final PopupMenuKeywordsTree INSTANCE =
        new PopupMenuKeywordsTree();

    public JMenuItem getItemAdd() {
        return itemAdd;
    }

    public JMenuItem getItemRemove() {
        return itemRemove;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemToggleReal() {
        return itemToggleReal;
    }

    public JMenuItem getItemAddToEditPanel() {
        return itemAddToEditPanel;
    }

    public JMenuItem getItemRemoveFromEditPanel() {
        return itemRemoveFromEditPanel;
    }

    public JMenuItem getItemCut() {
        return itemCut;
    }

    public JMenuItem getItemCopy() {
        return itemCopy;
    }

    public JMenuItem getItemPaste() {
        return itemPaste;
    }

    public JMenuItem getItemDisplayImages() {
        return itemDisplayImages;
    }

    public JMenuItem getItemDisplayImagesKw() {
        return itemDisplayImagesKw;
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    private PopupMenuKeywordsTree() {
        init();
    }

    public void setTreePath(TreePath path) {
        this.treePath = path;
    }

    public TreePath getTreePath() {
        return treePath;
    }

    public void setTreePaths(TreePath[] treePaths) {
        this.treePaths = treePaths;
    }

    public TreePath[] getTreePaths() {
        return treePaths;
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
        add(itemAddToEditPanel);
        add(itemRemoveFromEditPanel);

        JMenu menuEdit = new JMenu(
                             JptBundle.INSTANCE.getString(
                                 "PopupMenuKeywordsTree.DisplayName.MenuEdit"));

        menuEdit.add(itemAdd);
        menuEdit.add(itemRemove);
        menuEdit.add(itemRename);
        menuEdit.add(itemToggleReal);
        menuEdit.addSeparator();
        menuEdit.add(itemCopy);
        menuEdit.add(itemCut);
        menuEdit.add(itemPaste);
        addSeparator();
        add(menuEdit);
        addSeparator();
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        addSeparator();
        add(itemDisplayImages);
        add(itemDisplayImagesKw);
    }

    private void setAccelerators() {
        itemAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                InputEvent.CTRL_MASK));
        itemRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                0));
        itemRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemToggleReal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                InputEvent.CTRL_MASK));
        itemAddToEditPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                InputEvent.CTRL_MASK));
        itemRemoveFromEditPanel.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                InputEvent.CTRL_MASK));
        itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_MASK));
        itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                InputEvent.CTRL_MASK));
    }
}
