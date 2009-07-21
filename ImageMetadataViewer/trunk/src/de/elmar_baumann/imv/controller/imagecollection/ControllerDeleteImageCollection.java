package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.helper.ModifyImageCollections;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Aktion: Lösche Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections}.
 *
 * Also listens to the {@link JList}'s key events and deletes the selected image
 * collection when the keys <code>Ctrl+N</code> were pressed.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-00-10
 */
public final class ControllerDeleteImageCollection
        implements ActionListener, KeyListener {

    private final PopupMenuImageCollections actionPopup =
            PopupMenuImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();

    public ControllerDeleteImageCollection() {
        listen();
    }

    private void listen() {
        actionPopup.getItemDelete().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();
            if (value instanceof String) {
                deleteCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteCollection(actionPopup.getImageCollectionName());
    }

    private void deleteCollection(final String collectionName) {
        if (!ListModelImageCollections.checkIsNotSpecialCollection(collectionName,
                "ControllerDeleteImageCollection.Error.SpecialCollection"))
            return;
        if (collectionName != null) {
            if (ModifyImageCollections.deleteImageCollection(
                    collectionName)) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        ListModel model = list.getModel();
                        if (model instanceof ListModelImageCollections) {
                            ((ListModelImageCollections) model).removeElement(
                                    collectionName);
                        }
                    }
                });
            }
        } else {
            AppLog.logWarning(ControllerDeleteImageCollection.class,
                    Bundle.getString(
                    "ControllerDeleteImageCollection.Error.CollectionNameIsNull")); // NOI18N
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
