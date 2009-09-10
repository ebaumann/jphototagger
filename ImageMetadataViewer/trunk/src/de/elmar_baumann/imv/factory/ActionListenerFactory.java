package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerAddHierarchicalKeyword;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerAddHierarchicalKeywordsToEditPanel;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerCutPasteHierarchicalKeyword;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerHierarchicalKeywordsDisplayImages;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerRemoveHierarchicalKeyword;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerRemoveHierarchicalKeywordFromEditPanel;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerRenameHierarchicalKeyword;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.ControllerToggleRealHierarchicalKeyword;
import de.elmar_baumann.imv.controller.misc.ControllerAboutApp;
import de.elmar_baumann.imv.controller.misc.ControllerHelp;
import de.elmar_baumann.imv.controller.misc.ControllerMaintainDatabase;
import de.elmar_baumann.imv.controller.search.ControllerShowAdvancedSearchDialog;
import de.elmar_baumann.imv.controller.metadata.ControllerShowUpdateMetadataDialog;
import de.elmar_baumann.imv.controller.misc.ControllerShowUserSettingsDialog;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.InputHelperDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;

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

            appFrame.getMenuItemAbout().addActionListener(
                    new ControllerAboutApp());
            ControllerHelp ctrlHelp = new ControllerHelp();
            appFrame.getMenuItemHelp().addActionListener(ctrlHelp);
            appFrame.getMenuItemAcceleratorKeys().addActionListener(ctrlHelp);
            appFrame.getMenuItemMaintainDatabase().addActionListener(
                    new ControllerMaintainDatabase());
            appFrame.getMenuItemScanDirectory().addActionListener(
                    new ControllerShowUpdateMetadataDialog());
            appFrame.getMenuItemSettings().addActionListener(
                    new ControllerShowUserSettingsDialog());
            appFrame.getMenuItemSearch().addActionListener(
                    new ControllerShowAdvancedSearchDialog());
            listenToPopupMenuHierarchicalKeywords();
        }
    }

    /**
     * Creates the controller and let's them listen to the
     * {@link PopupMenuHierarchicalKeywords} because it's a singleton but more
     * more then one panel using it (2 panels: popup twice, 3 p. 3 times ...)
     */
    private void listenToPopupMenuHierarchicalKeywords() {
        HierarchicalKeywordsPanel hkwPanel =
                InputHelperDialog.INSTANCE.getPanelKeywords();
        ControllerRenameHierarchicalKeyword cRename =
                new ControllerRenameHierarchicalKeyword(hkwPanel);
        ControllerRemoveHierarchicalKeyword cRemove =
                new ControllerRemoveHierarchicalKeyword(hkwPanel);
        ControllerAddHierarchicalKeyword cAdd =
                new ControllerAddHierarchicalKeyword(hkwPanel);
        ControllerToggleRealHierarchicalKeyword cToggleReal =
                new ControllerToggleRealHierarchicalKeyword(hkwPanel);
        ControllerAddHierarchicalKeywordsToEditPanel cAddToEditPanel =
                new ControllerAddHierarchicalKeywordsToEditPanel(hkwPanel);
        ControllerRemoveHierarchicalKeywordFromEditPanel cRemoveFromEPn =
                new ControllerRemoveHierarchicalKeywordFromEditPanel(hkwPanel);
        ControllerCutPasteHierarchicalKeyword cCutPaste =
                new ControllerCutPasteHierarchicalKeyword(hkwPanel);
        ControllerHierarchicalKeywordsDisplayImages cShowImages =
                new ControllerHierarchicalKeywordsDisplayImages(hkwPanel);
        PopupMenuHierarchicalKeywords popup =
                PopupMenuHierarchicalKeywords.INSTANCE;
        popup.getMenuItemAdd().addActionListener(cAdd);
        popup.getMenuItemRename().addActionListener(cRename);
        popup.getMenuItemRemove().addActionListener(cRemove);
        popup.getMenuItemAddToEditPanel().addActionListener(cAddToEditPanel);
        popup.getMenuItemRemoveFromEditPanel().addActionListener(cRemoveFromEPn);
        popup.getMenuItemToggleReal().addActionListener(cToggleReal);
        popup.getMenuItemCut().addActionListener(cCutPaste);
        popup.getMenuItemPaste().addActionListener(cCutPaste);
        popup.getMenuItemDisplayImages().addActionListener(cShowImages);
    }
}
