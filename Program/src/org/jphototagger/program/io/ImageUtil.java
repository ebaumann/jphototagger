package org.jphototagger.program.io;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.filesystem.ControllerMoveFiles;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.helper.FilesystemDatabaseUpdater;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.CopyToDirectoryDialog;
import org.jphototagger.program.view.dialogs.MoveToDirectoryDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for images.
 *
 * @author Elmar Baumann
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
     * Copies image files to a directory with the {@link CopyToDirectoryDialog}.
     *
     * @param sourceFiles     source files
     * @param targetDirectory target directory
     * @param confirm         {@link ConfirmOverwrite#YES} if the user must
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

        if (confirm.yes()
                &&!confirmFileAction("ImageUtil.Confirm.Copy", sourceFiles.size(), targetDirectory.getAbsolutePath())) {
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
    public static void moveImageFiles(List<File> sourceFiles, File targetDirectory, ConfirmOverwrite confirm) {
        if (confirm.yes()
                &&!confirmFileAction("ImageUtil.Confirm.Move", sourceFiles.size(), targetDirectory.getAbsolutePath())) {
            return;
        }

        ControllerMoveFiles ctrl = ControllerFactory.INSTANCE.getController(ControllerMoveFiles.class);

        if (ctrl != null) {
            ctrl.moveFiles(sourceFiles, targetDirectory);
            GUI.refreshThumbnailsPanel();
        }
    }

    private static boolean confirmFileAction(String bundleKey, int size, String absolutePath) {
        return MessageDisplayer.confirmYesNo(null, bundleKey, size, absolutePath);
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
     * @see              XmpMetadata#canWriteSidecarFileForImageFile(File)
     */
    public static boolean checkImageEditable(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (!XmpMetadata.canWriteSidecarFileForImageFile(imageFile)) {
            MessageDisplayer.error(null, "ImageUtil.Error.WriteSidecarFile", imageFile.getParentFile());

            return false;
        }

        return true;
    }
}
