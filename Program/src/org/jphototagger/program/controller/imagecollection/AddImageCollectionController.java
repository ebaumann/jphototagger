package org.jphototagger.program.controller.imagecollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.model.ImageCollectionsListModel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.ImageCollectionsPopupMenu;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 *
 * @author Elmar Baumann
 */
public final class AddImageCollectionController implements ActionListener, KeyListener {

    public AddImageCollectionController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemCreateImageCollection().addActionListener(this);
        ImageCollectionsPopupMenu.INSTANCE.getItemCreate().addActionListener(this);
        GUI.getImageCollectionsList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N)) {
            createImageCollectionOfSelectedFiles();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        createImageCollectionOfSelectedFiles();
    }

    private void createImageCollectionOfSelectedFiles() {
        final String collectionName = ImageCollectionsHelper.insertImageCollection(GUI.getSelectedImageFiles());

        if (collectionName != null) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    insertImageCollection(collectionName);
                }
            });
        }
    }

    private void insertImageCollection(String collectionName) {
        ImageCollectionsListModel model = ModelFactory.INSTANCE.getModel(ImageCollectionsListModel.class);

        if (!model.contains(collectionName)) {
            model.addElement(collectionName);
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
