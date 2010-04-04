/*
 * @(#)ControllerKeywordsDisplayImages.java    Created on 2009-07-12
 *
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

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import org.jphototagger.lib.componentutil.ListUtil;

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
    public void actionPerformed(ActionEvent evt) {
        Keyword keyword = getKeyword();

        if (keyword == null) {
            return;
        }

        Object source = evt.getSource();

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
