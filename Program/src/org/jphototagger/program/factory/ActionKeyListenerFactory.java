package org.jphototagger.program.factory;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.controller.keywords.tree.AddKeywordController;
import org.jphototagger.program.controller.keywords.tree.AddKeywordsToEditPanelController;
import org.jphototagger.program.controller.keywords.tree.CopyCutPasteKeywordController;
import org.jphototagger.program.controller.keywords.tree.DeleteKeywordFromEditPanelController;
import org.jphototagger.program.controller.keywords.tree.DeleteKeywordsController;
import org.jphototagger.program.controller.keywords.tree.KeywordsDisplayImagesController;
import org.jphototagger.program.controller.keywords.tree.RenameKeywordController;
import org.jphototagger.program.controller.keywords.tree.ToggleRealKeywordController;
import org.jphototagger.program.controller.metadata.ShowUpdateMetadataDialogController;
import org.jphototagger.program.controller.misc.AboutJPhotoTaggerController;
import org.jphototagger.program.controller.misc.HelpController;
import org.jphototagger.program.controller.misc.MaintainRepositoryController;
import org.jphototagger.program.controller.misc.ShowUserSettingsDialogController;
import org.jphototagger.program.controller.search.ShowAdvancedSearchDialogController;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.popupmenus.KeywordsTreePopupMenu;

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
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
        addKeyListener(CopyCutPasteKeywordController.class, GUI.getAppPanel().getTreeEditKeywords());
        addKeyListener(CopyCutPasteKeywordController.class, InputHelperDialog.INSTANCE.getPanelKeywords().getTree());
    }

    private void listenToAppFrameMenuItems() {
        AppFrame appFrame = GUI.getAppFrame();

        addActionListeners(AboutJPhotoTaggerController.class, appFrame.getMenuItemAbout());
        addActionListeners(HelpController.class, appFrame.getMenuItemHelp());
        addActionListeners(MaintainRepositoryController.class, appFrame.getMenuItemMaintainDatabase());
        addActionListeners(ShowUpdateMetadataDialogController.class, appFrame.getMenuItemScanDirectory());
        addActionListeners(ShowUserSettingsDialogController.class, appFrame.getMenuItemSettings());
        addActionListeners(ShowAdvancedSearchDialogController.class, appFrame.getMenuItemSearch());
    }

    private void listenToPopupMenuKeywordsTrees() {
        KeywordsTreePopupMenu popup = KeywordsTreePopupMenu.INSTANCE;

        // Registerung not to more than 1 controller:
        // Popup menu stores tree and item of the action source
        addActionListener(AddKeywordController.class, popup.getItemAdd());
        addActionListener(RenameKeywordController.class, popup.getItemRename());
        addActionListener(DeleteKeywordsController.class, popup.getItemRemove());
        addActionListener(AddKeywordsToEditPanelController.class, popup.getItemAddToEditPanel());
        addActionListener(DeleteKeywordFromEditPanelController.class, popup.getItemRemoveFromEditPanel());
        addActionListener(ToggleRealKeywordController.class, popup.getItemToggleReal());
        addActionListener(CopyCutPasteKeywordController.class, popup.getItemCopy());
        addActionListener(CopyCutPasteKeywordController.class, popup.getItemCut());
        addActionListener(CopyCutPasteKeywordController.class, popup.getItemPaste());
        addActionListener(KeywordsDisplayImagesController.class, popup.getItemDisplayImages());
        addActionListener(KeywordsDisplayImagesController.class, popup.getItemDisplayImagesKw());
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
