package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;

/**
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
        ImageCollectionsUtil.insertImageCollection(GUI.getSelectedImageFiles());
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
