package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.DeleteOption;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Deletes image files from the file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/19
 */
public final class FileSystemDeleteImageFiles {

    /**
     * Deletes image files from the file system <strong>and</strong> their
     * sidecar files.
     * 
     * @param  imageFiles  image files to delete
     * @param  options     options
     * @return all deleted files
     */
    public static List<File> delete(List<File> imageFiles,
            EnumSet<DeleteOption> options) {
        List<File> deletedImageFiles = new ArrayList<File>(imageFiles.size());
        if (confirmDelete(options)) {
            List<Pair<File, File>> imageFilesWithSidecarFiles =
                    XmpMetadata.getImageFilesWithSidecarFiles(imageFiles);
            for (Pair<File, File> filePair : imageFilesWithSidecarFiles) {
                File imageFile = filePair.getFirst();
                if (imageFile.delete()) {
                    deleteSidecarFile(filePair.getSecond(), options);
                    deletedImageFiles.add(imageFile);
                } else {
                    errorMessageDelete(imageFile, options);
                }
            }
        }
        return deletedImageFiles;
    }

    private static void deleteSidecarFile(File sidecarFile,
            EnumSet<DeleteOption> options) {
        if (sidecarFile != null) {
            if (!sidecarFile.delete()) {
                errorMessageDelete(sidecarFile, options);
            }
        }
    }

    private static void errorMessageDelete(File file,
            EnumSet<DeleteOption> options) {
        if (options.contains(DeleteOption.MESSAGES_ON_FAILURES)) {
            AppLog.logWarning(ControllerDeleteFiles.class, Bundle.getString(
                    "FileSystemDeleteImageFiles.Error.Delete", // NOI18N
                    file.getAbsolutePath()));
        }
    }

    private static boolean confirmDelete(EnumSet<DeleteOption> options) {
        if (options.contains(DeleteOption.CONFIRM_DELETE)) {
            return MessageDisplayer.confirm(
                    "FileSystemDeleteImageFiles.Confirm.Delete", // NOI18N
                    MessageDisplayer.CancelButton.HIDE).equals(
                    MessageDisplayer.ConfirmAction.YES);
        }
        return true;
    }

    private FileSystemDeleteImageFiles() {
    }
}
