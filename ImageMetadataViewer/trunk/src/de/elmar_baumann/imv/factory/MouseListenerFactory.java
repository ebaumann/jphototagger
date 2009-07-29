package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.event.listener.impl.MouseListenerProgressBarScheduledTasks;
import de.elmar_baumann.imv.event.listener.impl.MouseListenerDirectories;
import de.elmar_baumann.imv.event.listener.impl.MouseListenerImageCollections;
import de.elmar_baumann.imv.event.listener.impl.MouseListenerSavedSearches;
import de.elmar_baumann.imv.event.listener.impl.MouseListenerFavorites;
import de.elmar_baumann.imv.event.listener.impl.MouseListenerHierarchicalKeywords;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;

/**
 * Erzeugt und verbindet MouseListener.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class MouseListenerFactory {

    static final MouseListenerFactory INSTANCE = new MouseListenerFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(MouseListenerFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            appPanel.getTreeDirectories().addMouseListener(
                    new MouseListenerDirectories());
            appPanel.getListSavedSearches().addMouseListener(
                    new MouseListenerSavedSearches());
            appPanel.getListImageCollections().addMouseListener(
                    new MouseListenerImageCollections());
            appPanel.getTreeFavorites().addMouseListener(
                    new MouseListenerFavorites());
            appPanel.getProgressBarScheduledTasks().addMouseListener(
                    new MouseListenerProgressBarScheduledTasks());
            MouseListenerHierarchicalKeywords mouseListenerHierarchicalKeywords =
                    new MouseListenerHierarchicalKeywords();
            appPanel.getTreeHierarchicalKeywords().addMouseListener(
                    mouseListenerHierarchicalKeywords);
            HierarchicalKeywordsDialog.INSTANCE.getPanel().getTree().
                    addMouseListener(mouseListenerHierarchicalKeywords);
        }
    }
}
