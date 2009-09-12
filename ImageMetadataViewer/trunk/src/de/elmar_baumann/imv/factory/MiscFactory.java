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
package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLifeCycle;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.ScheduledTasks;
import de.elmar_baumann.imv.view.dialogs.InputHelperDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.componentutil.ListItemPopupHighlighter;
import de.elmar_baumann.lib.componentutil.TreeCellPopupHighlighter;
import de.elmar_baumann.lib.renderer.TreeCellRendererAllSystemDirectories;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-16
 */
public final class MiscFactory {

    static final MiscFactory INSTANCE = new MiscFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(MiscFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            PopupMenuThumbnails popupMenuPanelThumbnails =
                    PopupMenuThumbnails.INSTANCE;
            UserSettings userSettings = UserSettings.INSTANCE;

            DatabaseImageFiles.INSTANCE.addDatabaseListener(
                    appPanel.getEditPanelsArray());
            appPanel.getEditPanelsArray().setAutocomplete();

            popupMenuPanelThumbnails.addOtherPrograms();
            ListenerProvider listenerProvider = ListenerProvider.INSTANCE;
            listenerProvider.addUserSettingsChangeListener(
                    popupMenuPanelThumbnails);
            listenerProvider.addUserSettingsChangeListener(userSettings);
            AppLifeCycle.INSTANCE.addAppExitListener(
                    appPanel.getPanelThumbnails());
            ScheduledTasks.INSTANCE.run();
            setPopupMenuHighlighter();
        }
    }

    private void setPopupMenuHighlighter() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        new TreeCellPopupHighlighter(
                appPanel.getTreeFavorites(), PopupMenuFavorites.INSTANCE);
        new TreeCellPopupHighlighter(appPanel.getTreeDirectories(),
                PopupMenuDirectories.INSTANCE);
        new TreeCellPopupHighlighter(appPanel.getTreeHierarchicalKeywords(),
                PopupMenuHierarchicalKeywords.INSTANCE);
        new TreeCellPopupHighlighter(
                InputHelperDialog.INSTANCE.getPanelKeywords().getTree(),
                PopupMenuHierarchicalKeywords.INSTANCE);
        new ListItemPopupHighlighter(appPanel.getListImageCollections(),
                PopupMenuImageCollections.INSTANCE);
        new ListItemPopupHighlighter(
                appPanel.getListSavedSearches(), PopupMenuSavedSearches.INSTANCE);

        setColorsToRendererTreeDirectories();
    }

    private void setColorsToRendererTreeDirectories() {
        TreeCellRenderer r =
                GUI.INSTANCE.getAppPanel().getTreeDirectories().getCellRenderer();
        if (r instanceof TreeCellRendererAllSystemDirectories) {
            TreeCellRendererAllSystemDirectories renderer =
                    (TreeCellRendererAllSystemDirectories) r;
            renderer.setHighlightColorsForPopup(
                    AppLookAndFeel.COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE,
                    AppLookAndFeel.COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE);
        }
    }
}
