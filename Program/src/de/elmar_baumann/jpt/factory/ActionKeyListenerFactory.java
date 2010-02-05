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
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.JMenuItem;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ActionKeyListenerFactory {

    static final ActionKeyListenerFactory INSTANCE = new ActionKeyListenerFactory();
    private      boolean               init;

    synchronized void init() {
        Support.checkInit(ActionKeyListenerFactory.class, init);
        if (!init) {
            init = true;
            addActionListeners();
            addKeyListeners();
        }
    }

    private void addActionListeners() {
        listenToAppFrameMenuItems();
        listenToPopupMenuKeywordsTrees();
    }

    private void addKeyListeners() {
        addKeyListener(ControllerCopyCutPasteKeyword.class, GUI.INSTANCE.getAppPanel().getTreeEditKeywords());
        addKeyListener(ControllerCopyCutPasteKeyword.class, InputHelperDialog.INSTANCE.getPanelKeywords().getTree());
    }

    private void listenToAppFrameMenuItems() {
        AppFrame appFrame = GUI.INSTANCE.getAppFrame();

        addActionListeners(ControllerAboutApp.class                , appFrame.getMenuItemAbout());
        addActionListeners(ControllerHelp.class                    , appFrame.getMenuItemHelp());
        addActionListeners(ControllerMaintainDatabase.class        , appFrame.getMenuItemMaintainDatabase());
        addActionListeners(ControllerShowUpdateMetadataDialog.class, appFrame.getMenuItemScanDirectory());
        addActionListeners(ControllerShowUserSettingsDialog.class  , appFrame.getMenuItemSettings());
        addActionListeners(ControllerShowAdvancedSearchDialog.class, appFrame.getMenuItemSearch());
    }

    private void listenToPopupMenuKeywordsTrees() {
        PopupMenuKeywordsTree popup = PopupMenuKeywordsTree.INSTANCE;

        // Registerung not to more than 1 controller:
        // Popup menu stores tree and item of the action source
        addActionListener(ControllerAddKeyword.class                , popup.getItemAdd());
        addActionListener(ControllerRenameKeyword.class             , popup.getItemRename());
        addActionListener(ControllerDeleteKeywords.class            , popup.getItemRemove());
        addActionListener(ControllerAddKeywordsToEditPanel.class    , popup.getItemAddToEditPanel());
        addActionListener(ControllerDeleteKeywordFromEditPanel.class, popup.getItemRemoveFromEditPanel());
        addActionListener(ControllerToggleRealKeyword.class         , popup.getItemToggleReal());
        addActionListener(ControllerCopyCutPasteKeyword.class       , popup.getItemCopy());
        addActionListener(ControllerCopyCutPasteKeyword.class       , popup.getItemCut());
        addActionListener(ControllerCopyCutPasteKeyword.class       , popup.getItemPaste());
        addActionListener(ControllerKeywordsDisplayImages.class     , popup.getItemDisplayImages());
        addActionListener(ControllerKeywordsDisplayImages.class     , popup.getItemDisplayImagesKw());
    }

    private void addActionListener(Class<? extends ActionListener> listenerClass, JMenuItem item) {
        ActionListener listener = ControllerFactory.INSTANCE.getController(listenerClass);

        item.addActionListener(listener);
    }

    private void addActionListeners(Class<? extends ActionListener> listenerClass, JMenuItem item) {
        for (ActionListener listener : ControllerFactory.INSTANCE.getControllers(listenerClass)) {
                item.addActionListener(listener);
        }
    }

    private void addKeyListener(Class<? extends KeyListener> listenerClass, Component c) {
        KeyListener listener = ControllerFactory.INSTANCE.getController(listenerClass);

        c.addKeyListener(listener);
    }
}
