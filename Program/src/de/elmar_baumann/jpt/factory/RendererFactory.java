/*
 * @(#)RendererFactory.java    2008-09-29
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

package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuImageCollections;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMiscMetadata;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuSavedSearches;
import de.elmar_baumann.jpt.view.renderer.TableCellRendererExif;
import de.elmar_baumann.jpt.view.renderer.TableCellRendererIptc;
import de.elmar_baumann.jpt.view.renderer.TableCellRendererXmp;
import de.elmar_baumann.lib.componentutil.ListItemTempSelectionRowSetter;
import de.elmar_baumann.lib.componentutil.MessageLabel;
import de.elmar_baumann.lib.componentutil.TreeItemTempSelectionRowSetter;

import java.util.List;

import javax.swing.JTable;

/**
 * Erzeugt Renderer und verknüpft sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann
 */
public final class RendererFactory {
    static final RendererFactory INSTANCE = new RendererFactory();
    private volatile boolean     init;

    synchronized void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        AppLogger.logFine(getClass(), "RendererFactory.Init.Start");
        GUI.INSTANCE.getAppPanel().setStatusbarText(
            JptBundle.INSTANCE.getString("RendererFactory.Init.Start"),
            MessageLabel.MessageType.INFO, -1);
        setMetadataTablesRenderers();
        setPopupMenuHighlighter();
        AppLogger.logFine(getClass(), "RendererFactory.Init.Finished");
        GUI.INSTANCE.getAppPanel().setStatusbarText(
            JptBundle.INSTANCE.getString("RendererFactory.Init.Finished"),
            MessageLabel.MessageType.INFO, 1000);
    }

    private void setMetadataTablesRenderers() {
        AppPanel             appPanel             = GUI.INSTANCE.getAppPanel();
        TableCellRendererXmp rendererTableCellXmp = new TableCellRendererXmp();
        List<JTable>         xmpTables            = appPanel.getXmpTables();

        for (JTable table : xmpTables) {
            table.setDefaultRenderer(Object.class, rendererTableCellXmp);
        }

        appPanel.getTableIptc().setDefaultRenderer(Object.class,
                new TableCellRendererIptc());
        appPanel.getTableExif().setDefaultRenderer(Object.class,
                new TableCellRendererExif());
    }

    private void setPopupMenuHighlighter() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        new TreeItemTempSelectionRowSetter(appPanel.getTreeFavorites(),
                                           PopupMenuFavorites.INSTANCE);
        new TreeItemTempSelectionRowSetter(appPanel.getTreeDirectories(),
                                           PopupMenuDirectories.INSTANCE);
        new TreeItemTempSelectionRowSetter(appPanel.getTreeEditKeywords(),
                                           PopupMenuKeywordsTree.INSTANCE);
        new TreeItemTempSelectionRowSetter(
            InputHelperDialog.INSTANCE.getPanelKeywords().getTree(),
            PopupMenuKeywordsTree.INSTANCE);
        new TreeItemTempSelectionRowSetter(appPanel.getTreeMiscMetadata(),
                                           PopupMenuMiscMetadata.INSTANCE);
        new ListItemTempSelectionRowSetter(appPanel.getListImageCollections(),
                                           PopupMenuImageCollections.INSTANCE);
        new ListItemTempSelectionRowSetter(appPanel.getListSavedSearches(),
                                           PopupMenuSavedSearches.INSTANCE);
    }
}
