/*
 * @(#)ControllerThumbnailFileFilter.java    Created on 2010-03-29
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.model.ComboBoxModelFileFilters;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.UserDefinedFileFilterDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.FileFilter;

import javax.swing.JComboBox;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailFileFilter
        implements ActionListener, ItemListener {
    public ControllerThumbnailFileFilter() {
        getFileFilterComboBox().addItemListener(this);
        GUI.getAppFrame().getMenuItemUserDefinedFileFilter().addActionListener(
            this);
    }

    private JComboBox getFileFilterComboBox() {
        return GUI.getAppPanel().getComboBoxFileFilters();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        new UserDefinedFileFilterDialog().setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        Object          item    = evt.getItem();
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (item instanceof FileFilter) {
            tnPanel.setFileFilter((FileFilter) item);
        } else if (item instanceof UserDefinedFileFilter) {
            tnPanel.setFileFilter(
                ((UserDefinedFileFilter) item).getFileFilter());
        }

        writeSettings();
    }

    private void writeSettings() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(getFileFilterComboBox().getSelectedIndex(),
                     ComboBoxModelFileFilters.SETTINGS_KEY_SEL_INDEX);
        UserSettings.INSTANCE.writeToFile();
    }
}
