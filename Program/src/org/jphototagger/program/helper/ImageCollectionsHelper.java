/*
 * @(#)ImageCollectionsHelper.java    Created on 2010-09-15
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

package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.controller.imagecollection
    .ControllerDeleteFromImageCollection;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.ViewUtil;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsHelper {

    /**
     * Deletes selected files from an image collection.
     */
    public static void deleteSelectedFiles() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object selectedValue = getSelectedCollection();

                if (selectedValue != null) {
                    ThumbnailsPanel tnPanel = ViewUtil.getThumbnailsPanel();
                    List<File>      selectedFiles = tnPanel.getSelectedFiles();

                    if (ModifyImageCollections.deleteImagesFromCollection(
                            selectedValue.toString(), selectedFiles)) {
                        tnPanel.remove(selectedFiles);
                    }
                } else {
                    AppLogger.logWarning(
                        ControllerDeleteFromImageCollection.class,
                        "ImageCollectionsHelper.Error.NoCollectionSelected");
                }
            }
        });
    }

    /**
     * Returns the selected list item from the image collection list.
     * 
     * @return item or null if no item is selected
     */
    public static Object getSelectedCollection() {
        JList list = GUI.INSTANCE.getAppPanel().getListImageCollections();

        return list.getSelectedValue();
    }

    private ImageCollectionsHelper() {}
}
