/*
 * @(#)ShowThumbnailsContainingAllKeywords2.java    Created on 2009-09-02
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

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails of images containing all
 * specific keywords.
 *
 * @author  Elmar Baumann
 */
public final class ShowThumbnailsContainingAllKeywords2 implements Runnable {
    private final DatabaseImageFiles db              =
        DatabaseImageFiles.INSTANCE;
    private final ThumbnailsPanel    thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final EditMetadataPanels editPanels =
        GUI.INSTANCE.getAppPanel().getEditMetadataPanels();
    private final List<List<String>> keywordLists;

    /**
     * Creates a new instance of this class.
     *
     * @param keywordLists all keywords a image must have to be displayed
     */
    public ShowThumbnailsContainingAllKeywords2(
            List<List<String>> keywordLists) {
        this.keywordLists = deepCopy(keywordLists);
    }

    @Override
    public void run() {
        setFilesToThumbnailsPanel();
        setMetadataEditable();
    }

    private void setFilesToThumbnailsPanel() {
        List<File> imageFiles = getImageFilesOfKeywords();

        ControllerSortThumbnails.setLastSort();
        thumbnailsPanel.setFiles(imageFiles, Content.KEYWORD);
    }

    private List<File> getImageFilesOfKeywords() {
        List<File> imageFiles = new ArrayList<File>();

        for (List<String> keywords : keywordLists) {

            // Faster when using 2 different DB queries if only 1 keyword is
            // selected
            if (keywords.size() == 1) {
                imageFiles.addAll(db.getImageFilesOfDcSubject(keywords.get(0)));
                setTitle(keywords.get(0));
            } else if (keywords.size() > 1) {
                setTitle(keywords);
                imageFiles.addAll(db.getImageFilesOfAllDcSubjects(keywords));
            }
        }

        return imageFiles;
    }

    private void setTitle(List<String> keywords) {
        GUI.INSTANCE.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString(
                "ShowThumbnailsContainingAllKeywords2.AppFrame.Title.Keywords.Path",
                Util.keywordPathString(keywords)));
    }

    private void setTitle(String keyword) {
        GUI.INSTANCE.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString(
                "ShowThumbnailsContainingAllKeywords2.AppFrame.Title.Keyword",
                keyword));
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }

    private List<List<String>> deepCopy(List<List<String>> kwLists) {
        List<List<String>> copy = new ArrayList<List<String>>(kwLists.size());

        for (List<String> kwList : kwLists) {
            copy.add(new ArrayList<String>(kwList));
        }

        return copy;
    }
}
