/*
 * @(#)ShowThumbnailsContainingKeywords.java    Created on 2009-09-02
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

package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails with specific keywords.
 *
 * @author  Elmar Baumann
 */
public final class ShowThumbnailsContainingKeywords implements Runnable {
    private final DatabaseImageFiles db      = DatabaseImageFiles.INSTANCE;
    private final ThumbnailsPanel    tnPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final EditMetadataPanels editPanels =
        GUI.INSTANCE.getAppPanel().getEditMetadataPanels();
    private final List<String>             keywords;
    private final ThumbnailsPanel.Settings tnPanelSettings;

    /**
     * Creates a new instance of this class.
     *
     * @param keywords one of that keywords a image must have to be displayed
     * @param settings
     */
    public ShowThumbnailsContainingKeywords(List<String> keywords,
            ThumbnailsPanel.Settings settings) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        this.keywords   = new ArrayList<String>(keywords);
        tnPanelSettings = settings;
    }

    @Override
    public void run() {
        setFilesToThumbnailsPanel();
        setMetadataEditable();
    }

    private void setFilesToThumbnailsPanel() {
        List<File> imageFiles =
            new ArrayList<File>(getImageFilesOfSelectedKeywords());

        if (imageFiles != null) {
            ControllerSortThumbnails.setLastSort();
            tnPanel.setFiles(imageFiles, Content.KEYWORD);
            tnPanel.apply(tnPanelSettings);
        }
    }

    private Set<File> getImageFilesOfSelectedKeywords() {

        // Faster than using 2 different DB queries if only 1 keyword is
        // selected
        if (keywords.size() == 1) {
            setTitle(keywords.get(0));

            return db.getImageFilesOfDcSubject(keywords.get(0));
        } else if (keywords.size() > 1) {
            setTitle(keywords);

            return db.getImageFilesOfDcSubjects(keywords);
        }

        return null;
    }

    private void setTitle(List<String> keywords) {
        GUI.INSTANCE.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString(
                "ShowThumbnailsContainingKeywords.AppFrame.Title.Keywords.Path",
                Util.keywordPathString(keywords)));
    }

    private void setTitle(String keyword) {
        GUI.INSTANCE.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString(
                "ShowThumbnailsContainingKeywords.AppFrame.Title.Keyword",
                keyword));
    }

    private void setMetadataEditable() {
        if (!tnPanel.isFileSelected()) {
            editPanels.setEditable(false);
        }
    }
}
