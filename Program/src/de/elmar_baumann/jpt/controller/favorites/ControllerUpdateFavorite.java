/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.favorites;

import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.helper.FavoritesHelper;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTree;
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
public final class ControllerUpdateFavorite extends ControllerFavorite {

    public ControllerUpdateFavorite() {
        listenToActionsOf(PopupMenuFavorites.INSTANCE.getItemUpdateFavorite());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControl(evt, KeyEvent.VK_E);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuFavorites.INSTANCE.getItemUpdateFavorite();
    }

    @Override
    protected void action(Favorite favorite) {
        FavoritesHelper.updateFavorite(favorite);
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        // ignore
    }
}
