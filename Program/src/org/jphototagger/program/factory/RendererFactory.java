/*
 * @(#)RendererFactory.java    Created on 2008-09-29
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
import org.jphototagger.lib.componentutil.TreeItemTempSelectionRowSetter;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;
import org.jphototagger.program.view.renderer.TableCellRendererExif;
import org.jphototagger.program.view.renderer.TableCellRendererIptc;
import org.jphototagger.program.view.renderer.TableCellRendererXmp;

import java.awt.EventQueue;

import java.util.List;

import javax.swing.JTable;

/**
 * Erzeugt Renderer und verknüpft sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann
 */
public final class RendererFactory {
    static final RendererFactory INSTANCE = new RendererFactory();
    private final Support        support  = new Support();
    private volatile boolean     init;

    synchronized void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("RendererFactory.Init.Start");
                setMetadataTablesRenderers();
                setPopupMenuHighlighter();
                Support.setStatusbarInfo("RendererFactory.Init.Finished");
            }
        });
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

        support.add(
            new TreeItemTempSelectionRowSetter(
                appPanel.getTreeFavorites(), PopupMenuFavorites.INSTANCE));
        support.add(
            new TreeItemTempSelectionRowSetter(
                appPanel.getTreeDirectories(), PopupMenuDirectories.INSTANCE));
        support.add(
            new TreeItemTempSelectionRowSetter(
                appPanel.getTreeEditKeywords(),
                PopupMenuKeywordsTree.INSTANCE));
        support.add(
            new TreeItemTempSelectionRowSetter(
                InputHelperDialog.INSTANCE.getPanelKeywords().getTree(),
                PopupMenuKeywordsTree.INSTANCE));
        support.add(
            new ListItemTempSelectionRowSetter(
                appPanel.getListImageCollections(),
                PopupMenuImageCollections.INSTANCE));
        support.add(
            new ListItemTempSelectionRowSetter(
                appPanel.getListSavedSearches(),
                PopupMenuSavedSearches.INSTANCE));
    }
}
