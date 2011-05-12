package org.jphototagger.program.factory;

import org.jphototagger.program.controller.keywords.tree.ControllerAddKeyword;
import org.jphototagger.program.controller.keywords.tree.ControllerAddKeywordsToEditPanel;
import org.jphototagger.program.controller.keywords.tree.ControllerCopyCutPasteKeyword;
import org.jphototagger.program.controller.keywords.tree.ControllerDeleteKeywordFromEditPanel;
import org.jphototagger.program.controller.keywords.tree.ControllerDeleteKeywords;
import org.jphototagger.program.controller.keywords.tree.ControllerKeywordsDisplayImages;
import org.jphototagger.program.controller.keywords.tree.ControllerRenameKeyword;
import org.jphototagger.program.controller.keywords.tree.ControllerToggleRealKeyword;
import org.jphototagger.program.controller.metadata.ControllerShowUpdateMetadataDialog;
import org.jphototagger.program.controller.misc.ControllerAboutApp;
import org.jphototagger.program.controller.misc.ControllerHelp;
import org.jphototagger.program.controller.misc.ControllerMaintainDatabase;
import org.jphototagger.program.controller.misc.ControllerShowUserSettingsDialog;
import org.jphototagger.program.controller.search.ControllerShowAdvancedSearchDialog;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class ActionKeyListenerFactory {
    static final ActionKeyListenerFactory INSTANCE = new ActionKeyListenerFactory();
    private boolean init;

    synchronized void init() {
        Support.checkInit(ActionKeyListenerFactory.class, init);

        if (!init) {
            init = true;
            EventQueueUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    addActionListeners();
                    addKeyListeners();
                }
            });
        }
    }

    private void addActionListeners() {
        listenToAppFrameMenuItems();
        listenToPopupMenuKeywordsTrees();
    }

    private void addKeyListeners() {
        addKeyListener(ControllerCopyCutPasteKeyword.class, GUI.getAppPanel().getTreeEditKeywords());
        addKeyListener(ControllerCopyCutPasteKeyword.class, InputHelperDialog.INSTANCE.getPanelKeywords().getTree());
    }

    private void listenToAppFrameMenuItems() {
        AppFrame appFrame = GUI.getAppFrame();

        addActionListeners(ControllerAboutApp.class, appFrame.getMenuItemAbout());
        addActionListeners(ControllerHelp.class, appFrame.getMenuItemHelp());
        addActionListeners(ControllerMaintainDatabase.class, appFrame.getMenuItemMaintainDatabase());
        addActionListeners(ControllerShowUpdateMetadataDialog.class, appFrame.getMenuItemScanDirectory());
        addActionListeners(ControllerShowUserSettingsDialog.class, appFrame.getMenuItemSettings());
        addActionListeners(ControllerShowAdvancedSearchDialog.class, appFrame.getMenuItemSearch());
    }

    private void listenToPopupMenuKeywordsTrees() {
        PopupMenuKeywordsTree popup = PopupMenuKeywordsTree.INSTANCE;

        // Registerung not to more than 1 controller:
        // Popup menu stores tree and item of the action source
        addActionListener(ControllerAddKeyword.class, popup.getItemAdd());
        addActionListener(ControllerRenameKeyword.class, popup.getItemRename());
        addActionListener(ControllerDeleteKeywords.class, popup.getItemRemove());
        addActionListener(ControllerAddKeywordsToEditPanel.class, popup.getItemAddToEditPanel());
        addActionListener(ControllerDeleteKeywordFromEditPanel.class, popup.getItemRemoveFromEditPanel());
        addActionListener(ControllerToggleRealKeyword.class, popup.getItemToggleReal());
        addActionListener(ControllerCopyCutPasteKeyword.class, popup.getItemCopy());
        addActionListener(ControllerCopyCutPasteKeyword.class, popup.getItemCut());
        addActionListener(ControllerCopyCutPasteKeyword.class, popup.getItemPaste());
        addActionListener(ControllerKeywordsDisplayImages.class, popup.getItemDisplayImages());
        addActionListener(ControllerKeywordsDisplayImages.class, popup.getItemDisplayImagesKw());
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
