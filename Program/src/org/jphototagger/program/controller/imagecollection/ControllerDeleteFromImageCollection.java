package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.program.controller.filesystem.ControllerDeleteFiles;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * image collection.
 *
 * @author Elmar Baumann
 * @see     ControllerDeleteFiles
 */
public final class ControllerDeleteFromImageCollection implements ActionListener, KeyListener {
    public ControllerDeleteFromImageCollection() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemDeleteFromImageCollection().addActionListener(this);
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

        if (tnPanel.getContent().equals(Content.IMAGE_COLLECTION) && (tnPanel.isAFileSelected())) {
            ImageCollectionsHelper.deleteSelectedFiles();
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
