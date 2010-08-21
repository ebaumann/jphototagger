/*
 * @(#)FileChooserExt.java    Created on 2010-08-21
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

package org.jphototagger.lib.dialog;

import org.jphototagger.lib.resource.JslBundle;

import java.io.File;

import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * An extended {@link JFileChooser}:
 *
 * <ul>
 * <li>When saving, the user must confirm if a file exists and
 *    {@link #isConfirmOverwrite()} is true
 * </li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class FileChooserExt extends JFileChooser {
    private static final long serialVersionUID = 1L;
    private boolean           confirmOverwrite;
    private String            addedFilenameExtension;

    public FileChooserExt(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
    }

    public FileChooserExt(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
    }

    public FileChooserExt(FileSystemView fsv) {
        super(fsv);
    }

    public FileChooserExt(File currentDirectory) {
        super(currentDirectory);
    }

    public FileChooserExt(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    public FileChooserExt() {}

    /**
     * Returns, whether to confirm overwriting existing files.
     * 
     * @return true, if confirm
     * @see    #setConfirmOverwrite(boolean)
     */
    public boolean isConfirmOverwrite() {
        return confirmOverwrite;
    }

    /**
     * Sets the behavior if {@link #getDialogType()} is
     * {@link JFileChooser#SAVE_DIALOG}, the user selects an existing file and
     * approves.
     *
     * @param confirmOverwrite if true, {@link #approveSelection()} displays a
     *                         confirm dialog to confirm overwriting existing
     *                         files. Only if overwriting is confirmed, the
     *                         base class implementation of
     *                         {@link #approveSelection()} will be called.
     *                         Default: false.
     * @see #setAddedFilenameExtension(java.lang.String)
     */
    public void setConfirmOverwrite(boolean confirmOverwrite) {
        this.confirmOverwrite = confirmOverwrite;
    }

    /**
     * Sets extension which will be added after selecting a file with a save
     * dialog and the selected file does not have that extension. This extension
     * will be regognized within {@link #setConfirmOverwrite(boolean)}.
     *
     * @param extension case insensitive extension, e.g. <code>".xml"</code> (a
     *                  dot will <em>not</em> be added automatically!). Null if
     *                  no extension will be added to a selected file
     */
    public void setAddedFilenameExtension(String extension) {
        addedFilenameExtension = (extension == null)
                                 ? null
                                 : extension.trim();
    }

    @Override
    public void approveSelection() {
        if ((getDialogType() == JFileChooser.SAVE_DIALOG) && confirmOverwrite
                && selSaveFileExists()) {
            if (confirmOverwrite()) {
                super.approveSelection();
            }
        } else {
            super.approveSelection();
        }
    }

    private boolean selSaveFileExists() {
        if (isMultiSelectionEnabled()) {
            for (File selFile : getSelectedFiles()) {
                if (saveFileExists(selFile)) {
                    return true;
                }
            }

            return false;
        } else {
            File selFile = getSelectedFile();

            return (selFile == null)
                   ? false
                   : saveFileExists(selFile);
        }
    }

    private boolean saveFileExists(File file) {
        if ((addedFilenameExtension == null)
                || addedFilenameExtension.isEmpty()) {
            return file.exists();
        }

        String lcFilename = file.getName().toLowerCase();

        if (lcFilename.endsWith(addedFilenameExtension)) {
            return file.exists();
        } else {
            return new File(file.getAbsolutePath()
                            + addedFilenameExtension).exists();
        }
    }

    private boolean confirmOverwrite() {
        boolean multiSelection = isMultiSelectionEnabled();
        String  question       = multiSelection
                                 ? JslBundle.INSTANCE.getString(
                                     "FileChooserExt.Confirm.OverwriteMultiSel")
                                 : JslBundle.INSTANCE.getString(
                                     "FileChooserExt.Confirm.OverwriteSingleSel");
        String title = JslBundle.INSTANCE.getString(
                           "FileChooserExt.Confirm.OverwriteTitle");

        return JOptionPane.showConfirmDialog(null, question, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
