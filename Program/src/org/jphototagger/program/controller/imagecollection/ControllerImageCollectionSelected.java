/*
 * @(#)ControllerImageCollectionSelected.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author Elmar Baumann
 */
public final class ControllerImageCollectionSelected
        implements ListSelectionListener, RefreshListener {
    public ControllerImageCollectionSelected() {
        listen();
    }

    private void listen() {
        GUI.getImageCollectionsList().addListSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this,
                Content.IMAGE_COLLECTION);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (GUI.getImageCollectionsList().getSelectedIndex() >= 0) {
            showImageCollection(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getImageCollectionsList().getSelectedIndex() >= 0) {
            showImageCollection(evt.getSettings());
        }
    }

    private void showImageCollection(final ThumbnailsPanel.Settings settings) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Object selValue =
                    GUI.getImageCollectionsList().getSelectedValue();

                if (selValue != null) {
                    showImageCollection(selValue.toString(), settings);
                } else {
                    AppLogger.logWarning(
                        ControllerImageCollectionSelected.class,
                        "ControllerImageCollectionSelected.Error.SelectedValueIsNull");
                }

                setMetadataEditable();
            }
        }, "JPhotoTagger: Displaying selected image collection");

        thread.start();
    }

    private void showImageCollection(final String collectionName,
                                     final ThumbnailsPanel.Settings settings) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WaitDisplay.show();

                List<File> imageFiles =
                    DatabaseImageCollections.INSTANCE.getImageFilesOf(
                        collectionName);
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

                setTitle();
                tnPanel.setFileSortComparator(FileSort.NO_SORT.getComparator());
                tnPanel.setFiles(imageFiles, Content.IMAGE_COLLECTION);
                tnPanel.apply(settings);
                WaitDisplay.hide();
            }
            private void setTitle() {
                GUI.getAppFrame().setTitle(
                    JptBundle.INSTANCE.getString(
                        "ControllerImageCollectionSelected.AppFrame.Title.Collection",
                        collectionName));
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }
}
