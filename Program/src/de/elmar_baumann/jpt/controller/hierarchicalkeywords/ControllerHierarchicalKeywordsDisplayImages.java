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
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item
 * {@link PopupMenuHierarchicalKeywords#getMenuItemDisplayImages()} and on
 * action displays images with the selected keyword.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerHierarchicalKeywordsDisplayImages
        implements ActionListener {

    private final PopupMenuHierarchicalKeywords popup =
            PopupMenuHierarchicalKeywords.INSTANCE;
    private final JMenuItem itemHk = popup.getMenuItemDisplayImages();
    private final JMenuItem itemKw = popup.getMenuItemDisplayImagesKw();

    public ControllerHierarchicalKeywordsDisplayImages() {
        listen();
    }

    private void listen() {
        itemHk.addActionListener(this);
        itemKw.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HierarchicalKeyword keyword = getKeyword();
        if (keyword == null) return;
        Object source = e.getSource();
        if (source == itemHk) {
            showImages(keyword);
        } else if (source == itemKw) {
            showImages(keyword.getName());
        }
    }

    private HierarchicalKeyword getKeyword() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) popup.getTreePath().
                getLastPathComponent();
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            return (HierarchicalKeyword) userObject;
        }
        return null;
    }

    private void showImages(HierarchicalKeyword hierarchicalKeyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        appPanel.getTabbedPaneSelection().setSelectedComponent(
                appPanel.getTabSelectionHierarchicalKeywords());
        HierarchicalKeywordsHelper.selectNode(
                appPanel.getTreeSelKeywords(),
                hierarchicalKeyword);
    }

    private void showImages(String keyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        appPanel.getTabbedPaneSelection().setSelectedComponent(
                appPanel.getTabSelectionKeywords());
        ListUtil.select(appPanel.getListSelKeywords(), keyword, 0);
    }
}
