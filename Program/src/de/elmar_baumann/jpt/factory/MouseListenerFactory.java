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

package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerDirectories;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerFavorites;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerImageCollections;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerKeywordsList;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerKeywordsTree;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerMetadataTemplates;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerSavedSearches;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerTreeExpand;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataTemplates;
import de.elmar_baumann.lib.componentutil.ListItemTempSelectionRowSetter;
import de.elmar_baumann.lib.componentutil.MessageLabel;
import de.elmar_baumann.lib.componentutil.TreeItemTempSelectionRowSetter;
import de.elmar_baumann.lib.event.listener.TableButtonMouseListener;

/**
 * Erzeugt und verbindet MouseListener.
 *
 * @author  Elmar Baumann
 * @version 2008-09-29
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
        appPanel.getTreeMiscMetadata().addMouseListener(listenerTreeExpand);
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
