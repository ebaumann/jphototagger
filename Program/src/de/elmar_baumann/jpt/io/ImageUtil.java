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
package de.elmar_baumann.jpt.io;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppFileFilters;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.helper.FilesystemDatabaseUpdater;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.jpt.view.dialogs.MoveToDirectoryDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for images.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-14
 */
public final class ImageUtil {

    /**
     * Returns wheter a file is an image file.
     *
     * @param  file file
     * @return      true if the file is an image file
     */
    public static boolean isImageFile(File file) {
        return AppFileFilters.ACCEPTED_IMAGE_FILENAME_FILTER.accept(file);
    }

    /**
     * Returns from a collection of files the image files.
     *
     * @param  files files
     * @return       image files of that files
     */
    public static List<File> getImageFiles(Collection<? extends File> files) {

        List<File> imageFiles = new ArrayList<File>(files.size());

        for (File file : files) {
            if (isImageFile(file)) {
                imageFiles.add(file);
            }
        }
        return imageFiles;
    }

    public enum ConfirmOverwrite {
        YES,
        NO;

        public boolean yes() {
            return this.equals(YES);
        }

        public static ConfirmOverwrite fromBoolean(boolean b) {
            return b ? YES : NO;
        }
    }

    /**
     * Copies image files to a directory with the {@link CopyToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@link ConfirmOverwrite#YES} if the user must
     *                        confirm overwrite existing files
     */
    public static void copyImageFiles(
            List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {

        if (confirm.yes() &&
            !confirmFileAction("ImageUtil.Confirm.Copy", sourceFiles.size(), targetDirectory.getAbsolutePath())) {
            return;
        }

        CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();

        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        dialog.addFileSystemActionListener(new FilesystemDatabaseUpdater(true));

        addProgressListener(dialog);

        dialog.copy(true, UserSettings.INSTANCE.getCopyMoveFilesOptions());
    }

    /**
     * Moves image files to a directory with the {@link MoveToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@link ConfirmOverwrite#YES} if the user must
     *                        confirm overwrite existing files
     */
    public static void moveImageFiles(
            List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {

        if (confirm.yes() &&
            !confirmFileAction("ImageUtil.Confirm.Move", sourceFiles.size(), targetDirectory.getAbsolutePath())) {
            return;
        }

        MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();

        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);

        addProgressListener(dialog);

        dialog.setVisible(true);
    }

    private static boolean confirmFileAction(String bundleKey, int size, String absolutePath) {
        return MessageDisplayer.confirmYesNo(null, bundleKey, size, absolutePath);
    }

    private synchronized static void addProgressListener(MoveToDirectoryDialog dialog) {

        dialog.addProgressListener(new ProgressListener() {

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

    private synchronized static void addProgressListener(CopyToDirectoryDialog dialog) {

        dialog.addProgressListener(new ProgressListener() {

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
        List<File> files = new ArrayList<File>(imageFiles.size() * 2);

        for (File imageFile : imageFiles) {

            if (imageFile != null && isImageFile(imageFile)) {

                files.add(imageFile);

                String sidecarFilename = XmpMetadata.getSidecarFilename(imageFile.getAbsolutePath());
                File   sidecarFile     = sidecarFilename == null ? null : new File(sidecarFilename);

                if (sidecarFile != null) {
                    files.add(sidecarFile);
                }
            }
        }
        return files;
    }

    private ImageUtil() {
    }
}
