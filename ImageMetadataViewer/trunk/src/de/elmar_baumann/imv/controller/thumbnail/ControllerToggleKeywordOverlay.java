package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-16
 */
public final class ControllerToggleKeywordOverlay implements ActionListener {

    private static final String KEY_SHOW_METADATA_OVERLAY =
            "UserSettings.ShowMetadataOverlay"; // NOI18N
    private final AppFrame appFrame = GUI.INSTANCE.getAppFrame();

    public ControllerToggleKeywordOverlay() {
        listen();
        readPersistent();
    }

    private void listen() {
        appFrame.getCheckBoxMenuItemKeywordOverlay().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleKeywordOverlay();
        writePersistent();
    }

    private void toggleKeywordOverlay() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        boolean active = !appPanel.getPanelThumbnails().isKeywordsOverlay();
        appPanel.getPanelThumbnails().setKeywordsOverlay(active);
        appFrame.getCheckBoxMenuItemKeywordOverlay().setSelected(active);
    }

    private void readPersistent() {
        UserSettings settings = UserSettings.INSTANCE;
        if (settings.getProperties().containsKey(KEY_SHOW_METADATA_OVERLAY)) {
            boolean wasSelected =
                    settings.getSettings().getBoolean(KEY_SHOW_METADATA_OVERLAY);
            appFrame.getCheckBoxMenuItemKeywordOverlay().setSelected(wasSelected);
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().setKeywordsOverlay(
                    wasSelected);
        }
    }

    private void writePersistent() {
        UserSettings.INSTANCE.getSettings().setBoolean(
                appFrame.getCheckBoxMenuItemKeywordOverlay().isSelected(),
                KEY_SHOW_METADATA_OVERLAY);
        UserSettings.INSTANCE.writeToFile();
    }
}
