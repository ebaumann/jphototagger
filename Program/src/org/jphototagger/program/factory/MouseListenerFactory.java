package org.jphototagger.program.factory;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListItemTempSelectionRowSetter;
import org.jphototagger.lib.componentutil.TreeItemTempSelectionRowSetter;
import org.jphototagger.lib.event.listener.TableButtonMouseListener;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.directories.DirectoriesMouseListener;
import org.jphototagger.program.module.favorites.FavoritesMouseListener;
import org.jphototagger.program.module.imagecollections.ImageCollectionsMouseListener;
import org.jphototagger.program.module.keywords.list.KeywordsListMouseListener;
import org.jphototagger.program.module.keywords.tree.KeywordsTreeMouseListener;
import org.jphototagger.program.module.metadatatemplates.MetadataTemplatesMouseListener;
import org.jphototagger.program.module.search.SavedSearchesMouseListener;
import org.jphototagger.program.event.listener.TreeExpandMouseListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.module.keywords.list.KeywordsListPopupMenu;
import org.jphototagger.program.module.metadatatemplates.MetadataTemplatesPopupMenu;

/**
 * Erzeugt und verbindet MouseListener.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerFactory {

    static final MouseListenerFactory INSTANCE = new MouseListenerFactory();
    private final Support support = new Support();
    private volatile boolean init;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(MouseListenerFactory.class, "MouseListenerFactory.Init.Start");
                Support.setStatusbarInfo(message);
                addMouseListeners();
                message = Bundle.getString(MouseListenerFactory.class, "MouseListenerFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private void addMouseListeners() {
        AppPanel appPanel = GUI.getAppPanel();
        TreeExpandMouseListener listenerTreeExpand = new TreeExpandMouseListener();
        KeywordsTreeMouseListener listenerKeywordsTree = new KeywordsTreeMouseListener();

        appPanel.getTableExif().addMouseListener(new TableButtonMouseListener(appPanel.getTableExif()));
        appPanel.getTreeDirectories().addMouseListener(new DirectoriesMouseListener());
        appPanel.getListSavedSearches().addMouseListener(new SavedSearchesMouseListener());
        appPanel.getListEditKeywords().addMouseListener(new KeywordsListMouseListener());
        appPanel.getListImageCollections().addMouseListener(new ImageCollectionsMouseListener());
        appPanel.getTreeFavorites().addMouseListener(new FavoritesMouseListener());
        appPanel.getTreeTimeline().addMouseListener(listenerTreeExpand);
        appPanel.getTreeSelKeywords().addMouseListener(listenerTreeExpand);
        appPanel.getTreeEditKeywords().addMouseListener(listenerKeywordsTree);
        InputHelperDialog.INSTANCE.getPanelKeywords().getTree().addMouseListener(listenerKeywordsTree);
        InputHelperDialog.INSTANCE.getPanelKeywords().getList().addMouseListener(new KeywordsListMouseListener());
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().addMouseListener(new MetadataTemplatesMouseListener());
        support.add(new TreeItemTempSelectionRowSetter(appPanel.getTreeMiscMetadata(), listenerTreeExpand.getPopupMenu()));
        support.add(new TreeItemTempSelectionRowSetter(appPanel.getTreeTimeline(), listenerTreeExpand.getPopupMenu()));
        support.add(new TreeItemTempSelectionRowSetter(appPanel.getTreeSelKeywords(), listenerTreeExpand.getPopupMenu()));
        support.add(new ListItemTempSelectionRowSetter(appPanel.getListEditKeywords(), KeywordsListPopupMenu.INSTANCE));
        support.add(new ListItemTempSelectionRowSetter(InputHelperDialog.INSTANCE.getPanelKeywords().getList(), KeywordsListPopupMenu.INSTANCE));
        support.add(new ListItemTempSelectionRowSetter(InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList(), MetadataTemplatesPopupMenu.INSTANCE));
    }
}
