package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListImageCollections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

/**
 * Kontrolliert die Aktion: Benenne eine Bildsammlung um, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuListImageCollections}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public class ControllerRenameImageCollection extends Controller
    implements ActionListener {

    private PopupMenuListImageCollections actionPopup = PopupMenuListImageCollections.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList list = appPanel.getListImageCollections();
    private ListModelImageCollections model = (ListModelImageCollections) list.getModel();

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
                model.rename(oldName, newName);
            }
        }
    }
}
