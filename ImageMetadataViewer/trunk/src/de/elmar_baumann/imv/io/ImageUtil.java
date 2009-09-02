/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppFileFilter;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.helper.FilesystemDatabaseUpdater;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog;
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
        return AppFileFilter.ACCEPTED_IMAGE_FILE_FORMATS.accept(file);
    }

    /**
     * Returns from a collection of files the image files.
     *
     * FIXME better class
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

    /**
     * Copies image files to a directory with the {@link CopyToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         true if the user shall confirm copying
     */
    public static void copyImageFiles(
            List<File> sourceFiles, File targetDirectory, boolean confirm) {

        if (confirm && !confirmFileAction(
                "IoUtil.Confirm.Copy", sourceFiles.size(), // NOI18N
                targetDirectory.getAbsolutePath())) return;

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
     * @param confirm         true if the user shall confirm copying
     */
    public static void moveImageFiles(
            List<File> sourceFiles, File targetDirectory, boolean confirm) {

        if (confirm && !confirmFileAction(
                "IoUtil.Confirm.Move", sourceFiles.size(), // NOI18N
                targetDirectory.getAbsolutePath())) return;

        MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        addProgressListener(dialog);
        dialog.setVisible(true);
    }

    private static boolean confirmFileAction(
            String bundleKey, int size, String absolutePath) {
        return MessageDisplayer.confirm(null, bundleKey,
                MessageDisplayer.CancelButton.HIDE, size, absolutePath).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private synchronized static void addProgressListener(
            MoveToDirectoryDialog dialog) {

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

    private synchronized static void addProgressListener(
            CopyToDirectoryDialog dialog) {

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
                File sidecarFile =
                        XmpMetadata.getSidecarFileOfImageFileIfExists(imageFile);
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
