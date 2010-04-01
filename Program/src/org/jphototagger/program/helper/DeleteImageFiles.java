/*
 * @(#)DeleteImageFiles.java    Created on 2009-02-19
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

package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.filesystem.ControllerDeleteFiles;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.types.DeleteOption;
import org.jphototagger.lib.generics.Pair;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Deletes image files from the file system.
 *
 * @author  Elmar Baumann
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
    public static List<File> delete(List<File> imageFiles,
                                    DeleteOption... options) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        List<File>         deletedImageFiles =
            new ArrayList<File>(imageFiles.size());
        List<DeleteOption> optionList        = Arrays.asList(options);

        if (confirmDelete(optionList)) {
            List<Pair<File, File>> imageFilesWithSidecarFiles =
                XmpMetadata.getImageFilesWithSidecarFiles(imageFiles);

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

    private static void deleteSidecarFile(File sidecarFile,
            List<DeleteOption> options) {
        if (sidecarFile != null) {
            if (!sidecarFile.delete()) {
                errorMessageDelete(sidecarFile, options);
            }
        }
    }

    private static void errorMessageDelete(File file,
            List<DeleteOption> options) {
        if (options.contains(DeleteOption.MESSAGES_ON_FAILURES)) {
            AppLogger.logWarning(ControllerDeleteFiles.class,
                                 "DeleteImageFiles.Error.Delete",
                                 file.getAbsolutePath());
        }
    }

    private static boolean confirmDelete(List<DeleteOption> options) {
        if (options.contains(DeleteOption.CONFIRM_DELETE)) {
            return MessageDisplayer.confirmYesNo(null,
                    "DeleteImageFiles.Confirm.Delete");
        }

        return true;
    }

    private DeleteImageFiles() {}
}
