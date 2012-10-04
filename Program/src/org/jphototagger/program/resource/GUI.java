package org.jphototagger.program.resource;

import java.io.File;
import java.util.List;
import javax.swing.JTree;
import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.ui.AppFrame;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanelProvider;
import org.openide.util.Lookup;

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

    public static ThumbnailsPanel getThumbnailsPanel() {
        ThumbnailsPanelProvider provider = Lookup.getDefault().lookup(ThumbnailsPanelProvider.class);
        return provider.getThumbnailsPanel();
    }

    public static List<File> getSelectedImageFiles() {
        ThumbnailsPanelProvider provider = Lookup.getDefault().lookup(ThumbnailsPanelProvider.class);
        return provider.getThumbnailsPanel().getSelectedFiles();
    }

    /**
     * Calls {@code ThumbnailsPanel#refresh()}.
     */
    public static void refreshThumbnailsPanel() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                getThumbnailsPanel().refresh();
            }
        });
    }

    public static JXList getSelKeywordsList() {
        return appPanel.getListSelKeywords();
    }

    public static JXList getEditKeywordsList() {
        return appPanel.getPanelEditKeywords().getList();
    }

    public static JXList getInputHelperKeywordsList() {
        return InputHelperDialog.INSTANCE.getPanelKeywords().getList();
    }

    public static JXList getSavedSearchesList() {
        return appPanel.getListSavedSearches();
    }

    public static JXList getImageCollectionsList() {
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

    private GUI() {
    }
}
