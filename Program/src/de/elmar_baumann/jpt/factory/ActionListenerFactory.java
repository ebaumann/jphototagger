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
        Util.checkInit(ActionListenerFactory.class, init);
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
        KeywordsPanel                        hkwPanel        = InputHelperDialog.INSTANCE.getPanelKeywords();
        ControllerRenameKeyword              cRename         = new ControllerRenameKeyword(hkwPanel);
        ControllerDeleteKeywords              cRemove         = new ControllerDeleteKeywords(hkwPanel);
        ControllerAddKeyword                 cAdd            = new ControllerAddKeyword(hkwPanel);
        ControllerToggleRealKeyword          cToggleReal     = new ControllerToggleRealKeyword(hkwPanel);
        ControllerAddKeywordsToEditPanel     cAddToEditPanel = new ControllerAddKeywordsToEditPanel(hkwPanel);
        ControllerDeleteKeywordFromEditPanel cRemoveFromEPn  = new ControllerDeleteKeywordFromEditPanel(hkwPanel);
        ControllerCopyCutPasteKeyword        cCopyCutPaste   = new ControllerCopyCutPasteKeyword(hkwPanel);
        ControllerKeywordsDisplayImages      cKwDisplayImg   = new ControllerKeywordsDisplayImages();

        ControllerFactory.INSTANCE.add(cRename);
        ControllerFactory.INSTANCE.add(cRemove);
        ControllerFactory.INSTANCE.add(cAdd);
        ControllerFactory.INSTANCE.add(cToggleReal);
        ControllerFactory.INSTANCE.add(cAddToEditPanel);
        ControllerFactory.INSTANCE.add(cRemoveFromEPn);
        ControllerFactory.INSTANCE.add(cCopyCutPaste);
        ControllerFactory.INSTANCE.add(cKwDisplayImg);

        hkwPanel.addKeyListener(cCopyCutPaste);
        GUI.INSTANCE.getAppPanel().getTreeEditKeywords().addKeyListener(cCopyCutPaste);

        PopupMenuKeywordsTree popup = PopupMenuKeywordsTree.INSTANCE;

        popup.getItemAdd()                .addActionListener(cAdd);
        popup.getItemRename()             .addActionListener(cRename);
        popup.getItemRemove()             .addActionListener(cRemove);
        popup.getItemAddToEditPanel()     .addActionListener(cAddToEditPanel);
        popup.getItemRemoveFromEditPanel().addActionListener(cRemoveFromEPn);
        popup.getItemToggleReal()         .addActionListener(cToggleReal);
        popup.getItemCopy()               .addActionListener(cCopyCutPaste);
        popup.getItemCut()                .addActionListener(cCopyCutPaste);
        popup.getItemPaste()              .addActionListener(cCopyCutPaste);
    }
}
