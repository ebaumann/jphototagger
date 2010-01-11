/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.favorites;

import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.model.TreeModelFavorites;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.FavoritePropertiesDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the {@link PopupMenuFavorites} and let's edit the selected
 * favorite directory: Rename or set's a different directory when the
 * special menu item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and let's edit the selected
 * file favorite directory if the keys <code>Strg+E</code> were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-23
 */
public final class ControllerUpdateFavorite
        implements ActionListener, KeyListener {

    private final PopupMenuFavorites popupMenu = PopupMenuFavorites.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeFavorites();

    public ControllerUpdateFavorite() {
        listen();
    }

    private void listen() {
        popupMenu.getItemUpdateFavorite().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_E) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                Object userObject =
                        ((DefaultMutableTreeNode) node).getUserObject();
                if (userObject instanceof Favorite) {
                    updateFavorite((Favorite) userObject);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateFavorite(popupMenu.getFavoriteDirectory());
    }

    private void updateFavorite(final Favorite favorite) {
        FavoritePropertiesDialog dialog =
                new FavoritePropertiesDialog();
        dialog.setFavoriteName(favorite.getName());
        dialog.setDirectoryName(favorite.getDirectoryName());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            final String favoriteName = dialog.getFavoriteName();
            final String directoryName = dialog.getDirectoryName();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    TreeModelFavorites model =
                            (TreeModelFavorites) appPanel.getTreeFavorites().
                            getModel();
                    model.replaceFavorite(favorite, new Favorite(
                            favoriteName,
                            directoryName,
                            favorite.getIndex()));
                }
            });
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
