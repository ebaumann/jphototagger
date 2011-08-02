package org.jphototagger.program.controller.imagecollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerDeleteImageCollection implements ActionListener, KeyListener {

    private static final Logger LOGGER = Logger.getLogger(ControllerDeleteImageCollection.class.getName());

    public ControllerDeleteImageCollection() {
        listen();
    }

    private void listen() {
        PopupMenuImageCollections.INSTANCE.getItemDelete().addActionListener(this);
        GUI.getImageCollectionsList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getImageCollectionsList();

        if ((evt.getKeyCode() == KeyEvent.VK_DELETE) &&!list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof String) {
                deleteCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        deleteCollection(ListUtil.getItemString(GUI.getImageCollectionsList(),
                PopupMenuImageCollections.INSTANCE.getItemIndex()));
    }

    private void deleteCollection(final String collectionName) {
        String errorMessage = Bundle.getString(ControllerDeleteImageCollection.class, "ControllerDeleteImageCollection.Error.SpecialCollection", collectionName);
        if (!ListModelImageCollections.checkIsNotSpecialCollection(collectionName, errorMessage)) {
            return;
        }

        if (collectionName != null) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    if (ImageCollectionsHelper.deleteImageCollection(collectionName)) {
                        ModelFactory.INSTANCE.getModel(ListModelImageCollections.class).removeElement(collectionName);
                    }
                }
            });
        } else {
            LOGGER.log(Level.WARNING, "Delete photo album: Couldn't find the selected photo album in the database!");
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
