/*
 * @(#)TransferHandlerImageCollectionsList.java    Created on 2008-10-24
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

package org.jphototagger.program.datatransfer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ModifyImageCollections;
import org.jphototagger.program.io.ImageUtil;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Adds images to an image collection (item hitted) or creates a new one (free
 * list area hitted) if thumbnails are dropped on the list with image
 * collections.
 *
 * @author  Elmar Baumann
 */
public final class TransferHandlerImageCollectionsList extends TransferHandler {
    private static final long serialVersionUID = 1375965940535469098L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(Flavor.THUMBNAILS_PANEL)
               && transferSupport
                   .isDataFlavorSupported(DataFlavor
                       .javaFileListFlavor) && ((JList
                           .DropLocation) transferSupport.getDropLocation())
                               .getIndex() >= 0;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }

        List<File> imageFiles = null;

        try {
            Transferable transferable = transferSupport.getTransferable();

            imageFiles = getImageFiles(
                (List<File>) transferable.getTransferData(
                    DataFlavor.javaFileListFlavor));
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerImageCollectionsList.class, ex);

            return false;
        }

        int listIndex =
            ((JList.DropLocation) transferSupport.getDropLocation()).getIndex();

        handleDroppedThumbnails(listIndex, imageFiles);

        return true;
    }

    protected void handleDroppedThumbnails(int itemIndex,
            List<File> imageFiles) {
        if (itemIndex >= 0) {
            addToImageCollection(itemIndex, imageFiles);
        } else {
            createImageCollection(imageFiles);
        }
    }

    private void addToImageCollection(int itemIndex, List<File> imageFiles) {
        boolean added = ModifyImageCollections.addImagesToCollection(
                            getImageCollectionName(itemIndex), imageFiles);

        if (added) {
            refreshThumbnailsPanel();
        }
    }

    private void createImageCollection(final List<File> imageFiles) {
        String newCollectionName =
            ModifyImageCollections.insertImageCollection(imageFiles);

        if (newCollectionName != null) {
            ModelFactory.INSTANCE.getModel(
                ListModelImageCollections.class).addElement(newCollectionName);
        }
    }

    private String getImageCollectionName(int itemIndex) {
        JList list = GUI.INSTANCE.getAppPanel().getListImageCollections();

        return list.getModel().getElementAt(itemIndex).toString();
    }

    private void refreshThumbnailsPanel() {
        GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {}

    private List<File> getImageFiles(List<File> list) {
        List<File> imageFiles = new ArrayList<File>(list.size() / 2);

        for (File file : list) {
            if (ImageUtil.isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }
}
