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

package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import de.elmar_baumann.lib.componentutil.ListUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item
 * {@link PopupMenuKeywordsTree#getItemDisplayImages()} and on
 * action displays images with the selected keyword.
 *
 * @author  Elmar Baumann
 * @version 2009-07-12
 */
public class ControllerKeywordsDisplayImages implements ActionListener {
    private final PopupMenuKeywordsTree popup  = PopupMenuKeywordsTree.INSTANCE;
    private final JMenuItem             itemHk = popup.getItemDisplayImages();
    private final JMenuItem             itemKw = popup.getItemDisplayImagesKw();

    public ControllerKeywordsDisplayImages() {
        listen();
    }

    private void listen() {
        itemHk.addActionListener(this);
        itemKw.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Keyword keyword = getKeyword();

        if (keyword == null) {
            return;
        }

        Object source = e.getSource();

        if (source == itemHk) {
            showImages(keyword);
        } else if (source == itemKw) {
            showImages(keyword.getName());
        }
    }

    private Keyword getKeyword() {
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) popup.getTreePath().getLastPathComponent();
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            return (Keyword) userObject;
        }

        return null;
    }

    private void showImages(Keyword keyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        appPanel.displaySelKeywordsTree(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);
        KeywordsHelper.selectNode(appPanel.getTreeSelKeywords(), keyword);
    }

    private void showImages(String keyword) {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        appPanel.displaySelKeywordsList(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);
        ListUtil.select(appPanel.getListSelKeywords(), keyword, 0);
    }
}
