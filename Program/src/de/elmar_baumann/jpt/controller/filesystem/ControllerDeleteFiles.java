/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.controller.imagecollection.ControllerDeleteFromImageCollection;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.helper.DeleteImageFiles;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.types.DeleteOption;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * file system if the panel's content is <em>not</em>
 * {@link Content#IMAGE_COLLECTION}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-12
 * @see     ControllerDeleteFromImageCollection
 */
public final class ControllerDeleteFiles implements ActionListener, KeyListener {

    private final ThumbnailsPanel     thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles  db              = DatabaseImageFiles.INSTANCE;
    private final PopupMenuThumbnails popupMenu       = PopupMenuThumbnails.INSTANCE;

    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        popupMenu.getItemFileSystemDeleteFiles().addActionListener(this);
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            if (thumbnailsPanel.getContent().equals(Content.IMAGE_COLLECTION)) return;
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        if (thumbnailsPanel.getSelectionCount() > 0 &&
                thumbnailsPanel.getContent().canDeleteImagesFromFileSystem()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    deleteSelectedFiles();
                }
            });
        }
    }

    private void deleteSelectedFiles() {
        List<File> deletedImageFiles = DeleteImageFiles.delete(
                thumbnailsPanel.getSelectedFiles(), DeleteOption.CONFIRM_DELETE, DeleteOption.MESSAGES_ON_FAILURES);
        if (deletedImageFiles.size() > 0) {
            db.delete(FileUtil.getAsFilenames(deletedImageFiles));
            thumbnailsPanel.remove(deletedImageFiles);
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
