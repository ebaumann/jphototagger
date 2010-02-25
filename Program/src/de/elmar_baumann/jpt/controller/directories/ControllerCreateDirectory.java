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
package de.elmar_baumann.jpt.controller.directories;

import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemCreateDirectory()} and
 * creates a directory when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and creates a new directory
 * into the selected directory when the keys <code>Ctrl+N</code> was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-19
 */
public final class ControllerCreateDirectory extends ControllerDirectory {

    public ControllerCreateDirectory() {
        listenToActionsOf(PopupMenuDirectories.INSTANCE.getItemCreateDirectory());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControl(evt, KeyEvent.VK_N);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuDirectories.INSTANCE.getItemCreateDirectory();
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        ModelFactory.INSTANCE.getModel(TreeModelAllSystemDirectories.class).
                                                        createDirectoryIn(node);
    }
}
