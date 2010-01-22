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
package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.controller.keywords.tree.ControllerAddKeyword;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerAddKeywordsToEditPanel;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerCopyCutPasteKeyword;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerKeywordsDisplayImages;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerDeleteKeywords;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerDeleteKeywordFromEditPanel;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerRenameKeyword;
import de.elmar_baumann.jpt.controller.keywords.tree.ControllerToggleRealKeyword;
import de.elmar_baumann.jpt.controller.misc.ControllerAboutApp;
import de.elmar_baumann.jpt.controller.misc.ControllerHelp;
import de.elmar_baumann.jpt.controller.misc.ControllerMaintainDatabase;
import de.elmar_baumann.jpt.controller.search.ControllerShowAdvancedSearchDialog;
import de.elmar_baumann.jpt.controller.metadata.ControllerShowUpdateMetadataDialog;
import de.elmar_baumann.jpt.controller.misc.ControllerShowUserSettingsDialog;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;

/**
 * Erzeugt Actionlistener und verknüpft sie mit Aktionsquellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ActionListenerFactory {

    static final ActionListenerFactory INSTANCE = new ActionListenerFactory();
    private      boolean               init;

    synchronized void init() {
        Support.checkInit(ActionListenerFactory.class, init);
        if (!init) {
            init = true;
            AppFrame appFrame = GUI.INSTANCE.getAppFrame();

            ControllerAboutApp                 ctrlAbout           = new ControllerAboutApp();
            ControllerHelp                     ctrlHelp            = new ControllerHelp();
            ControllerMaintainDatabase         ctrlMaintainDb      = new ControllerMaintainDatabase();
            ControllerShowUpdateMetadataDialog ctrlShowUpdateMd    = new ControllerShowUpdateMetadataDialog();
            ControllerShowUserSettingsDialog   ctrlShowUserSettDlg = new ControllerShowUserSettingsDialog();
            ControllerShowAdvancedSearchDialog ctrlAdvSearch       = new ControllerShowAdvancedSearchDialog();

            ControllerFactory.INSTANCE.add(ctrlAbout);
            ControllerFactory.INSTANCE.add(ctrlHelp);
            ControllerFactory.INSTANCE.add(ctrlMaintainDb);
            ControllerFactory.INSTANCE.add(ctrlShowUpdateMd);
            ControllerFactory.INSTANCE.add(ctrlShowUserSettDlg);
            ControllerFactory.INSTANCE.add(ctrlAdvSearch);

            appFrame.getMenuItemAbout()           .addActionListener(ctrlAbout);
            appFrame.getMenuItemHelp()            .addActionListener(ctrlHelp);
            appFrame.getMenuItemAcceleratorKeys() .addActionListener(ctrlHelp);
            appFrame.getMenuItemMaintainDatabase().addActionListener(ctrlMaintainDb);
            appFrame.getMenuItemScanDirectory()   .addActionListener(ctrlShowUpdateMd);
            appFrame.getMenuItemSettings()        .addActionListener(ctrlShowUserSettDlg);
            appFrame.getMenuItemSearch()          .addActionListener(ctrlAdvSearch);
            listenToPopupMenuKeywordsTree();
        }
    }

    /**
     * Creates the controller and let's them listen to the
     * {@link PopupMenuKeywordsTree} because it's a singleton but more
     * more then one panel using it (2 panels: popup twice, 3 p. 3 times ...)
     */
    private void listenToPopupMenuKeywordsTree() {
        KeywordsPanel                        hkwPanel           = InputHelperDialog.INSTANCE.getPanelKeywords();
        ControllerRenameKeyword              ctrlRename         = new ControllerRenameKeyword(hkwPanel);
        ControllerDeleteKeywords             ctrlRemove         = new ControllerDeleteKeywords(hkwPanel);
        ControllerAddKeyword                 ctrlAdd            = new ControllerAddKeyword(hkwPanel);
        ControllerToggleRealKeyword          ctrlToggleReal     = new ControllerToggleRealKeyword(hkwPanel);
        ControllerAddKeywordsToEditPanel     ctrlAddToEditPanel = new ControllerAddKeywordsToEditPanel(hkwPanel);
        ControllerDeleteKeywordFromEditPanel ctrlRemoveFromEPn  = new ControllerDeleteKeywordFromEditPanel(hkwPanel);
        ControllerCopyCutPasteKeyword        ctrlCopyCutPaste   = new ControllerCopyCutPasteKeyword(hkwPanel);
        ControllerKeywordsDisplayImages      ctrlKwDisplayImg   = new ControllerKeywordsDisplayImages();

        ControllerFactory.INSTANCE.add(ctrlRename);
        ControllerFactory.INSTANCE.add(ctrlRemove);
        ControllerFactory.INSTANCE.add(ctrlAdd);
        ControllerFactory.INSTANCE.add(ctrlToggleReal);
        ControllerFactory.INSTANCE.add(ctrlAddToEditPanel);
        ControllerFactory.INSTANCE.add(ctrlRemoveFromEPn);
        ControllerFactory.INSTANCE.add(ctrlCopyCutPaste);
        ControllerFactory.INSTANCE.add(ctrlKwDisplayImg);

        hkwPanel.addKeyListener(ctrlCopyCutPaste);
        GUI.INSTANCE.getAppPanel().getTreeEditKeywords().addKeyListener(ctrlCopyCutPaste);

        PopupMenuKeywordsTree popup = PopupMenuKeywordsTree.INSTANCE;

        popup.getItemAdd()                .addActionListener(ctrlAdd);
        popup.getItemRename()             .addActionListener(ctrlRename);
        popup.getItemRemove()             .addActionListener(ctrlRemove);
        popup.getItemAddToEditPanel()     .addActionListener(ctrlAddToEditPanel);
        popup.getItemRemoveFromEditPanel().addActionListener(ctrlRemoveFromEPn);
        popup.getItemToggleReal()         .addActionListener(ctrlToggleReal);
        popup.getItemCopy()               .addActionListener(ctrlCopyCutPaste);
        popup.getItemCut()                .addActionListener(ctrlCopyCutPaste);
        popup.getItemPaste()              .addActionListener(ctrlCopyCutPaste);
    }
}
