package org.jphototagger.program.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.types.DeleteOption;
import org.jphototagger.xmp.ImageFileSidecarFile;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Deletes image files from the file system.
 *
 * @author Elmar Baumann
 */
public final class DeleteImageFiles {

    private static final Logger LOGGER = Logger.getLogger(DeleteImageFiles.class.getName());

    /**
     * Deletes image files from the file system <strong>and</strong> their
     * sidecar files.
     *
     * @param  imageFiles  image files to delete
     * @param  options     options
     * @return all deleted files
     */
    public static List<File> delete(List<File> imageFiles, DeleteOption... options) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        List<File> deletedImageFiles = new ArrayList<File>(imageFiles.size());
        List<DeleteOption> optionList = Arrays.asList(options);

        if (confirmDelete(optionList)) {
            List<ImageFileSidecarFile> imageFilesWithSidecarFiles = XmpMetadata.getImageFilesWithSidecarFiles(imageFiles);

            for (ImageFileSidecarFile imageFileSidecarFile : imageFilesWithSidecarFiles) {
                File imageFile = imageFileSidecarFile.getImageFile();

                if (imageFile.delete()) {
                    deleteSidecarFile(imageFileSidecarFile.getSidecarFile(), optionList);
                    deletedImageFiles.add(imageFile);
                } else {
                    errorMessageDelete(imageFile, optionList);
                }
            }
        }

        return deletedImageFiles;
    }

    private static void deleteSidecarFile(File sidecarFile, List<DeleteOption> options) {
        if (sidecarFile != null) {
            if (!sidecarFile.delete()) {
                errorMessageDelete(sidecarFile, options);
            }
        }
    }

    private static void errorMessageDelete(File file, List<DeleteOption> options) {
        if (options.contains(DeleteOption.MESSAGES_ON_FAILURES)) {
            LOGGER.log(Level.WARNING, "File ''{0}'' couldn't be deleted!", file);
        }
    }

    private static boolean confirmDelete(List<DeleteOption> options) {
        if (options.contains(DeleteOption.CONFIRM_DELETE)) {
            return MessageDisplayer.confirmYesNo(null, "DeleteImageFiles.Confirm.Delete");
        }

        return true;
    }

    private DeleteImageFiles() {
    }
}
