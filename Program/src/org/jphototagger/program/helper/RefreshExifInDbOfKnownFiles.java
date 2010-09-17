/*
 * @(#)RefreshExifInDbOfKnownFiles.java    Created on 2010-01-02
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.util.List;

/**
 * Refreshes the EXIF metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author Elmar Baumann
 */
public final class RefreshExifInDbOfKnownFiles extends HelperThread {
    private volatile boolean cancel;

    public RefreshExifInDbOfKnownFiles() {
        super("JPhotoTagger: Refreshing EXIF in the database of known files");
        setInfo(
            JptBundle.INSTANCE.getString("RefreshExifInDbOfKnownFiles.Info"));
    }

    @Override
    public void run() {
        DatabaseImageFiles db         = DatabaseImageFiles.INSTANCE;
        List<File>         imageFiles = db.getAllImageFiles();
        int                fileCount  = imageFiles.size();

        progressStarted(0, 0, fileCount, (fileCount > 0)
                                         ? imageFiles.get(0)
                                         : null);

        for (int i = 0; !cancel &&!isInterrupted() && (i < fileCount); i++) {
            File imageFile = imageFiles.get(i);
            Exif exif      = ExifMetadata.getExif(imageFile);

            if (exif != null) {
                db.insertOrUpdateExif(imageFile, exif);
            }

            progressPerformed(i + 1, imageFile);
        }

        progressEnded(null);
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
