package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.controller.misc.ControllerAboutApp;
import de.elmar_baumann.imv.controller.misc.ControllerHelp;
import de.elmar_baumann.imv.controller.misc.ControllerMaintainDatabase;
import de.elmar_baumann.imv.controller.search.ControllerShowAdvancedSearchDialog;
import de.elmar_baumann.imv.controller.metadata.ControllerShowUpdateMetadataDialog;
import de.elmar_baumann.imv.controller.misc.ControllerShowUserSettingsDialog;
import de.elmar_baumann.imv.controller.thumbnail.ControllerRefreshThumbnailsPanel;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;

/**
 * Erzeugt Actionlistener und verknüpft sie mit Aktionsquellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class ActionListenerFactory {
    
    private static final ActionListenerFactory instance = new ActionListenerFactory();
    
    static ActionListenerFactory getInstance() {
        return instance;
    }
    
    private ActionListenerFactory() {
        createActionListener();
    }

    private void createActionListener() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        AppFrame appFrame = Panels.getInstance().getAppFrame();
        
        appFrame.getMenuItemAbout().addActionListener(new ControllerAboutApp());
        appFrame.getMenuItemHelp().addActionListener(new ControllerHelp());
        appFrame.getMenuItemMaintainDatabase().addActionListener(new ControllerMaintainDatabase());
        appFrame.getMenuItemRefresh().addActionListener(new ControllerRefreshThumbnailsPanel(appPanel.getPanelThumbnails()));
        appFrame.getMenuItemScanDirectory().addActionListener(new ControllerShowUpdateMetadataDialog());
        appFrame.getMenuItemSettings().addActionListener(new ControllerShowUserSettingsDialog());
        appFrame.getMenuItemSearch().addActionListener(new ControllerShowAdvancedSearchDialog());
    }

}
