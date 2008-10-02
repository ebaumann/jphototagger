package de.elmar_baumann.imagemetadataviewer.factory;

import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerAboutApp;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerHelp;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerMaintainDatabase;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerShowAdvancedSearchDialog;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerShowUpdateMetadataDialog;
import de.elmar_baumann.imagemetadataviewer.controller.misc.ControllerShowUserSettingsDialog;
import de.elmar_baumann.imagemetadataviewer.controller.thumbnail.ControllerRefreshThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.frames.AppFrame;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;

/**
 * Erzeugt Actionlistener und verknüpft sie mit Aktionsquellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class ActionListenerFactory {
    
    private static ActionListenerFactory instance = new ActionListenerFactory();
    
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
        appFrame.getMenuItemRefresh().addActionListener(new ControllerRefreshThumbnailsPanel(appPanel.getPanelImageFileThumbnails()));
        appFrame.getMenuItemScanDirectory().addActionListener(new ControllerShowUpdateMetadataDialog());
        appFrame.getMenuItemSettings().addActionListener(new ControllerShowUserSettingsDialog());
        appFrame.getMenuItemSearch().addActionListener(new ControllerShowAdvancedSearchDialog());
    }

}
