package org.jphototagger.program.module.keywords.list;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Displays in the {@code ThumbnailsPanel} thumbnails of images containing all specific keywords.
 *
 * @author Elmar Baumann
 */
public final class ShowThumbnailsContainingAllKeywords2 implements Runnable {

    private final List<List<String>> keywordLists;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final ThumbnailsPanelSettings settings;

    /**
     * Creates a new instance of this class.
     *
     * @param keywordLists all keywords a image must have to be displayed
     */
    public ShowThumbnailsContainingAllKeywords2(List<List<String>> keywordLists, ThumbnailsPanelSettings settings) {
        if (keywordLists == null) {
            throw new NullPointerException("keywordLists == null");
        }
        if (settings == null) {
            throw new NullPointerException("settings == null");
        }
        this.keywordLists = deepCopy(keywordLists);
        this.settings = settings;
    }

    @Override
    public void run() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                setFilesToThumbnailsPanel();
                waitDisplayer.hide();
            }
        });
    }

    private void setFilesToThumbnailsPanel() {
        List<File> imageFiles = getImageFilesOfKeywords();
        GUI.getThumbnailsPanel().setFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_A_KEYWORD);
        GUI.getThumbnailsPanel().applyThumbnailsPanelSettings(settings);
    }

    private List<File> getImageFilesOfKeywords() {
        List<File> imageFiles = new ArrayList<>();
        for (List<String> keywords : keywordLists) {
            // Faster when using 2 different DB queries if only 1 keyword is
            // selected
            if (keywords.size() == 1) {
                imageFiles.addAll(repo.findImageFilesContainingDcSubject(keywords.get(0), false));
                setTitle(keywords.get(0));
            } else if (keywords.size() > 1) {
                setTitle(keywords);
                imageFiles.addAll(repo.findImageFilesContainingAllDcSubjects(keywords));
            }
        }
        return imageFiles;
    }

    private void setTitle(List<String> keywords) {
        String keywordPathString = KeywordsListControllerUtil.keywordPathString(keywords);
        String title = Bundle.getString(ShowThumbnailsContainingAllKeywords2.class, "ShowThumbnailsContainingAllKeywords2.AppFrame.Title.Keywords.Path",
                keywordPathString);
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(title);
    }

    private void setTitle(String keyword) {
        String title = Bundle.getString(ShowThumbnailsContainingAllKeywords2.class, "ShowThumbnailsContainingAllKeywords2.AppFrame.Title.Keyword",
                keyword);
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(title);
    }

    private List<List<String>> deepCopy(List<List<String>> kwLists) {
        List<List<String>> copy = new ArrayList<>(kwLists.size());
        for (List<String> kwList : kwLists) {
            copy.add(new ArrayList<>(kwList));
        }
        return copy;
    }
}
