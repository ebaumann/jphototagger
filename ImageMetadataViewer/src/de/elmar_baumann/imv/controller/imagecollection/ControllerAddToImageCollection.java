package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.view.dialogs.ImageCollectionsDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public class ControllerAddToImageCollection extends Controller
    implements ActionListener {

    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();

    public ControllerAddToImageCollection() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionListenerAddToImageCollection(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            addToImageCollection();
        }
    }

    private void addToImageCollection() {
        String collectionName = selectCollectionName();
        if (collectionName != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            manager.addImagesToCollection(collectionName,
                FileUtil.getAsFilenames(popup.getThumbnailsPanel().getSelectedFiles()));
        }
    }

    private String selectCollectionName() {
        ImageCollectionsDialog dialog = new ImageCollectionsDialog(null);
        dialog.setVisible(true);
        return dialog.getSelectedCollection();
    }
}
