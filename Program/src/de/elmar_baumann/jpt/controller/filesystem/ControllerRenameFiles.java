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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.cache.RenderedThumbnailCache;
import de.elmar_baumann.jpt.cache.ThumbnailCache;
import de.elmar_baumann.jpt.cache.XmpCache;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.FileSystemEvent;
import de.elmar_baumann.jpt.event.listener.FileSystemListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.RenameDialog;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when
 * <code>F2</code> was pressed shows the {@link RenameDialog} to rename the
 * selected files.
 *
 * @author  Elmar Baumann
 * @version 2008-10-13
 */
public final class ControllerRenameFiles
        implements ActionListener, KeyListener, FileSystemListener {

    private final ThumbnailsPanel    thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem          menuItemRename  = PopupMenuThumbnails.INSTANCE.getItemFileSystemRenameFiles();
    private final DatabaseImageFiles db              = DatabaseImageFiles.INSTANCE;

    public ControllerRenameFiles() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addKeyListener(this);
        menuItemRename.addActionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F2) {
            renameSelectedFiles();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(menuItemRename)) {
            renameSelectedFiles();
        }
    }

    private void renameSelectedFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            RenameDialog dialog = new RenameDialog();
            Collections.sort(files);
            dialog.setFiles(files);
            dialog.addFileSystemListener(this);
            dialog.setEnabledTemplates(thumbnailsPanel.getContent().isUniqueFileSystemDirectory());
            dialog.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(FileSystemEvent event) {

        if (!event.getType().equals(FileSystemEvent.Type.RENAME) || event.isError()) return;

        final File src    = event.getSource();
        final File target = event.getTarget();

        AppLogger.logInfo(ControllerRenameFiles.class, "ControllerRenameFiles.Info.Rename", src, target);

        db.updateRename(src.getAbsolutePath(), target.getAbsolutePath());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ThumbnailCache.INSTANCE.updateFiles(src, target);
                XmpCache.INSTANCE.updateFiles(src, target);
                RenderedThumbnailCache.INSTANCE.updateFiles(src, target);
                thumbnailsPanel.rename(src, target);
            }
        });
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
