package org.jphototagger.program.datatransfer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.io.ImageFileFilterer;
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
 * @author Elmar Baumann
 */
public final class TransferHandlerImageCollectionsList extends TransferHandler {
    private static final long serialVersionUID = 1375965940535469098L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(Flavor.THUMBNAILS_PANEL)
               && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
               && ((JList.DropLocation) support.getDropLocation()).getIndex()
                  >= 0;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        List<File> imageFiles = null;

        try {
            Transferable transferable = support.getTransferable();

            imageFiles = getImageFiles(
                (List<File>) transferable.getTransferData(
                    DataFlavor.javaFileListFlavor));
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerImageCollectionsList.class, ex);

            return false;
        }

        int listIndex =
            ((JList.DropLocation) support.getDropLocation()).getIndex();

        handleDroppedThumbnails(listIndex, imageFiles);

        return true;
    }

    private void handleDroppedThumbnails(int itemIndex, List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        if (itemIndex >= 0) {
            addToImageCollection(itemIndex, imageFiles);
        } else {
            createImageCollection(imageFiles);
        }
    }

    private void addToImageCollection(int itemIndex, List<File> imageFiles) {
        boolean added = ImageCollectionsHelper.addImagesToCollection(
                            getImageCollectionName(itemIndex), imageFiles);

        if (added) {
            GUI.refreshThumbnailsPanel();
        }
    }

    private void createImageCollection(final List<File> imageFiles) {
        String newCollectionName =
            ImageCollectionsHelper.insertImageCollection(imageFiles);

        if (newCollectionName != null) {
            ModelFactory.INSTANCE.getModel(
                ListModelImageCollections.class).addElement(newCollectionName);
        }
    }

    private String getImageCollectionName(int itemIndex) {
        JList list = GUI.getAppPanel().getListImageCollections();

        return list.getModel().getElementAt(itemIndex).toString();
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {}

    private List<File> getImageFiles(List<File> list) {
        List<File> imageFiles = new ArrayList<File>(list.size() / 2);

        for (File file : list) {
            if (ImageFileFilterer.isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }
}
