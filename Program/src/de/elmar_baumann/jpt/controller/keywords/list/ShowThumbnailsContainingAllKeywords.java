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
package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails of images containing all
 * specific keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-02
 */
public final class ShowThumbnailsContainingAllKeywords implements Runnable {

    private final DatabaseImageFiles       db              = DatabaseImageFiles.INSTANCE;
    private final ThumbnailsPanel          thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final EditMetadataPanels       editPanels      = GUI.INSTANCE.getAppPanel().getEditMetadataPanels();
    private final ThumbnailsPanel.Settings tnPanelSettings;
    private final List<String>             keywords;

    /**
     * Creates a new instance of this class.
     *
     * @param keywords all keywords a image must have to be displayed
     * @param settings
     */
    public ShowThumbnailsContainingAllKeywords(List<String> keywords, ThumbnailsPanel.Settings settings) {
        this.keywords     = new ArrayList<String>(keywords);
        tnPanelSettings   = settings;
    }

    @Override
    public void run() {
        setFilesToThumbnailsPanel();
        setMetadataEditable();
    }

    private void setFilesToThumbnailsPanel() {
        Set<String> filenames = getFilenamesOfKeywords();
        if (filenames != null) {
            ControllerSortThumbnails.setLastSort();
            thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.KEYWORD);
            thumbnailsPanel.apply(tnPanelSettings);
        }
    }

    private Set<String> getFilenamesOfKeywords() {
        // Faster than using 2 different DB queries if only 1 keyword is selected
        if (keywords.size() == 1) {
            setTitle(keywords.get(0));
            return db.getFilenamesOfDcSubject(keywords.get(0));
        } else if (keywords.size() > 1) {
            setTitle(keywords);
            return db.getFilenamesOfAllDcSubjects(keywords);
        }
        return null;
    }

    private void setTitle(List<String> keywords) {
        GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString("ShowThumbnailsContainingAllKeywords.AppFrame.Title.Keywords.Path", Util.keywordPathString(keywords)));
    }

    private void setTitle(String keyword) {
        GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString("ShowThumbnailsContainingAllKeywords.AppFrame.Title.Keyword", keyword));
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
