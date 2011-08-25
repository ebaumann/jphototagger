package org.jphototagger.program.controller.keywords.list;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.Lookup;

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails of images containing all
 * specific keywords.
 *
 * @author Elmar Baumann
 */
public final class ShowThumbnailsContainingAllKeywords implements Runnable {

    private final List<String> keywords;
    private final ThumbnailsPanel.Settings tnPanelSettings;
    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    /**
     * Creates a new instance of this class.
     *
     * @param keywords all keywords a image must have to be displayed
     * @param settings
     */
    public ShowThumbnailsContainingAllKeywords(List<String> keywords, ThumbnailsPanel.Settings settings) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        this.keywords = new ArrayList<String>(keywords);
        tnPanelSettings = settings;
    }

    @Override
    public void run() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplay.show();
                setFilesToThumbnailsPanel();
                setMetadataEditable();
                WaitDisplay.hide();
            }
        });
    }

    private void setFilesToThumbnailsPanel() {
        List<File> imageFiles = new ArrayList<File>(getImageFilesOfKeywords());

        if (imageFiles != null) {
            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            ControllerSortThumbnails.setLastSort();
            tnPanel.setFiles(imageFiles, TypeOfDisplayedImages.KEYWORD);
            tnPanel.apply(tnPanelSettings);
        }
    }

    private Set<File> getImageFilesOfKeywords() {

        // Faster than using 2 different DB queries if only 1 keyword
        // is selected
        if (keywords.size() == 1) {
            setTitle(keywords.get(0));

            return repo.getImageFilesContainingDcSubject(keywords.get(0), false);
        } else if (keywords.size() > 1) {
            setTitle(keywords);

            return repo.getImageFilesContainingAllDcSubjects(keywords);
        }

        return null;
    }

    private void setTitle(List<String> keywords) {
        GUI.getAppFrame().setTitle(
                Bundle.getString(ShowThumbnailsContainingAllKeywords.class,
                "ShowThumbnailsContainingAllKeywords.AppFrame.Title.Keywords.Path", Util.keywordPathString(keywords)));
    }

    private void setTitle(String keyword) {
        GUI.getAppFrame().setTitle(
                Bundle.getString(ShowThumbnailsContainingAllKeywords.class, "ShowThumbnailsContainingAllKeywords.AppFrame.Title.Keyword", keyword));
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }
}
