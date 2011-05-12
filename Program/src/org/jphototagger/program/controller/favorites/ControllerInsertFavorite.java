package org.jphototagger.program.controller.favorites;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.FavoritePropertiesDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import javax.swing.JTree;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to the {@link PopupMenuFavorites} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and inserts a new favorite if
 * the keys <code>Ctrl+I</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerInsertFavorite implements ActionListener, KeyListener {
    public ControllerInsertFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemInsertFavorite().addActionListener(this);
        PopupMenuDirectories.INSTANCE.getItemAddToFavorites().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_I)) {
            insertFavorite(null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        insertFavorite(getDirectory(evt.getSource()));
    }

    private File getDirectory(Object o) {
        File directory = null;
        boolean isAddToFavorites = PopupMenuDirectories.INSTANCE.getItemAddToFavorites().equals(o);

        if (isAddToFavorites) {
            directory = PopupMenuDirectories.INSTANCE.getDirectory();
        }

        return directory;
    }

    private void insertFavorite(final File directory) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                FavoritePropertiesDialog dlg = new FavoritePropertiesDialog();

                if (directory != null) {
                    dlg.setDirectory(directory);
                    dlg.setEnabledButtonChooseDirectory(false);
                }

                dlg.setVisible(true);

                if (dlg.isAccepted()) {
                    TreeModelFavorites model = ModelFactory.INSTANCE.getModel(TreeModelFavorites.class);
                    Favorite favorite = new Favorite();

                    favorite.setName(dlg.getFavoriteName());
                    favorite.setDirectory(dlg.getDirectory());
                    model.insert(favorite);
                }
            }
        });
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
