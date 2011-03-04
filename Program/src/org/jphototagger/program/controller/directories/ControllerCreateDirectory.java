package org.jphototagger.program.controller.directories;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.controller.favorites.ControllerFavoritesAddFilesystemFolder;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.EventQueue;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemCreateDirectory()} and
 * creates a directory when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and creates a new directory
 * into the selected directory when the keys <code>Ctrl+N</code> was pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerCreateDirectory extends ControllerDirectory {
    public ControllerCreateDirectory() {
        listenToActionsOf(PopupMenuDirectories.INSTANCE.getItemCreateDirectory());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == PopupMenuDirectories.INSTANCE.getItemCreateDirectory();
    }

    @Override
    protected void action(final DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                File dir = ModelFactory.INSTANCE.getModel(TreeModelAllSystemDirectories.class).createDirectoryIn(node);

                if (dir != null) {
                    ControllerFavoritesAddFilesystemFolder ctrl =
                        ControllerFactory.INSTANCE.getController(ControllerFavoritesAddFilesystemFolder.class);

                    if (ctrl != null) {
                        ctrl.confirmMoveSelFilesInto(dir);
                    }
                }
            }
        });
    }
}
