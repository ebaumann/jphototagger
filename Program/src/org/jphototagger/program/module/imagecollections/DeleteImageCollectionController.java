package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class DeleteImageCollectionController implements ActionListener, KeyListener {

    private static final Logger LOGGER = Logger.getLogger(DeleteImageCollectionController.class.getName());

    public DeleteImageCollectionController() {
        listen();
    }

    private void listen() {
        ImageCollectionsPopupMenu.INSTANCE.getItemDelete().addActionListener(this);
        GUI.getImageCollectionsList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getImageCollectionsList();

        if ((evt.getKeyCode() == KeyEvent.VK_DELETE) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof String) {
                deleteCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        deleteCollection(ListUtil.getItemString(GUI.getImageCollectionsList(),
                ImageCollectionsPopupMenu.INSTANCE.getItemIndex()));
    }

    private void deleteCollection(final String collectionName) {
        String errorMessage = Bundle.getString(DeleteImageCollectionController.class, "DeleteImageCollectionController.Error.SpecialCollection",
                ImageCollection.getLocalizedName(collectionName));
        if (!ImageCollectionsUtil.checkIsNotSpecialCollection(collectionName, errorMessage)) {
            return;
        }

        if (collectionName != null) {
            ImageCollectionsUtil.deleteImageCollection(collectionName);
        } else {
            LOGGER.log(Level.WARNING, "Delete photo album: Couldn't find the selected photo album in the repository!");
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }
}
