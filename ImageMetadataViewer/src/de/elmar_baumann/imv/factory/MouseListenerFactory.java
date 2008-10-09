package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.controller.tasks.MouseListenerProgressBarScheduledTasks;
import de.elmar_baumann.imv.event.listener.ListFavoriteDirectoriesMouseListener;
import de.elmar_baumann.imv.event.listener.TreeDirectoriesMouseListener;
import de.elmar_baumann.imv.event.listener.TreeImageCollectionsMouseListener;
import de.elmar_baumann.imv.event.listener.TreeSavedSearchesMouseListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;

/**
 * Erzeugt und verbindet MouseListener.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class MouseListenerFactory {

    private static MouseListenerFactory instance = new MouseListenerFactory();

    static MouseListenerFactory getInstance() {
        return instance;
    }

    private MouseListenerFactory() {
        createMouseListener();
    }

    private void createMouseListener() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        appPanel.getTreeDirectories().addMouseListener(new TreeDirectoriesMouseListener());
        appPanel.getTreeSavedSearches().addMouseListener(new TreeSavedSearchesMouseListener());
        appPanel.getTreeImageCollections().addMouseListener(new TreeImageCollectionsMouseListener());
        appPanel.getListFavoriteDirectories().addMouseListener(new ListFavoriteDirectoriesMouseListener());
        appPanel.getProgressBarScheduledTasks().addMouseListener(new MouseListenerProgressBarScheduledTasks());
    }
}
