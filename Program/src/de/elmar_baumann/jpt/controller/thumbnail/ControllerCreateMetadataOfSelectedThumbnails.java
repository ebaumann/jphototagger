/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.thumbnail;

import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ProgressBarUserTasks;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von {@link de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails}.
 * 
 * <em>Nur eine Instanz erzeugen!</em>
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerCreateMetadataOfSelectedThumbnails
        implements ActionListener {

    private final Map<JMenuItem, EnumSet<InsertImageFilesIntoDatabase.Insert>> databaseUpdateOfMenuItem =
            new HashMap<JMenuItem, EnumSet<InsertImageFilesIntoDatabase.Insert>>();
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        initDatabaseUpdateOfMenuItem();
        listen();
    }

    private void initDatabaseUpdateOfMenuItem() {

        databaseUpdateOfMenuItem.put(
                popupMenu.getItemUpdateMetadata(), EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.EXIF,
                InsertImageFilesIntoDatabase.Insert.XMP));
        databaseUpdateOfMenuItem.put(
                popupMenu.getItemUpdateThumbnail(), EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.THUMBNAIL));
    }

    private EnumSet<InsertImageFilesIntoDatabase.Insert> getMetadataToInsertIntoDatabase(
            Object o) {
        if (o instanceof JMenuItem) {
            return databaseUpdateOfMenuItem.get((JMenuItem) o);
        }
        return EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE);
    }

    private void listen() {
        popupMenu.getItemUpdateThumbnail().addActionListener(this);
        popupMenu.getItemUpdateMetadata().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            updateMetadata(getMetadataToInsertIntoDatabase(e.getSource()));
        }
    }

    private void updateMetadata(
            EnumSet<InsertImageFilesIntoDatabase.Insert> what) {

        UserTasks.INSTANCE.add(new InsertImageFilesIntoDatabase(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()),
                what,
                ProgressBarUserTasks.INSTANCE));
    }
}
