/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerDirectories;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerImageCollections;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerSavedSearches;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerFavorites;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerHierarchicalKeywords;
import de.elmar_baumann.jpt.event.listener.impl.MouseListenerProgressBarScheduledTasks;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ProgressBarScheduledTasks;
import javax.swing.JProgressBar;

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
            addMouseListenerProgressBarScheduledTasks();
            MouseListenerHierarchicalKeywords mouseListenerHierarchicalKeywords =
                    new MouseListenerHierarchicalKeywords();
            appPanel.getTreeHierarchicalKeywords().addMouseListener(
                    mouseListenerHierarchicalKeywords);
            InputHelperDialog.INSTANCE.getPanelKeywords().getTree().
                    addMouseListener(mouseListenerHierarchicalKeywords);
        }
    }

    private void addMouseListenerProgressBarScheduledTasks() {
        Object owner = MouseListenerFactory.class;
        JProgressBar progressBar =
                ProgressBarScheduledTasks.INSTANCE.getResource(owner);
        if (progressBar == null) {
            AppLog.logWarning(MouseListenerFactory.class,
                    "MouseListenerFactory.Warning.ProgressBarScheduledTasksNoMousListener"); // NOI18N
        } else {
            progressBar.addMouseListener(
                    new MouseListenerProgressBarScheduledTasks());
            ProgressBarScheduledTasks.INSTANCE.releaseResource(owner);
        }
    }
}
