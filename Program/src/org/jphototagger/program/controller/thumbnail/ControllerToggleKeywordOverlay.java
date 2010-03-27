/*
 * @(#)ControllerToggleKeywordOverlay.java    Created on 2009-07-16
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.panels.AppPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 *
 * @author  Martin Pohlack
 */
public final class ControllerToggleKeywordOverlay implements ActionListener {
    private static final String KEY_SHOW_METADATA_OVERLAY =
        "UserSettings.ShowMetadataOverlay";
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
        boolean  active   = !appPanel.getPanelThumbnails().isKeywordsOverlay();

        appPanel.getPanelThumbnails().setKeywordsOverlay(active);
        appFrame.getCheckBoxMenuItemKeywordOverlay().setSelected(active);
    }

    private void readPersistent() {
        UserSettings settings = UserSettings.INSTANCE;

        if (settings.getProperties().containsKey(KEY_SHOW_METADATA_OVERLAY)) {
            boolean wasSelected =
                settings.getSettings().getBoolean(KEY_SHOW_METADATA_OVERLAY);

            appFrame.getCheckBoxMenuItemKeywordOverlay().setSelected(
                wasSelected);
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().setKeywordsOverlay(
                wasSelected);
        }
    }

    private void writePersistent() {
        UserSettings.INSTANCE.getSettings().set(
            appFrame.getCheckBoxMenuItemKeywordOverlay().isSelected(),
            KEY_SHOW_METADATA_OVERLAY);
        UserSettings.INSTANCE.writeToFile();
    }
}
