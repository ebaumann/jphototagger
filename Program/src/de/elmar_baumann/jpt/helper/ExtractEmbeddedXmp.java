/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.types.FileEditor;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.lib.image.metadata.xmp.XmpFileReader;
import de.elmar_baumann.lib.io.FileLock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Extracts in images embedded XMP metadata into sidecar files.
 *
 * @author  Elmar Baumann
 * @version 2009-05-22
 */
public final class ExtractEmbeddedXmp extends FileEditor {

    @Override
    public void edit(File file) {
        if (!IoUtil.lockLogWarning(file, this)) return;

        String sidecarFilename = XmpMetadata.getSidecarFilename(file.getAbsolutePath());
        File   sidecarFile     = sidecarFilename == null ? null : new File(sidecarFilename);

        if (sidecarFile != null && !confirmRemove(sidecarFile.getAbsolutePath())) return;

        writeSidecarFile(file);
        FileLock.INSTANCE.unlock(file, this);
    }

    private boolean confirmRemove(String absolutePath) {
        if (getConfirmOverwrite()) {
            return MessageDisplayer.confirmYesNo(null, "ExtractEmbeddedXmp.Confirm.Overwrite", absolutePath);
        }
        return true;
    }

    private void create(File file) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) throw new IOException("File couldn't be created: " + file);
        }
    }

    private void writeSidecarFile(File file) {
        String xmp = XmpFileReader.readFile(file.getAbsolutePath());
        FileOutputStream fos = null;
        if (xmp != null) {
            try {
                create(file);
                fos = new FileOutputStream(new File(
                        XmpMetadata.suggestSidecarFilename(file.getAbsolutePath())));
                fos.getChannel().lock();
                fos.write(xmp.getBytes());
                fos.flush();
                updateDatabase(file.getAbsolutePath());
            } catch (Exception ex) {
                AppLogger.logSevere(ExtractEmbeddedXmp.class, ex);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ex) {
                        AppLogger.logSevere(ExtractEmbeddedXmp.class, ex);
                    }
                }
            }
        }
    }

    private void updateDatabase(String imageFilename) {
        InsertImageFilesIntoDatabase insert = new InsertImageFilesIntoDatabase(
                                                      Arrays.asList(imageFilename),
                                                      Insert.XMP);
        insert.run(); // Shall run in this thread!
    }
}
