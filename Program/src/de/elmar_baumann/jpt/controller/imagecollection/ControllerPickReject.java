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

import de.elmar_baumann.jpt.app.AppTexts;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.JList;
import javax.swing.JMenuItem;

/**
 * Listens to the menu item {@link ThumbnailsPanel} to key events
 * and on action adds a new keyword below the selected keyword.
 * <p>
 * If the key <strong>P</strong> was pressed, this class adds the selected
 * thumbnails to the <strong>Pick</strong> collection and if the key
 * <strong>R</strong> was pressed to the <strong>Reject</strong> collection.
 * <p>
 * Also listens to the {@link PopupMenuThumbnails#getItemPick()} and
 * {@link PopupMenuThumbnails#getItemReject()} and does the same as by the
 * key events <strong>P</strong> or <strong>R</strong>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-21
 */
public final class ControllerPickReject implements ActionListener, KeyListener {

    private final ThumbnailsPanel panelThumbnails =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem itemPick =
            PopupMenuThumbnails.INSTANCE.getItemPick();
    private final JMenuItem itemReject =
            PopupMenuThumbnails.INSTANCE.getItemReject();

    public ControllerPickReject() {
        listen();
    }

    private void listen() {
        panelThumbnails.addKeyListener(this);
        itemPick.addActionListener(this);
        itemReject.addActionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            addOrRemove(true);
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            addOrRemove(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(itemPick)) {
            addOrRemove(true);
        } else if (e.getSource().equals(itemReject)) {
            addOrRemove(false);
        }
    }

    private void addOrRemove(boolean pick) {
        if (pick && isPickCollection() || !pick && isRejectCollection()) return;
        if (panelThumbnails.getSelectionCount() > 0) {
            List<File> selFiles = panelThumbnails.getSelectedFiles();
            GUI.INSTANCE.getAppPanel().showMessage(getPopupMessage(pick), false, 1000);
            addToCollection(
                    pick
                    ? AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED
                    : AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED,
                    selFiles);
            if (pick && isRejectCollection() || !pick && isPickCollection()) {
                deleteFromCollection(
                        pick
                        ? AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED
                        : AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED,
                        selFiles);
                panelThumbnails.remove(selFiles);
            }
        }
    }

    private String getPopupMessage(boolean pick) {
        return pick
                ? Bundle.getString("ControllerPickReject.Info.Pick")
                : Bundle.getString("ControllerPickReject.Info.Reject");
    }

    private boolean isPickCollection() {
        return isCollection(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED);
    }

    private boolean isRejectCollection() {
        return isCollection(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED);
    }

    private boolean isCollection(String collection) {
        if (!panelThumbnails.getContent().equals(Content.IMAGE_COLLECTION))
            return false;
        JList list = GUI.INSTANCE.getAppPanel().getListImageCollections();
        if (list.getSelectedIndex() < 0) return false;
        return list.getSelectedValue().toString().equals(collection);
    }

    private void addToCollection(String collection, List<File> files) {
        DatabaseImageCollections.INSTANCE.insertImagesIntoCollection(
                collection, FileUtil.getAsFilenames(files));
    }

    private void deleteFromCollection(String collection, List<File> files) {
        DatabaseImageCollections.INSTANCE.deleteImagesFromCollection(
                collection, FileUtil.getAsFilenames(files));
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
