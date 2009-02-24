package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
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
public final class ControllerRenameImageCollection implements ActionListener {

    private final PopupMenuListImageCollections popupMenu = PopupMenuListImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final ListModelImageCollections model = (ListModelImageCollections) list.getModel();

    public ControllerRenameImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerRename(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameImageCollection();
    }

    private void renameImageCollection() {
        String oldName = popupMenu.getImageCollectionName();
        if (oldName != null) {
            String newName = ImageCollectionDatabaseUtils.renameImageCollection(oldName);
            if (newName != null) {
                model.rename(oldName, newName);
            }
        } else {
            AppLog.logWarning(ControllerRenameImageCollection.class, "ControllerRenameImageCollection.ErrorMessage.NameIsNull");
        }
    }
}
