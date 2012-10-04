package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * Renames the selected image collection when the
 * {@code org.jphototagger.program.view.popupmenus.ImageCollectionsPopupMenu} fires.
 *
 * Also listenes to the {@code JTree}'s key events and renames the selected
 * image collection when the keys <code>Ctrl+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author Elmar Baumann
 */
public final class RenameImageCollectionController implements ActionListener, KeyListener {

    private static final Logger LOGGER = Logger.getLogger(RenameImageCollectionController.class.getName());

    public RenameImageCollectionController() {
        listen();
    }

    private void listen() {
        ImageCollectionsPopupMenu.INSTANCE.getItemRename().addActionListener(this);
        GUI.getImageCollectionsList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getImageCollectionsList();

        if (isRename(evt) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof String) {
                renameImageCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        renameImageCollection(ListUtil.getItemString(GUI.getImageCollectionsList(),
                ImageCollectionsPopupMenu.INSTANCE.getItemIndex()));
    }

    private boolean isRename(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameImageCollection(final String fromName) {
        if (fromName != null) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    String errorMessage = Bundle.getString(RenameImageCollectionController.class, "ListModelImageCollections.Error.RenameSpecialCollection", fromName);
                    if (!ImageCollectionsUtil.checkIsNotSpecialCollection(fromName, errorMessage)) {
                        return;
                    }

                    ImageCollectionsUtil.renameImageCollection(fromName);
                }
            });
        } else {
            LOGGER.log(Level.WARNING, "Rename photo album: Couldn't find the album's name (Item value == null)!");
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
