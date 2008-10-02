package de.elmar_baumann.imagemetadataviewer.controller.imagecollection;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imagemetadataviewer.model.TreeModelImageCollections;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert Aktion: Erzeuge eine Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerCreateImageCollection extends Controller
    implements ActionListener {

    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private TreeModelImageCollections model = (TreeModelImageCollections) appPanel.getTreeImageCollections().getModel();

    public ControllerCreateImageCollection() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionListenerCreateImageCollection(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            createCollection();
        }
    }

    private void createCollection() {
        ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
        String collectionName = manager.addImageCollection(
            popup.getThumbnailsPanel().getSelectedFilenames());
        if (collectionName != null) {
            model.addNode(collectionName);
        }
    }
}
