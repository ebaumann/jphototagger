/*
 * @(#)MouseListenerFactory.java    Created on 2008-09-29
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.factory;

import org.jphototagger.lib.componentutil.ListItemTempSelectionRowSetter;
import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.componentutil.TreeItemTempSelectionRowSetter;
import org.jphototagger.lib.event.listener.TableButtonMouseListener;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.impl.MouseListenerDirectories;
import org.jphototagger.program.event.listener.impl.MouseListenerFavorites;
import org.jphototagger.program.event.listener.impl
    .MouseListenerImageCollections;
import org.jphototagger.program.event.listener.impl.MouseListenerKeywordsList;
import org.jphototagger.program.event.listener.impl.MouseListenerKeywordsTree;
import org.jphototagger.program.event.listener.impl
    .MouseListenerMetadataTemplates;
import org.jphototagger.program.event.listener.impl.MouseListenerSavedSearches;
import org.jphototagger.program.event.listener.impl.MouseListenerTreeExpand;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;

/**
 * Erzeugt und verbindet MouseListener.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerFactory {
    static final MouseListenerFactory INSTANCE = new MouseListenerFactory();
    private volatile boolean          init;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        AppLogger.logFine(getClass(), "MouseListenerFactory.Init.Start");
        GUI.INSTANCE.getAppPanel().setStatusbarText(
            JptBundle.INSTANCE.getString("MouseListenerFactory.Init.Start"),
            MessageLabel.MessageType.INFO, -1);

        AppPanel                appPanel           = GUI.INSTANCE.getAppPanel();
        MouseListenerTreeExpand listenerTreeExpand =
            new MouseListenerTreeExpand();
        MouseListenerKeywordsTree listenerKeywordsTree =
            new MouseListenerKeywordsTree();

        appPanel.getTableExif().addMouseListener(
            new TableButtonMouseListener(appPanel.getTableExif()));
        appPanel.getTreeDirectories().addMouseListener(
            new MouseListenerDirectories());
        appPanel.getListSavedSearches().addMouseListener(
            new MouseListenerSavedSearches());
        appPanel.getListEditKeywords().addMouseListener(
            new MouseListenerKeywordsList());
        appPanel.getListImageCollections().addMouseListener(
            new MouseListenerImageCollections());
        appPanel.getTreeFavorites().addMouseListener(
            new MouseListenerFavorites());
        appPanel.getTreeTimeline().addMouseListener(listenerTreeExpand);
        appPanel.getTreeSelKeywords().addMouseListener(listenerTreeExpand);
        appPanel.getTreeEditKeywords().addMouseListener(listenerKeywordsTree);
        InputHelperDialog.INSTANCE.getPanelKeywords().getTree()
            .addMouseListener(listenerKeywordsTree);
        InputHelperDialog.INSTANCE.getPanelKeywords().getList()
            .addMouseListener(new MouseListenerKeywordsList());
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList()
            .addMouseListener(new MouseListenerMetadataTemplates());
        new TreeItemTempSelectionRowSetter(appPanel.getTreeMiscMetadata(),
                                           listenerTreeExpand.getPopupMenu());
        new TreeItemTempSelectionRowSetter(appPanel.getTreeTimeline(),
                                           listenerTreeExpand.getPopupMenu());
        new TreeItemTempSelectionRowSetter(appPanel.getTreeSelKeywords(),
                                           listenerTreeExpand.getPopupMenu());
        new ListItemTempSelectionRowSetter(appPanel.getListEditKeywords(),
                                           PopupMenuKeywordsList.INSTANCE);
        new ListItemTempSelectionRowSetter(
            InputHelperDialog.INSTANCE.getPanelKeywords().getList(),
            PopupMenuKeywordsList.INSTANCE);
        new ListItemTempSelectionRowSetter(
            InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList(),
            PopupMenuMetadataTemplates.INSTANCE);
        AppLogger.logFine(getClass(), "MouseListenerFactory.Init.Finished");
        GUI.INSTANCE.getAppPanel().setStatusbarText(
            JptBundle.INSTANCE.getString("MouseListenerFactory.Init.Finished"),
            MessageLabel.MessageType.INFO, 1000);
    }
}
