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

import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerAddHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerAddHierarchicalKeywordsToEditPanel;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerCopyCutPasteHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerHierarchicalKeywordsDisplayImages;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerRemoveHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerRemoveHierarchicalKeywordFromEditPanel;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerRenameHierarchicalKeyword;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.ControllerToggleRealHierarchicalKeyword;
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
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;

/**
 * Erzeugt Actionlistener und verknüpft sie mit Aktionsquellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ActionListenerFactory {

    static final ActionListenerFactory INSTANCE = new ActionListenerFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(ActionListenerFactory.class, init);
        if (!init) {
            init = true;
            AppFrame appFrame = GUI.INSTANCE.getAppFrame();

            appFrame.getMenuItemAbout().addActionListener(new ControllerAboutApp());

            ControllerHelp ctrlHelp = new ControllerHelp();

            appFrame.getMenuItemHelp()            .addActionListener(ctrlHelp);
            appFrame.getMenuItemAcceleratorKeys() .addActionListener(ctrlHelp);
            appFrame.getMenuItemMaintainDatabase().addActionListener(new ControllerMaintainDatabase());
            appFrame.getMenuItemScanDirectory()   .addActionListener(new ControllerShowUpdateMetadataDialog());
            appFrame.getMenuItemSettings()        .addActionListener(new ControllerShowUserSettingsDialog());
            appFrame.getMenuItemSearch()          .addActionListener(new ControllerShowAdvancedSearchDialog());
            listenToPopupMenuHierarchicalKeywords();
        }
    }

    /**
     * Creates the controller and let's them listen to the
     * {@link PopupMenuHierarchicalKeywords} because it's a singleton but more
     * more then one panel using it (2 panels: popup twice, 3 p. 3 times ...)
     */
    private void listenToPopupMenuHierarchicalKeywords() {
        KeywordsPanel                        hkwPanel        = InputHelperDialog.INSTANCE.getPanelKeywords();
        ControllerRenameHierarchicalKeyword              cRename         = new ControllerRenameHierarchicalKeyword(hkwPanel);
        ControllerRemoveHierarchicalKeyword              cRemove         = new ControllerRemoveHierarchicalKeyword(hkwPanel);
        ControllerAddHierarchicalKeyword                 cAdd            = new ControllerAddHierarchicalKeyword(hkwPanel);
        ControllerToggleRealHierarchicalKeyword          cToggleReal     = new ControllerToggleRealHierarchicalKeyword(hkwPanel);
        ControllerAddHierarchicalKeywordsToEditPanel     cAddToEditPanel = new ControllerAddHierarchicalKeywordsToEditPanel(hkwPanel);
        ControllerRemoveHierarchicalKeywordFromEditPanel cRemoveFromEPn  = new ControllerRemoveHierarchicalKeywordFromEditPanel(hkwPanel);
        ControllerCopyCutPasteHierarchicalKeyword        cCopyCutPaste   = new ControllerCopyCutPasteHierarchicalKeyword(hkwPanel);

        new ControllerHierarchicalKeywordsDisplayImages();

        hkwPanel.addKeyListener(cCopyCutPaste);
        GUI.INSTANCE.getAppPanel().getTreeEditKeywords().addKeyListener(cCopyCutPaste);

        PopupMenuHierarchicalKeywords popup = PopupMenuHierarchicalKeywords.INSTANCE;

        popup.getMenuItemAdd()                .addActionListener(cAdd);
        popup.getMenuItemRename()             .addActionListener(cRename);
        popup.getMenuItemRemove()             .addActionListener(cRemove);
        popup.getMenuItemAddToEditPanel()     .addActionListener(cAddToEditPanel);
        popup.getMenuItemRemoveFromEditPanel().addActionListener(cRemoveFromEPn);
        popup.getMenuItemToggleReal()         .addActionListener(cToggleReal);
        popup.getMenuItemCopy()               .addActionListener(cCopyCutPaste);
        popup.getMenuItemCut()                .addActionListener(cCopyCutPaste);
        popup.getMenuItemPaste()              .addActionListener(cCopyCutPaste);
    }
}
