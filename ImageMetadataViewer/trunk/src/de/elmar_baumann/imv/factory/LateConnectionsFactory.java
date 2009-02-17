package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;

/**
 * Methods causing the form designer fail to display the AppFrame are called here. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/16
 */
public final class LateConnectionsFactory {

    static final LateConnectionsFactory INSTANCE = new LateConnectionsFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(LateConnectionsFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = Panels.getInstance().getAppPanel();
            PopupMenuPanelThumbnails popupMenuPanelThumbnails = PopupMenuPanelThumbnails.getInstance();
            UserSettings userSettings = UserSettings.getInstance();

            appPanel.getPanelThumbnails().setDefaultThumbnailWidth(userSettings.getMaxThumbnailWidth());

            DatabaseImageFiles.getInstance().addDatabaseListener(appPanel.getEditPanelsArray());
            if (userSettings.isUseAutocomplete()) {
                appPanel.getEditPanelsArray().setAutocomplete();
            }

            popupMenuPanelThumbnails.addOtherPrograms();
            ListenerProvider listenerProvider = ListenerProvider.getInstance();
            listenerProvider.addUserSettingsChangeListener(popupMenuPanelThumbnails);
            listenerProvider.addUserSettingsChangeListener(userSettings);
        }
    }
}
