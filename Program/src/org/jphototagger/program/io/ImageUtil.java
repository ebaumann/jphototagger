/*
 * @(#)ImageUtil.java    Created on 2009-08-14
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

package org.jphototagger.program.io;

import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.FilesystemDatabaseUpdater;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.CopyToDirectoryDialog;
import org.jphototagger.program.view.dialogs.MoveToDirectoryDialog;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for images.
 *
 * @author  Elmar Baumann
 */
public final class ImageUtil {
    private ImageUtil() {}

    public enum ConfirmOverwrite {
        YES, NO;

        public boolean yes() {
            return this.equals(YES);
        }

        public static ConfirmOverwrite fromBoolean(boolean b) {
            return b
                   ? YES
                   : NO;
        }
    }

    /**
     * Returns wheter a file is an image file.
     *
     * @param  file file
     * @return      true if the file is an image file
     */
    public static boolean isImageFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return AppFileFilters.ACCEPTED_IMAGE_FILENAMES.accept(file);
    }

    /**
     * Returns from a collection of files the image files.
     *
     * @param  files files
     * @return       image files of that files
     */
    public static List<File> getImageFiles(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<File> imageFiles = new ArrayList<File>(files.size());

        for (File file : files) {
            if (isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }

    /**
     * Copies image files to a directory with the {@link CopyToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@link ConfirmOverwrite#YES} if the user must
     *                        confirm overwrite existing files
     */
    public static void copyImageFiles(List<File> sourceFiles,
                                      File targetDirectory,
                                      ConfirmOverwrite confirm) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        if (targetDirectory == null) {
            throw new NullPointerException("targetDirectory == null");
        }

        if (confirm == null) {
            throw new NullPointerException("confirm == null");
        }

        if (confirm.yes()
                &&!confirmFileAction("ImageUtil.Confirm.Copy",
                                     sourceFiles.size(),
                                     targetDirectory.getAbsolutePath())) {
            return;
        }

        CopyToDirectoryDialog dlg = new CopyToDirectoryDialog();

        dlg.setTargetDirectory(targetDirectory);
        dlg.setSourceFiles(sourceFiles);
        dlg.addFileSystemActionListener(new FilesystemDatabaseUpdater(true));
        addProgressListener(dlg);
        dlg.copy(true, UserSettings.INSTANCE.getCopyMoveFilesOptions());
    }

    /**
     * Moves image files to a directory with the {@link MoveToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@link ConfirmOverwrite#YES} if the user must
     *                        confirm overwrite existing files
     */
    public static void moveImageFiles(List<File> sourceFiles,
                                      File targetDirectory,
                                      ConfirmOverwrite confirm) {
        if (confirm.yes()
                &&!confirmFileAction("ImageUtil.Confirm.Move",
                                     sourceFiles.size(),
                                     targetDirectory.getAbsolutePath())) {
            return;
        }

        MoveToDirectoryDialog dlg = new MoveToDirectoryDialog();

        dlg.setTargetDirectory(targetDirectory);
        dlg.setSourceFiles(sourceFiles);
        addProgressListener(dlg);
        dlg.setVisible(true);
    }

    private static boolean confirmFileAction(String bundleKey, int size,
            String absolutePath) {
        return MessageDisplayer.confirmYesNo(null, bundleKey, size,
                absolutePath);
    }

    private synchronized static void addProgressListener(
            MoveToDirectoryDialog dlg) {
        dlg.addProgressListener(new ProgressListener() {
            @Override
            public void progressStarted(ProgressEvent evt) {

                // ignore
            }
            @Override
            public void progressPerformed(ProgressEvent evt) {

                // ignore
            }
            @Override
            public void progressEnded(ProgressEvent evt) {
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
            }
        });
    }

    private synchronized static void addProgressListener(
            CopyToDirectoryDialog dlg) {
        dlg.addProgressListener(new ProgressListener() {
            @Override
            public void progressStarted(ProgressEvent evt) {

                // ignore
            }
            @Override
            public void progressPerformed(ProgressEvent evt) {

                // ignore
            }
            @Override
            public void progressEnded(ProgressEvent evt) {
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
            }
        });
    }

    /**
     * Adds to a list of image files sidecar files.
     *
     * If a file in <code>imageFiles</code> is not an image file it will not
     * be added.
     *
     * @param  imageFiles image files
     * @return            image files and their sidecar files
     */
    public static List<File> addSidecarFiles(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        List<File> files = new ArrayList<File>(imageFiles.size() * 2);

        for (File imageFile : imageFiles) {
            if ((imageFile != null) && isImageFile(imageFile)) {
                files.add(imageFile);

                File sidecarFile = XmpMetadata.getSidecarFile(imageFile);

                if (sidecarFile != null) {
                    files.add(sidecarFile);
                }
            }
        }

        return files;
    }
}
