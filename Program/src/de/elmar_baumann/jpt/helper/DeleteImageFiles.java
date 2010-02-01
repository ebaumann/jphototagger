/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.filesystem.ControllerDeleteFiles;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.types.DeleteOption;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Deletes image files from the file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-19
 */
public final class DeleteImageFiles {

    /**
     * Deletes image files from the file system <strong>and</strong> their
     * sidecar files.
     *
     * @param  imageFiles  image files to delete
     * @param  options     options
     * @return all deleted files
     */
    public static List<File> delete(List<File> imageFiles, DeleteOption... options) {
        List<File>         deletedImageFiles = new ArrayList<File>(imageFiles.size());
        List<DeleteOption> optionList        = Arrays.asList(options);

        if (confirmDelete(optionList)) {
            List<Pair<File, File>> imageFilesWithSidecarFiles = XmpMetadata.getImageFilesWithSidecarFiles(imageFiles);
            for (Pair<File, File> filePair : imageFilesWithSidecarFiles) {
                File imageFile = filePair.getFirst();
                if (imageFile.delete()) {
                    deleteSidecarFile(filePair.getSecond(), optionList);
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
            AppLogger.logWarning(ControllerDeleteFiles.class,
                    "FileSystemDeleteImageFiles.Error.Delete",
                    file.getAbsolutePath());
        }
    }

    private static boolean confirmDelete(List<DeleteOption> options) {
        if (options.contains(DeleteOption.CONFIRM_DELETE)) {
            return MessageDisplayer.confirmYesNo(
                    null,
                    "FileSystemDeleteImageFiles.Confirm.Delete");
        }
        return true;
    }

    private DeleteImageFiles() {
    }
}
