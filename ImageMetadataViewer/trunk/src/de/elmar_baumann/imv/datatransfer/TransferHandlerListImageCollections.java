package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.ModifyImageCollections;
import de.elmar_baumann.imv.io.ImageUtil;
import de.elmar_baumann.lib.io.FileUtil;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-24
 */
public final class TransferHandlerListImageCollections extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(Flavors.THUMBNAILS_PANEL_FLAVOR)
                && transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) return false;
        List<File> files = null;
        try {
            Transferable transferable = transferSupport.getTransferable();
            files = getImageFiles((List<File>) transferable.getTransferData(
                    DataFlavor.javaFileListFlavor));
        } catch (Exception ex) {
            AppLog.logSevere(TransferHandlerListImageCollections.class, ex);
            return false;
        }
        int listIndex =
                ((JList.DropLocation) transferSupport.getDropLocation()).
                getIndex();
        handleDroppedThumbnails(listIndex, FileUtil.getAsFilenames(files));
        return true;
    }

    protected void handleDroppedThumbnails(int itemIndex, List<String> filenames) {
        if (itemIndex >= 0) {
            addToImageCollection(itemIndex, filenames);
        } else {
            createImageCollection(filenames);
        }
    }

    private void addToImageCollection(int itemIndex, List<String> filenames) {
        boolean added =
                ModifyImageCollections.addImagesToCollection(
                getImageCollectionName(itemIndex), filenames);
        if (added) {
            refreshThumbnailsPanel();
        }
    }

    private void createImageCollection(final List<String> filenames) {
        String newCollectionName =
                ModifyImageCollections.insertImageCollection(filenames);
        if (newCollectionName != null) {
            ((ListModelImageCollections) GUI.INSTANCE.getAppPanel().
                    getListImageCollections().getModel()).addElement(
                    newCollectionName);
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
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

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
