/*
 * @(#)ControllerRenameFiles.java    Created on 2008-10-13
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.filesystem;

import java.awt.EventQueue;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.cache.RenderedThumbnailCache;
import org.jphototagger.program.cache.ThumbnailCache;
import org.jphototagger.program.cache.XmpCache;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.RenameDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.Collections;
import java.util.List;

import javax.swing.JMenuItem;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when
 * <code>F2</code> was pressed shows the {@link RenameDialog} to rename the
 * selected files.
 *
 * @author  Elmar Baumann
 */
public final class ControllerRenameFiles
        implements ActionListener, KeyListener, FileSystemListener {
    private final ThumbnailsPanel tnPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem menuItemRename =
        PopupMenuThumbnails.INSTANCE.getItemFileSystemRenameFiles();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    public ControllerRenameFiles() {
        listen();
    }

    private void listen() {
        tnPanel.addKeyListener(this);
        menuItemRename.addActionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_F2) {
            renameSelectedFiles();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(menuItemRename)) {
            renameSelectedFiles();
        }
    }

    private void renameFile(final File fromFile, final File toFile) {
        AppLogger.logInfo(ControllerRenameFiles.class,
                          "ControllerRenameFiles.Info.Rename", fromFile,
                          toFile);
        db.updateRename(fromFile, toFile);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ThumbnailCache.INSTANCE.updateFiles(fromFile, toFile);
                XmpCache.INSTANCE.updateFiles(fromFile, toFile);
                RenderedThumbnailCache.INSTANCE.updateFiles(fromFile, toFile);
                tnPanel.rename(fromFile, toFile);
            }
        });
    }

    private void renameSelectedFiles() {
        List<File> selFiles = tnPanel.getSelectedFiles();

        if (selFiles.size() > 0) {
            RenameDialog dlg = new RenameDialog();

            Collections.sort(selFiles);
            dlg.setImageFiles(selFiles);
            dlg.addFileSystemListener(this);
            dlg.setEnabledTemplates(
                tnPanel.getContent().isUniqueFileSystemDirectory());
            dlg.setVisible(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }

    @Override
    public void fileRenamed(final File fromFile, final File toFile) {
        renameFile(fromFile, toFile);
        }

            @Override
    public void fileCopied(File source, File target) {

        // ignore
    }

    @Override
    public void fileDeleted(File file) {

        // ignore
    }

    @Override
    public void fileMoved(File source, File target) {

        // ignore
    }
}
