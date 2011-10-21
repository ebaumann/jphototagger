package org.jphototagger.program.factory;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.keywords.tree.AddKeywordToTreeController;
import org.jphototagger.program.module.keywords.tree.AddKeywordsToEditPanelController;
import org.jphototagger.program.module.keywords.tree.CopyCutPasteKeywordController;
import org.jphototagger.program.module.keywords.tree.DeleteKeywordFromEditPanelController;
import org.jphototagger.program.module.keywords.tree.DeleteKeywordsFromTreeController;
import org.jphototagger.program.module.keywords.tree.KeywordsDisplayImagesController;
import org.jphototagger.program.module.keywords.tree.KeywordsTreePopupMenu;
import org.jphototagger.program.module.keywords.tree.RenameKeywordController;
import org.jphototagger.program.module.keywords.tree.ToggleRealKeywordController;
import org.jphototagger.program.resource.GUI;

/**
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
        listenToPopupMenuKeywordsTrees();
    }

    private void addKeyListeners() {
        addKeyListener(CopyCutPasteKeywordController.class, GUI.getAppPanel().getTreeEditKeywords());
        addKeyListener(CopyCutPasteKeywordController.class, InputHelperDialog.INSTANCE.getPanelKeywords().getTree());
    }

    private void listenToPopupMenuKeywordsTrees() {
        KeywordsTreePopupMenu popup = KeywordsTreePopupMenu.INSTANCE;

        // Registerung not to more than 1 controller:
        // Popup menu stores tree and item of the action source
        addActionListener(AddKeywordToTreeController.class, popup.getItemAdd());
        addActionListener(RenameKeywordController.class, popup.getItemRename());
        addActionListener(DeleteKeywordsFromTreeController.class, popup.getItemRemove());
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
