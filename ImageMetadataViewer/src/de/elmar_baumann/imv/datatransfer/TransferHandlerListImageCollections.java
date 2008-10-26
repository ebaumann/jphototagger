package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import java.util.List;
import javax.swing.JList;

/**
 * Adds images to an image collection (item hitted) or creates a new one (free
 * list area hitted) if thumbnails are dropped on the list with image 
 * collections.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/24
 */
public class TransferHandlerListImageCollections extends TransferHandlerListThumbnails {

    @Override
    protected void handleDroppedThumbnails(int itemIndex, List<String> filenames) {
        if (itemIndex >= 0) {
            addToImageCollection(itemIndex, filenames);
        } else {
            createImageCollection(filenames);
        }
    }

    private void addToImageCollection(int itemIndex, List<String> filenames) {
        ImageCollectionToDatabase db = new ImageCollectionToDatabase();
        boolean added =
            db.addImagesToCollection(getImageCollectionName(itemIndex), filenames);
        if (added) {
            refreshThumbnailsPanel();
        }
    }

    private void createImageCollection(List<String> filenames) {
        ImageCollectionToDatabase db = new ImageCollectionToDatabase();
        String newCollectionName = db.addImageCollection(filenames);
        if (newCollectionName != null) {
            ((ListModelImageCollections) Panels.getInstance().getAppPanel().
                getListImageCollections().getModel()).addElement(newCollectionName);
        }
    }

    private String getImageCollectionName(int itemIndex) {
        JList list = Panels.getInstance().getAppPanel().getListImageCollections();
        return list.getModel().getElementAt(itemIndex).toString();
    }

    private void refreshThumbnailsPanel() {
        Panels.getInstance().getAppPanel().getPanelThumbnails().refresh();
    }
}
