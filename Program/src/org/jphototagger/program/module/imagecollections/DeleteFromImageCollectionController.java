package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;

/**
 * Listens to key events of {@code ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * image collection.
 *
 * @author Elmar Baumann
 */
public final class DeleteFromImageCollectionController implements ActionListener, KeyListener {

    public DeleteFromImageCollectionController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemDeleteFromImageCollection().addActionListener(this);
        GUI.getThumbnailsPanel().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        delete();
    }

    private void delete() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (tnPanel.getOriginOfDisplayedThumbnails().equals(OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION) && (tnPanel.isAFileSelected())) {
            ImageCollectionsUtil.deleteSelectedFiles();
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
