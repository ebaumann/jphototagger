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
 * {@link JFileChooser} with the following extensions (<strong>Save
 * Mode</strong> means, {@link #getDialogType()} is
 * {@link JFileChooser#SAVE_DIALOG}):
 *
 * <ul>
 * <li>If an extension, e.g. <code>".xml"</code>, was set through
 *     {@link #setSaveFilenameExtension(String)}, in <strong>Save Mode</strong>,
 *     files returned from {@link #getSelectedFile()} or
 *     {@link #getSelectedFiles()} are having that extension if the user did not
 *     input it
 * </li>
 * <li>In <strong>Save Mode</strong>, the user can be forced to
 *     <strong>confirm</strong> overwriting existing files through
 *     {@link #setConfirmOverwrite(boolean)}
 * </li>
 * </ul>
 * <p>
 *
 * @author Elmar Baumann
 */
public final class FileChooserExt extends JFileChooser {
    private static final long serialVersionUID = 2263451265476542816L;
    private boolean           confirmOverwrite;
    private String            saveFilenameExtension;

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
     * Returns, whether to confirm overwrite existing files in Save Mode.
     *
     * @return true, if the user has to confirm overwrite existing files
     * @see    #setConfirmOverwrite(boolean)
     */
    public boolean isConfirmOverwrite() {
        return confirmOverwrite;
    }

    /**
     * Sets whether to confirm overwriting existing files in Save Mode.
     *
     * @param confirmOverwrite if true, the user has to confirm overwrite
     *                         existing files. Default: false.
     */
    public void setConfirmOverwrite(boolean confirmOverwrite) {
        this.confirmOverwrite = confirmOverwrite;
    }

    /**
     * Sets an extension which shall be added to files in Save Mode if the user
     * did not input that extension.
     * <p>
     * {@link #getSelectedFile()} or {@link #getSelectedFiles()} are returning
     * files having that extension in Save Mode.
     * <p>
     * Extensions are treated as <strong>case insensitive</strong> because some
     * file systems do so: A file named <code>"a.xml"</code> and a file named
     * <code>"a.XML"</code> are both having the extension <code>".xml"</code>.
     *
     * @param extension extension, e.g. <code>".xml"</code>. A leading dot will
     *                  <em>not</em> be added automatically! Null if no
     *                  extension shall be added to a selected file.
     *                  Default: null.
     */
    public void setSaveFilenameExtension(String extension) {
        saveFilenameExtension = (extension == null)
                                ? null
                                : extension;

        if ((saveFilenameExtension != null)
                && saveFilenameExtension.isEmpty()) {
            saveFilenameExtension = null;
        }
    }

    @Override
    public void approveSelection() {
        if (isSave() && confirmOverwrite && selSaveFileExists()) {

            // Has to be here, not in the if clause (else
            // super.approveSelection() in the else branch would be called if
            // not confirmed)!
            if (confirmOverwrite()) {
                super.approveSelection();
            }
        } else {
            super.approveSelection();
        }
    }

    private boolean isSave() {
        return getDialogType() == JFileChooser.SAVE_DIALOG;
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
        if (saveFilenameExtension == null) {
            return file.exists();
        }

        final String path = file.getPath();

        // Case insensitivity because some file systems are treating the same
        // lower and upper case characters as equals (A == a, B == b, ...)
        if (path.toLowerCase().endsWith(saveFilenameExtension.toLowerCase())) {

            // Can lead to multiple files with the same extension but different
            // cases on case sensitive file systems
            return file.exists();
        } else {
            return new File(path + saveFilenameExtension).exists();
        }
    }

    private boolean confirmOverwrite() {
        boolean multiSel = isMultiSelectionEnabled();
        String  question = multiSel
                           ? JslBundle.INSTANCE.getString(
                               "FileChooserExt.Confirm.OverwriteMultiSel")
                           : JslBundle.INSTANCE.getString(
                               "FileChooserExt.Confirm.OverwriteSingleSel");
        String title = JslBundle.INSTANCE.getString(
                           "FileChooserExt.Confirm.OverwriteTitle");

        return JOptionPane.showConfirmDialog(null, question, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Ensures in Save Mode - if set through
     * {@link #setSaveFilenameExtension(String)} - that all selected files are
     * having the set extension.
     *
     * @return selected files
     */
    @Override
    public File[] getSelectedFiles() {
        File[] selFiles = super.getSelectedFiles();
        int    length   = selFiles.length;

        for (int i = 0; i < length; i++) {
            selFiles[i] = ensureSaveFilenameExtension(selFiles[i]);
        }

        return selFiles;
    }

    /**
     * Ensures in Save Mode - if set through
     * {@link #setSaveFilenameExtension(String)} - that the selected file has
     * the set extension.
     *
     * @return selected file
     */
    @Override
    public File getSelectedFile() {
        return ensureSaveFilenameExtension(super.getSelectedFile());
    }

    private File ensureSaveFilenameExtension(File file) {
        if ((file != null) && (saveFilenameExtension != null) && isSave()) {
            final String path = file.getPath();

            // Case insensitivity because some file systems are treating the
            // same lower and upper case characters as equals (A == a, B == b,
            // ...)
            if (!path.toLowerCase().endsWith(
                    saveFilenameExtension.toLowerCase())) {
                return new File(path + saveFilenameExtension);
            }
        }

        return file;
    }
}
