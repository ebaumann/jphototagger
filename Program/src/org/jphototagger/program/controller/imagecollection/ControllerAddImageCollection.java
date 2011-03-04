package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.comparator.ComparatorStringAscending;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import javax.swing.JList;

/**
 * Kontrolliert Aktion: Erzeuge eine Bildsammlung, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * Also listens to the {@link JList}'s key events and creates a new image
 * collection when the keys <code>Ctrl+N</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerAddImageCollection implements ActionListener, KeyListener {
    public ControllerAddImageCollection() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemCreateImageCollection().addActionListener(this);
        PopupMenuImageCollections.INSTANCE.getItemCreate().addActionListener(this);
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
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    insertImageCollection(collectionName);
                }
            });
        }
    }

    private void insertImageCollection(String collectionName) {
        ListModelImageCollections model = ModelFactory.INSTANCE.getModel(ListModelImageCollections.class);

        ListUtil.insertSorted(model, collectionName, ComparatorStringAscending.INSTANCE,
                              ListModelImageCollections.getSpecialCollectionCount(), model.getSize() - 1);
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
