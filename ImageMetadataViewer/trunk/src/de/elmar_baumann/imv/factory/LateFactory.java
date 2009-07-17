package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;

/**
 * Methods causing the form designer fail to display the AppFrame are called
 * here.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/16
 */
public final class LateFactory {

    static final LateFactory INSTANCE = new LateFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(LateFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            PopupMenuThumbnails popupMenuPanelThumbnails =
                    PopupMenuThumbnails.INSTANCE;
            UserSettings userSettings = UserSettings.INSTANCE;

            DatabaseImageFiles.INSTANCE.addDatabaseListener(
                    appPanel.getEditPanelsArray());
            appPanel.getEditPanelsArray().setAutocomplete();

            popupMenuPanelThumbnails.addOtherPrograms();
            ListenerProvider listenerProvider = ListenerProvider.INSTANCE;
            listenerProvider.addUserSettingsChangeListener(
                    popupMenuPanelThumbnails);
            listenerProvider.addUserSettingsChangeListener(userSettings);
            GUI.INSTANCE.getAppFrame().addAppExitListener(
                    appPanel.getPanelThumbnails());
        }
    }
}
