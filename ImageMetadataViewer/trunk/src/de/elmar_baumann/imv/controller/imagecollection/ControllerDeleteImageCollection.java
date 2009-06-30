package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Aktion: Lösche Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public final class ControllerDeleteImageCollection implements ActionListener {

    private final PopupMenuImageCollections actionPopup =
            PopupMenuImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final ListModelImageCollections model =
            (ListModelImageCollections) list.getModel();

    public ControllerDeleteImageCollection() {
        listen();
    }

    private void listen() {
        actionPopup.getItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteCollection();
    }

    private void deleteCollection() {
        final String collectionName = actionPopup.getImageCollectionName();
        if (collectionName != null) {
            if (ImageCollectionDatabaseUtils.deleteImageCollection(
                    collectionName)) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        model.removeElement(collectionName);
                    }
                });
            }
        } else {
            AppLog.logWarning(ControllerDeleteImageCollection.class,
                    Bundle.getString(
                    "ControllerDeleteImageCollection.ErrorMessage.CollectionNameIsNull"));
        }
    }
}
