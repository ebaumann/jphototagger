package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;

/**
 * Methods causing the form designer fail to display the AppFrame are called here. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/16
 */
public class LateConnectionsFactory {

    static LateConnectionsFactory instance = new LateConnectionsFactory();

    static LateConnectionsFactory getInstance() {
        return instance;
    }

    private LateConnectionsFactory() {
        init();
    }

    private void init() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        PopupMenuPanelThumbnails popupMenuPanelThumbnails = PopupMenuPanelThumbnails.getInstance();
        UserSettings userSettings = UserSettings.getInstance();
        
        Panels.getInstance().getAppFrame().addAppStartListener(appPanel);
        Panels.getInstance().getAppFrame().addAppExitListener(appPanel);
        
        appPanel.getPanelThumbnails().setDefaultThumbnailWidth(userSettings.getMaxThumbnailWidth());
        
        Database.getInstance().addDatabaseListener(appPanel.getEditPanelsArray());
        if (userSettings.isUseAutocomplete()) {
            appPanel.getEditPanelsArray().setAutocomplete();
        }
        
        popupMenuPanelThumbnails.addOtherOpenImageApps();
        UserSettingsDialog.getInstance().addChangeListener(popupMenuPanelThumbnails);
        UserSettingsDialog.getInstance().addChangeListener(userSettings);
    }
}
