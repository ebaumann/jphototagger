/*
 * @(#)ControllerDeleteImageCollection.java    Created on 2008-00-10
 *
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

package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.ModifyImageCollections;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;
import org.jphototagger.lib.componentutil.ListUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Aktion: Lösche Bildsammlung, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuImageCollections}.
 *
 * Also listens to the {@link JList}'s key events and deletes the selected image
 * collection when the keys <code>Ctrl+N</code> were pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDeleteImageCollection
        implements ActionListener, KeyListener {
    private final PopupMenuImageCollections popupMenu =
        PopupMenuImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList    list     = appPanel.getListImageCollections();

    public ControllerDeleteImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemDelete().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_DELETE) &&!list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof String) {
                deleteCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteCollection(ListUtil.getItemString(list,
                popupMenu.getItemIndex()));
    }

    private void deleteCollection(final String collectionName) {
        if (!ListModelImageCollections.checkIsNotSpecialCollection(
                collectionName,
                "ControllerDeleteImageCollection.Error.SpecialCollection")) {
            return;
        }

        if (collectionName != null) {
            if (ModifyImageCollections.deleteImageCollection(collectionName)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ModelFactory.INSTANCE.getModel(
                            ListModelImageCollections.class).removeElement(
                            collectionName);
                    }
                });
            }
        } else {
            AppLogger.logWarning(
                ControllerDeleteImageCollection.class,
                "ControllerDeleteImageCollection.Error.CollectionNameIsNull");
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
