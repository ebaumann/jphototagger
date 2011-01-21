package org.jphototagger.program.resource;

import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTree;

/**
 * Provides access to GUI elements.
 *
 * @author Elmar Baumann
 */
public final class GUI {
    private static AppPanel appPanel;
    private static AppFrame appFrame;

    public static void setAppPanel(AppPanel panel) {
        if (panel == null) {
            throw new NullPointerException("panel == null");
        }

        appPanel = panel;
    }

    public static void setAppFrame(AppFrame frame) {
        if (frame == null) {
            throw new NullPointerException("frame == null");
        }

        appFrame = frame;
    }

    public static AppPanel getAppPanel() {
        return appPanel;
    }

    public static AppFrame getAppFrame() {
        return appFrame;
    }

    public static JTextArea getSearchTextArea() {
        return appPanel.getTextAreaSearch();
    }

    public static EditMetadataPanels getEditPanel() {
        return appPanel.getEditMetadataPanels();
    }

    public static ThumbnailsPanel getThumbnailsPanel() {
        return appPanel.getPanelThumbnails();
    }

    /**
     * Returns all in the thumbnails panel selected images files.
     *
     * @return selected files
     */
    public static List<File> getSelectedImageFiles() {
        return appPanel.getPanelThumbnails().getSelectedFiles();
    }

    /**
     * Calls {@link ThumbnailsPanel#refresh()}.
     */
    public static void refreshThumbnailsPanel() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                getThumbnailsPanel().refresh();
            }
        });
    }

    public static JList getSelKeywordsList() {
        return appPanel.getListSelKeywords();
    }

    public static JList getEditKeywordsList() {
        return appPanel.getPanelEditKeywords().getList();
    }

    public static JList getInputHelperKeywordsList() {
        return InputHelperDialog.INSTANCE.getPanelKeywords().getList();
    }

    public static JList getNoMetadataList() {
        return appPanel.getListNoMetadata();
    }

    public static JList getSavedSearchesList() {
        return appPanel.getListSavedSearches();
    }

    public static JList getImageCollectionsList() {
        return appPanel.getListImageCollections();
    }

    public static JTree getSelKeywordsTree() {
        return appPanel.getTreeSelKeywords();
    }

    public static JTree getEditKeywordsTree() {
        return appPanel.getPanelEditKeywords().getTree();
    }

    public static JTree getInputHelperKeywordsTree() {
        return InputHelperDialog.INSTANCE.getPanelKeywords().getTree();
    }

    public static JTree getDirectoriesTree() {
        return appPanel.getTreeDirectories();
    }

    public static JTree getFavoritesTree() {
        return appPanel.getTreeFavorites();
    }

    public static JTree getMiscMetadataTree() {
        return appPanel.getTreeMiscMetadata();
    }

    public static JTree getTimelineTree() {
        return appPanel.getTreeTimeline();
    }

    private GUI() {}
}
