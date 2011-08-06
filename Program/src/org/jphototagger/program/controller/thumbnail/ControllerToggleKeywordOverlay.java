package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jphototagger.api.core.Storage;
import org.openide.util.Lookup;

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
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        if (storage.containsKey(KEY_SHOW_METADATA_OVERLAY)) {
            boolean wasSelected = storage.getBoolean(KEY_SHOW_METADATA_OVERLAY);

            GUI.getAppFrame().getCheckBoxMenuItemKeywordOverlay().setSelected(wasSelected);
            GUI.getThumbnailsPanel().setKeywordsOverlay(wasSelected);
        }
    }

    private void writePersistent() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        storage.setBoolean(KEY_SHOW_METADATA_OVERLAY, GUI.getAppFrame().getCheckBoxMenuItemKeywordOverlay().isSelected());
        UserSettings.INSTANCE.writeToFile();
    }
}
