/*
 * @(#)ControllerExtractEmbeddedXmp.java    Created on 2009-05-22
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

package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.app.AppFileFilters;
import de.elmar_baumann.jpt.helper.ExtractEmbeddedXmp;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.FileEditorDialog;
import de.elmar_baumann.jpt.view.panels.FileEditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Starts a {@link de.elmar_baumann.jpt.view.dialogs.FileEditorDialog} with
 * an {@link de.elmar_baumann.jpt.helper.ExtractEmbeddedXmp} editor.
 *
 * @author  Elmar Baumann
 */
public final class ControllerExtractEmbeddedXmp implements ActionListener {
    public ControllerExtractEmbeddedXmp() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemExtractEmbeddedXmp()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }

    private void showDialog() {
        FileEditorDialog dialog = new FileEditorDialog();
        FileEditorPanel  panel  = dialog.getFileEditorPanel();

        panel.setEditor(new ExtractEmbeddedXmp());
        panel.setTitle(
            JptBundle.INSTANCE.getString(
                "ControllerExtractEmbeddedXmp.Panel.Title"));
        panel.setDescription(
            JptBundle.INSTANCE.getString(
                "ControllerExtractEmbeddedXmp.Panel.Description"));
        panel.setDirChooserFileFilter(
            AppFileFilters.ACCEPTED_IMAGE_FILENAME_FILTER);
        panel.setSelectDirs(true);
        dialog.setHelpPageUrl(
            JptBundle.INSTANCE.getString("Help.Url.ExtractEmbeddedXmp"));
        dialog.setVisible(true);
    }
}
