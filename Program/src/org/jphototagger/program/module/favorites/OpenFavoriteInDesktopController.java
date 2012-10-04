package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;

/**
 * @author Elmar Baumann
 */
public final class OpenFavoriteInDesktopController implements ActionListener {

    public OpenFavoriteInDesktopController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemOpenInDesktop().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath treePath = FavoritesPopupMenu.INSTANCE.getTreePath();
        DefaultMutableTreeNode node = TreeFileSystemDirectories.getNodeOfLastPathComponent(treePath);
        Object userObject = node.getUserObject();
        File directory = null;
        if (userObject instanceof File) {
            directory = (File) userObject;
        } else if (userObject instanceof Favorite) {
            Favorite favorite = (Favorite) userObject;
            directory = favorite.getDirectory();
        }
        openDirectory(directory);
    }

    private void openDirectory(File directory) {
        if (directory == null) {
            return;
        }
        DesktopUtil.open(directory, "JPhotoTagger.BrowseFolder.Executable");
    }
}
