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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.resource.JptBundle;
import java.io.File;
import java.util.List;

/**
 * Refreshes the EXIF metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-02
 */
public final class RefreshExifInDbOfKnownFiles extends HelperThread {

    private volatile boolean stop;

    public RefreshExifInDbOfKnownFiles() {
        setName("Refreshing EXIF in the database of known files @ " + getClass().getSimpleName());
        setInfo(JptBundle.INSTANCE.getString("RefreshExifInDbOfKnownFiles.Info"));
    }

    @Override
    public void run() {

        DatabaseImageFiles db        = DatabaseImageFiles.INSTANCE;
        List<String>       filenames = db.getAllFilenames();
        int                fileCount = filenames.size();

        progressStarted(0, 0, fileCount, fileCount > 0 ? filenames.get(0) : null);

        for (int i = 0; !stop && i < fileCount; i++) {

            File imageFile = new File(filenames.get(i));
            Exif exif      = ExifMetadata.getExif(imageFile);

            if (exif != null) {
                db.insertOrUpdateExif(imageFile.getAbsolutePath(), exif);
            }
            progressPerformed(i + 1, imageFile);
        }
        progressEnded(null);
    }

    @Override
    protected void stopRequested() {
        stop = true;
    }
}
