package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Kontrolliert die Aktion: Lösche Bilder aus einer Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public class ControllerDeleteFromImageCollection extends Controller
    implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree tree = appPanel.getTreeImageCollections();
    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();

    public ControllerDeleteFromImageCollection() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionListenerDeleteFromImageCollection(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            deleteFromImageCollection();
        }
    }

    private void deleteFromImageCollection() {
        String collectionName = getCollectionName();
        if (collectionName != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            manager.deleteImagesFromCollection(collectionName,
                popup.getThumbnailsPanel().getSelectedFilenames());
            TreePath selectionPath = tree.getSelectionPath();
            if (selectionPath != null) {
                tree.clearSelection();
                tree.setSelectionPath(selectionPath);
            }
        }
    }

    private String getCollectionName() {
        String name = null;
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            Object item = path.getLastPathComponent();
            if (item != null) {
                name = item.toString();
            }
        }
        return name;
    }
}
