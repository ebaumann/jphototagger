package org.jphototagger.program.controller.keywords.list;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails of images containing all
 * specific keywords.
 *
 * @author Elmar Baumann
 */
public final class ShowThumbnailsContainingAllKeywords2 implements Runnable {
    private final List<List<String>> keywordLists;

    /**
     * Creates a new instance of this class.
     *
     * @param keywordLists all keywords a image must have to be displayed
     */
    public ShowThumbnailsContainingAllKeywords2(List<List<String>> keywordLists) {
        if (keywordLists == null) {
            throw new NullPointerException("keywordLists == null");
        }

        this.keywordLists = deepCopy(keywordLists);
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
        List<File> imageFiles = getImageFilesOfKeywords();

        ControllerSortThumbnails.setLastSort();
        GUI.getThumbnailsPanel().setFiles(imageFiles, TypeOfDisplayedImages.KEYWORD);
    }

    private List<File> getImageFilesOfKeywords() {
        List<File> imageFiles = new ArrayList<File>();

        for (List<String> keywords : keywordLists) {
            DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

            // Faster when using 2 different DB queries if only 1 keyword is
            // selected
            if (keywords.size() == 1) {
                imageFiles.addAll(db.getImageFilesContainingDcSubject(keywords.get(0), false));
                setTitle(keywords.get(0));
            } else if (keywords.size() > 1) {
                setTitle(keywords);
                imageFiles.addAll(db.getImageFilesContainingAllDcSubjects(keywords));
            }
        }

        return imageFiles;
    }

    private void setTitle(List<String> keywords) {
        GUI.getAppFrame().setTitle(
                Bundle.getString(ShowThumbnailsContainingAllKeywords2.class,
                "ShowThumbnailsContainingAllKeywords2.AppFrame.Title.Keywords.Path", Util.keywordPathString(keywords)));
    }

    private void setTitle(String keyword) {
        GUI.getAppFrame().setTitle(
                Bundle.getString(ShowThumbnailsContainingAllKeywords2.class, "ShowThumbnailsContainingAllKeywords2.AppFrame.Title.Keyword", keyword));
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
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
