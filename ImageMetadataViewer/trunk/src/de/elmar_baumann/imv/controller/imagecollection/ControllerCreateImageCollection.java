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
package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.helper.ModifyImageCollections;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Aktion: Erzeuge eine Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * Also listens to the {@link JList}'s key events and creates a new image
 * collection when the keys <code>Ctrl+N</code> were pressed.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class ControllerCreateImageCollection
        implements ActionListener, KeyListener {

    private final PopupMenuThumbnails popupMenuThumbnails =
            PopupMenuThumbnails.INSTANCE;
    private final PopupMenuImageCollections popupMenuImageCollections =
            PopupMenuImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList listImageCollections = appPanel.getListImageCollections();
    private final ThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerCreateImageCollection() {
        listen();
    }

    private void listen() {
        popupMenuThumbnails.getItemCreateImageCollection().addActionListener(this);
        popupMenuImageCollections.getItemCreate().addActionListener(this);
        listImageCollections.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_N)) {
            createImageCollectionOfSelectedFiles();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createImageCollectionOfSelectedFiles();
    }

    private void createImageCollectionOfSelectedFiles() {
        final String collectionName =
                ModifyImageCollections.insertImageCollection(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        if (collectionName != null) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ListModel model = listImageCollections.getModel();
                    if (model instanceof ListModelImageCollections) {
                        ListUtil.insertSorted((ListModelImageCollections) model,
                                collectionName,
                                ComparatorStringAscending.IGNORE_CASE,
                                ListModelImageCollections.
                                getSpecialCollectionCount(),
                                model.getSize() - 1);
                    }
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
