package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.model.TreeModelImageCollections;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeImageCollections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;

/**
 * Kontrolliert Aktion: Lösche Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeImageCollections}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public class ControllerDeleteImageCollection extends Controller
    implements ActionListener {

    private PopupMenuTreeImageCollections actionPopup = PopupMenuTreeImageCollections.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree tree = appPanel.getTreeImageCollections();
    private TreeModelImageCollections model = (TreeModelImageCollections) tree.getModel();

    public ControllerDeleteImageCollection() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        actionPopup.addActionListenerDelete(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            deleteCollection();
        }
    }

    private void deleteCollection() {
        String collectionName = actionPopup.getImageCollectionName();
        if (collectionName != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            if (manager.deleteImageCollection(collectionName)) {
                model.removeNode(collectionName);
                tree.setSelectionRow(0);
            }
        }
    }
}
