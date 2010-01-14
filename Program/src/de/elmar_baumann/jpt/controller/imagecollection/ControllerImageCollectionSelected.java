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
package de.elmar_baumann.jpt.controller.imagecollection;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.event.RefreshEvent;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerImageCollectionSelected implements
        ListSelectionListener, RefreshListener {

    private final AppPanel           appPanel        = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel    thumbnailsPanel = appPanel.getPanelThumbnails();
    private final EditMetadataPanels editPanels      = appPanel.getEditMetadataPanels();
    private final JList              list            = appPanel.getListImageCollections();

    public ControllerImageCollectionSelected() {
        listen();
    }

    private void listen() {
        list.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.IMAGE_COLLECTION);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (list.getSelectedIndex() >= 0) {
            showImageCollection(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (list.getSelectedIndex() >= 0) {
            showImageCollection(evt.getSettings());
        }
    }

    private void showImageCollection(final ThumbnailsPanel.Settings settings) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Object selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    showImageCollection(selectedValue.toString(), settings);
                } else {
                    AppLog.logWarning(ControllerImageCollectionSelected.class,
                            "ControllerImageCollectionSelected.Error.SelectedValueIsNull");
                }
                setMetadataEditable();
            }
        });
        thread.setName("Image collection selected @ " + getClass().getSimpleName());
        thread.start();
    }

    private void showImageCollection(final String collectionName, final ThumbnailsPanel.Settings settings) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                List<String> filenames = DatabaseImageCollections.INSTANCE
                        .getFilenamesOf(collectionName);
                setTitle();
                thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.IMAGE_COLLECTION);
                thumbnailsPanel.apply(settings);
            }

            private void setTitle() {
                GUI.INSTANCE.getAppFrame().setTitle(
                        Bundle.getString("AppFrame.Title.Collection", collectionName));
            }
        });
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
