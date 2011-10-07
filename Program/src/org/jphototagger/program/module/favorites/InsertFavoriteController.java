package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.module.directories.DirectoriesPopupMenu;

/**
 * Listens to the {@code FavoritesPopupMenu} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * Also listens to the {@code JTree}'s key events and inserts a new favorite if
 * the keys <code>Ctrl+I</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class InsertFavoriteController implements ActionListener, KeyListener {

    public InsertFavoriteController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemInsertFavorite().addActionListener(this);
        DirectoriesPopupMenu.INSTANCE.getItemAddToFavorites().addActionListener(this);
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
        boolean isAddToFavorites = DirectoriesPopupMenu.INSTANCE.getItemAddToFavorites().equals(o);

        if (isAddToFavorites) {
            directory = DirectoriesPopupMenu.INSTANCE.getDirectory();
        }

        return directory;
    }

    private void insertFavorite(final File directory) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                FavoritePropertiesDialog dlg = new FavoritePropertiesDialog();

                if (directory != null) {
                    dlg.setDirectory(directory);
                    dlg.setEnabledButtonChooseDirectory(false);
                }

                dlg.setVisible(true);

                if (dlg.isAccepted()) {
                    FavoritesTreeModel model = ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class);
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
