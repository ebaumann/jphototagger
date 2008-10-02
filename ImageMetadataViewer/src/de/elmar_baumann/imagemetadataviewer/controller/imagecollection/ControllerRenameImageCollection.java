package de.elmar_baumann.imagemetadataviewer.controller.imagecollection;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imagemetadataviewer.model.TreeModelImageCollections;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeImageCollections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;

/**
 * Kontrolliert die Aktion: Benenne eine Bildsammlung um, ausgelöst von
 * {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuTreeImageCollections}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public class ControllerRenameImageCollection extends Controller
    implements ActionListener {

    private PopupMenuTreeImageCollections actionPopup = PopupMenuTreeImageCollections.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTree tree = appPanel.getTreeImageCollections();
    private TreeModelImageCollections model = (TreeModelImageCollections) tree.getModel();

    public ControllerRenameImageCollection() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        actionPopup.addActionListenerRename(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            renameImageCollection();
        }
    }

    private void renameImageCollection() {
        String oldName = actionPopup.getImageCollectionName();
        if (oldName != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            String newName = manager.renameImageCollection(oldName);
            if (newName != null) {
                model.renameNode(oldName, newName);
            }
        }
    }
}
