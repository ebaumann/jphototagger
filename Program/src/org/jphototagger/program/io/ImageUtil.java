package org.jphototagger.program.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.api.storage.Preferences;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppStorageKeys;
import org.jphototagger.program.controller.filesystem.MoveFilesController;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.helper.CopyFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.CopyToDirectoryDialog;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Utilities for images.
 *
 * @author Elmar Baumann
 */
public final class ImageUtil {

    private ImageUtil() {
    }

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
     * Copies image files to a directory with the {@code CopyToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@code ConfirmOverwrite#YES} if the user must
     *                        confirm overwrite existing files
     */
    public static void copyImageFiles(List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        if (targetDirectory == null) {
            throw new NullPointerException("targetDirectory == null");
        }

        if (confirm == null) {
            throw new NullPointerException("confirm == null");
        }

        String message = Bundle.getString(ImageUtil.class, "ImageUtil.Confirm.Copy", sourceFiles.size(), targetDirectory.getAbsolutePath());

        if (confirm.yes() && !confirmFileAction(message)) {
            return;
        }

        CopyToDirectoryDialog dlg = new CopyToDirectoryDialog();

        dlg.setTargetDirectory(targetDirectory);
        dlg.setSourceFiles(sourceFiles);
        addProgressListener(dlg);
        dlg.copy(true, getCopyMoveFilesOptions());
    }

    private static CopyFiles.Options getCopyMoveFilesOptions() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppStorageKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES)
                ? CopyFiles.Options.fromInt(storage.getInt(AppStorageKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES))
                : CopyFiles.Options.CONFIRM_OVERWRITE;
    }

    /**
     * Moves image files to a directory with the {@code MoveToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@code ConfirmOverwrite#YES} if the user must
     *                        confirm overwrite existing files
     */
    public static void moveImageFiles(List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {
        String message = Bundle.getString(ImageUtil.class, "ImageUtil.Confirm.Move", sourceFiles.size(), targetDirectory.getAbsolutePath());

        if (confirm.yes() && !confirmFileAction(message)) {
            return;
        }

        MoveFilesController ctrl = ControllerFactory.INSTANCE.getController(MoveFilesController.class);

        if (ctrl != null) {
            ctrl.moveFiles(sourceFiles, targetDirectory);
            GUI.refreshThumbnailsPanel();
        }
    }

    private static boolean confirmFileAction(String message) {
        return MessageDisplayer.confirmYesNo(null, message);
    }

    private synchronized static void addProgressListener(CopyToDirectoryDialog dlg) {
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
                GUI.refreshThumbnailsPanel();
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
            if ((imageFile != null) && ImageFileFilterer.isImageFile(imageFile)) {
                files.add(imageFile);

                File sidecarFile = XmpMetadata.getSidecarFile(imageFile);

                if (sidecarFile != null) {
                    files.add(sidecarFile);
                }
            }
        }

        return files;
    }

    /**
     * Checks whether a sidecar file can be written for an image file, else
     * displays an error message.
     *
     * @param  imageFile image file
     * @return           true if a sidecar file can be written.
     */
    public static boolean checkImageEditable(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (!XmpMetadata.canWriteSidecarFileForImageFile(imageFile)) {
            String message = Bundle.getString(ImageUtil.class, "ImageUtil.Error.WriteSidecarFile", imageFile.getParentFile());
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }
}
