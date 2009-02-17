package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.event.listener.MouseListenerProgressBarScheduledTasks;
import de.elmar_baumann.imv.event.listener.ListFavoriteDirectoriesMouseListener;
import de.elmar_baumann.imv.event.listener.TreeDirectoriesMouseListener;
import de.elmar_baumann.imv.event.listener.ListImageCollectionsMouseListener;
import de.elmar_baumann.imv.event.listener.ListSavedSearchesMouseListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;

/**
 * Erzeugt und verbindet MouseListener.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class MouseListenerFactory {

    static final MouseListenerFactory INSTANCE = new MouseListenerFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(MouseListenerFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = Panels.getInstance().getAppPanel();
            appPanel.getTreeDirectories().addMouseListener(new TreeDirectoriesMouseListener());
            appPanel.getListSavedSearches().addMouseListener(new ListSavedSearchesMouseListener());
            appPanel.getListImageCollections().addMouseListener(new ListImageCollectionsMouseListener());
            appPanel.getListFavoriteDirectories().addMouseListener(new ListFavoriteDirectoriesMouseListener());
            appPanel.getProgressBarScheduledTasks().addMouseListener(new MouseListenerProgressBarScheduledTasks());
        }
    }
}
