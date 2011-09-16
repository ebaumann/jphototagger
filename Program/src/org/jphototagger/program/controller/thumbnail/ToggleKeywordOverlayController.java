package org.jphototagger.program.controller.thumbnail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Storage;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.panels.AppPanel;

/**
 *
 *
 * @author  Martin Pohlack
 */
public final class ToggleKeywordOverlayController implements ActionListener {

    private static final String KEY_SHOW_METADATA_OVERLAY = "UserSettings.ShowMetadataOverlay";

    public ToggleKeywordOverlayController() {
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
        AppFrame appFrame = GUI.getAppFrame();
        JCheckBoxMenuItem checkBoxMenuItemKeywordOverlay = appFrame.getCheckBoxMenuItemKeywordOverlay();
        boolean selected = checkBoxMenuItemKeywordOverlay.isSelected();

        storage.setBoolean(KEY_SHOW_METADATA_OVERLAY, selected);
    }
}
