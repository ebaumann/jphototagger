package org.jphototagger.program.factory;

import java.util.List;

import javax.swing.JTable;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.ListItemTempSelectionRowSetter;
import org.jphototagger.lib.swing.util.TreeItemTempSelectionRowSetter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.module.directories.DirectoriesPopupMenu;
import org.jphototagger.program.module.favorites.FavoritesPopupMenu;
import org.jphototagger.program.module.imagecollections.ImageCollectionsPopupMenu;
import org.jphototagger.program.module.keywords.tree.KeywordsTreePopupMenu;
import org.jphototagger.program.module.search.SavedSearchesPopupMenu;
import org.jphototagger.program.module.exif.ExifTableCellRenderer;
import org.jphototagger.program.module.iptc.IptcTableCellRenderer;
import org.jphototagger.program.module.xmp.XmpTableCellRenderer;

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
                setMetadataTablesRenderers();
                setPopupMenuHighlighter();
                message = Bundle.getString(RendererFactory.class, "RendererFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private void setMetadataTablesRenderers() {
        AppPanel appPanel = GUI.getAppPanel();
        XmpTableCellRenderer rendererTableCellXmp = new XmpTableCellRenderer();
        List<JTable> xmpTables = appPanel.getXmpTables();

        for (JTable table : xmpTables) {
            table.setDefaultRenderer(Object.class, rendererTableCellXmp);
        }

        appPanel.getTableIptc().setDefaultRenderer(Object.class, new IptcTableCellRenderer());
        appPanel.getTableExif().setDefaultRenderer(Object.class, new ExifTableCellRenderer());
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
