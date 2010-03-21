/*
 * @(#)ControllerDeleteFromImageCollection.java    Created on 2008-00-10
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.imagecollection;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.jpt.helper.ModifyImageCollections;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.List;

import javax.swing.JList;
import javax.swing.SwingUtilities;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * image collection.
 *
 * @author  Elmar Baumann
 * @see     ControllerDeleteFiles
 */
public final class ControllerDeleteFromImageCollection
        implements ActionListener, KeyListener {
    private final AppPanel            appPanel        =
        GUI.INSTANCE.getAppPanel();
    private final JList               list            =
        appPanel.getListImageCollections();
    private final PopupMenuThumbnails popupMenu       =
        PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel     thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerDeleteFromImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemDeleteFromImageCollection().addActionListener(this);
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        if (thumbnailsPanel.getContent().equals(Content.IMAGE_COLLECTION)
                && (thumbnailsPanel.getSelectionCount() > 0)) {
            deleteSelectedFilesFromImageCollection();
        }
    }

    private void deleteSelectedFilesFromImageCollection() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object selectedValue = list.getSelectedValue();

                if (selectedValue != null) {
                    List<File> selectedFiles =
                        thumbnailsPanel.getSelectedFiles();

                    if (ModifyImageCollections.deleteImagesFromCollection(
                            selectedValue.toString(), selectedFiles)) {
                        thumbnailsPanel.remove(selectedFiles);
                    }
                } else {
                    AppLogger.logWarning(
                        ControllerDeleteFromImageCollection.class,
                        "ControllerDeleteFromImageCollection.Error.SelectedImageCollectionIsNull");
                }
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // ignore
    }
}
