package org.jphototagger.program.controller.imagecollection;

import org.jdesktop.swingx.JXList;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import javax.swing.JTree;

/**
 * Renames the selected image collection when the
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuImageCollections} fires.
 *
 * Also listenes to the {@link JTree}'s key events and renames the selected
 * image collection when the keys <code>Ctrl+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerRenameImageCollection implements ActionListener, KeyListener {
    public ControllerRenameImageCollection() {
        listen();
    }

    private void listen() {
        PopupMenuImageCollections.INSTANCE.getItemRename().addActionListener(this);
        GUI.getImageCollectionsList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getImageCollectionsList();

        if (isRename(evt) &&!list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof String) {
                renameImageCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        renameImageCollection(ListUtil.getItemString(GUI.getImageCollectionsList(),
                PopupMenuImageCollections.INSTANCE.getItemIndex()));
    }

    private boolean isRename(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameImageCollection(final String fromName) {
        if (fromName != null) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!ListModelImageCollections.checkIsNotSpecialCollection(fromName,
                            "ListModelImageCollections.Error.RenameSpecialCollection")) {
                        return;
                    }

                    final String toName = ImageCollectionsHelper.renameImageCollection(fromName);

                    if (toName != null) {
                        ListModelImageCollections model =
                            ModelFactory.INSTANCE.getModel(ListModelImageCollections.class);

                        model.rename(fromName, toName);
                    }
                }
            });
        } else {
            AppLogger.logWarning(ControllerRenameImageCollection.class,
                                 "ControllerRenameImageCollection.Error.NameIsNull");
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
