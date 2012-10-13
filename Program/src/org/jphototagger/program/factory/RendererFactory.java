package org.jphototagger.program.factory;



import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.ListItemTempSelectionRowSetter;
import org.jphototagger.lib.swing.util.TreeItemTempSelectionRowSetter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.directories.DirectoriesPopupMenu;
import org.jphototagger.program.module.favorites.FavoritesPopupMenu;
import org.jphototagger.program.module.imagecollections.ImageCollectionsPopupMenu;
import org.jphototagger.program.module.keywords.tree.KeywordsTreePopupMenu;
import org.jphototagger.program.module.search.SavedSearchesPopupMenu;
import org.jphototagger.program.resource.GUI;

/**
 * Erzeugt Renderer und verknüpft sie mit den GUI-Elementen.
 *
 * @author Elmar Baumann
 */
public final class RendererFactory {

    static final RendererFactory INSTANCE = new RendererFactory();
    private final Support support = new Support();
    private volatile boolean init;

    synchronized void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(RendererFactory.class, "RendererFactory.Init.Start");
                Support.setStatusbarInfo(message);
                setPopupMenuHighlighter();
                message = Bundle.getString(RendererFactory.class, "RendererFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private void setPopupMenuHighlighter() {
        AppPanel appPanel = GUI.getAppPanel();

        support.add(new TreeItemTempSelectionRowSetter(appPanel.getTreeFavorites(), FavoritesPopupMenu.INSTANCE));
        support.add(new TreeItemTempSelectionRowSetter(appPanel.getTreeDirectories(), DirectoriesPopupMenu.INSTANCE));
        support.add(new TreeItemTempSelectionRowSetter(appPanel.getTreeEditKeywords(), KeywordsTreePopupMenu.INSTANCE));
        support.add(new TreeItemTempSelectionRowSetter(InputHelperDialog.INSTANCE.getPanelKeywords().getTree(),
                KeywordsTreePopupMenu.INSTANCE));
        support.add(new ListItemTempSelectionRowSetter(appPanel.getListImageCollections(),
                ImageCollectionsPopupMenu.INSTANCE));
        support.add(new ListItemTempSelectionRowSetter(appPanel.getListSavedSearches(),
                SavedSearchesPopupMenu.INSTANCE));
    }
}
