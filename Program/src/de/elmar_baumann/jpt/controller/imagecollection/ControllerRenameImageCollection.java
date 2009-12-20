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
package de.elmar_baumann.jpt.controller.imagecollection;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.helper.ModifyImageCollections;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuImageCollections;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * Renames the selected image collection when the
 * {@link de.elmar_baumann.jpt.view.popupmenus.PopupMenuImageCollections} fires.
 *
 * Also listenes to the {@link JTree}'s key events and renames the selected
 * image collection when the keys <code>Ctrl+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-00-10
 */
public final class ControllerRenameImageCollection
        implements ActionListener, KeyListener {

    private final PopupMenuImageCollections popupMenu =
            PopupMenuImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();

    public ControllerRenameImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemRename().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isRename(e) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();
            if (value instanceof String) {
                renameImageCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameImageCollection(ListUtil.getItemString(
                list, popupMenu.getItemIndex()));
    }

    private boolean isRename(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameImageCollection(final String oldName) {
        if (oldName != null) {
            if (!ListModelImageCollections.checkIsNotSpecialCollection(oldName,
                    "ListModelImageCollections.Error.RenameSpecialCollection")) // NOI18N
                return;
            final String newName =
                    ModifyImageCollections.renameImageCollection(oldName);
            if (newName != null) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        ListModel model = list.getModel();
                        if (model instanceof ListModelImageCollections) {
                            ((ListModelImageCollections) model).rename(
                                    oldName, newName);
                        }
                    }
                });
            }
        } else {
            AppLog.logWarning(ControllerRenameImageCollection.class,
                    "ControllerRenameImageCollection.Error.NameIsNull"); // NOI18N
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
