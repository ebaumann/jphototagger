package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListImageCollections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

/**
 * Kontrolliert Aktion: Lösche Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListImageCollections}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public final class ControllerDeleteImageCollection implements ActionListener {

    private final PopupMenuListImageCollections actionPopup = PopupMenuListImageCollections.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final ListModelImageCollections model = (ListModelImageCollections) list.getModel();

    public ControllerDeleteImageCollection() {
        listen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteCollection();
    }

    private void deleteCollection() {
        String collectionName = actionPopup.getImageCollectionName();
        if (collectionName != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            if (manager.deleteImageCollection(collectionName)) {
                model.removeElement(collectionName);
            }
        }
    }

    private void listen() {
        actionPopup.addActionListenerDelete(this);
    }
}
