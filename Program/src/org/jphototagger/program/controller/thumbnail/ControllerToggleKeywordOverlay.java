package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 *
 * @author  Martin Pohlack
 */
public final class ControllerToggleKeywordOverlay implements ActionListener {
    private static final String KEY_SHOW_METADATA_OVERLAY = "UserSettings.ShowMetadataOverlay";

    public ControllerToggleKeywordOverlay() {
        listen();
        readPersistent();
    }

    private void listen() {
        GUI.getAppFrame().getCheckBoxMenuItemKeywordOverlay().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        toggleKeywordOverlay();
        writePersistent();
    }

    private void toggleKeywordOverlay() {
        AppPanel appPanel = GUI.getAppPanel();
        boolean active = !appPanel.getPanelThumbnails().isKeywordsOverlay();

        appPanel.getPanelThumbnails().setKeywordsOverlay(active);
        GUI.getAppFrame().getCheckBoxMenuItemKeywordOverlay().setSelected(active);
    }

    private void readPersistent() {
        UserSettings settings = UserSettings.INSTANCE;

        if (settings.getProperties().containsKey(KEY_SHOW_METADATA_OVERLAY)) {
            boolean wasSelected = settings.getSettings().getBoolean(KEY_SHOW_METADATA_OVERLAY);

            GUI.getAppFrame().getCheckBoxMenuItemKeywordOverlay().setSelected(wasSelected);
            GUI.getThumbnailsPanel().setKeywordsOverlay(wasSelected);
        }
    }

    private void writePersistent() {
        UserSettings.INSTANCE.getSettings().set(KEY_SHOW_METADATA_OVERLAY, GUI.getAppFrame().getCheckBoxMenuItemKeywordOverlay().isSelected());
        UserSettings.INSTANCE.writeToFile();
    }
}
