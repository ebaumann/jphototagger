/*
 * @(#)ControllerCreateMetadataOfSelectedThumbnails.java    Created on 2008-10-05
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

import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * <em>Nur eine Instanz erzeugen!</em>
 *
 * @author  Elmar Baumann
 */
public final class ControllerCreateMetadataOfSelectedThumbnails
        implements ActionListener {
    private final Map<JMenuItem, Insert[]> databaseUpdateOfMenuItem =
        new HashMap<JMenuItem, Insert[]>();
    private final PopupMenuThumbnails popupMenu = PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel     tnPanel   =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        initDatabaseUpdateOfMenuItem();
        listen();
    }

    private void initDatabaseUpdateOfMenuItem() {
        databaseUpdateOfMenuItem.put(popupMenu.getItemUpdateMetadata(),
                                     new Insert[] { Insert.EXIF,
                Insert.XMP });
        databaseUpdateOfMenuItem.put(popupMenu.getItemUpdateThumbnail(),
                                     new Insert[] { Insert.THUMBNAIL });
    }

    private Insert[] getMetadataToInsertIntoDatabase(Object o) {
        if (o instanceof JMenuItem) {
            return databaseUpdateOfMenuItem.get((JMenuItem) o);
        }

        return new Insert[] { Insert.OUT_OF_DATE };
    }

    private void listen() {
        popupMenu.getItemUpdateThumbnail().addActionListener(this);
        popupMenu.getItemUpdateMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (tnPanel.isFileSelected()) {
            updateMetadata(getMetadataToInsertIntoDatabase(evt.getSource()));
        }
    }

    private void updateMetadata(Insert[] what) {
        InsertImageFilesIntoDatabase inserter =
            new InsertImageFilesIntoDatabase(tnPanel.getSelectedFiles(), what);
        String pBarString =
            JptBundle.INSTANCE.getString(
                "ControllerCreateMetadataOfSelectedThumbnails.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter,
                pBarString));
        UserTasks.INSTANCE.add(inserter);
    }
}
