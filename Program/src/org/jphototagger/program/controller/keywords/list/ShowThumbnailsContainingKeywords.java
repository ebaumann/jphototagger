package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Displays in the {@link ThumbnailsPanel} thumbnails with specific keywords.
 *
 * @author Elmar Baumann
 */
public final class ShowThumbnailsContainingKeywords implements Runnable {
    private final List<String> keywords;
    private final ThumbnailsPanel.Settings tnPanelSettings;

    /**
     * Creates a new instance of this class.
     *
     * @param keywords one of that keywords a image must have to be displayed
     * @param settings
     */
    public ShowThumbnailsContainingKeywords(List<String> keywords, ThumbnailsPanel.Settings settings) {
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
        List<File> imageFiles = new ArrayList<File>(getImageFilesOfSelectedKeywords());

        if (imageFiles != null) {
            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            ControllerSortThumbnails.setLastSort();
            tnPanel.setFiles(imageFiles, Content.KEYWORD);
            tnPanel.apply(tnPanelSettings);
        }
    }

    private Set<File> getImageFilesOfSelectedKeywords() {
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

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
        GUI.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString(
                "ShowThumbnailsContainingKeywords.AppFrame.Title.Keywords.Path", Util.keywordPathString(keywords)));
    }

    private void setTitle(String keyword) {
        GUI.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString("ShowThumbnailsContainingKeywords.AppFrame.Title.Keyword", keyword));
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }
}
