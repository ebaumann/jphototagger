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

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.Bundle;
import java.io.File;
import java.util.List;

/**
 * Sets the EXIF date of all known imagesfiles to XMP date created whithout time
 * stamp check.
 * <p>
 * Can also be used to set the EXIF date to the XMP date created of only one
 * file through {@link #set(java.lang.String, boolean)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-02
 */
public final class SetExifDateToXmpDateCreated extends HelperThread {

    private volatile boolean stop;
    private final    boolean replaceXmpDateCreated;

    /**
     *
     * @param replaceXmpDateCreated true, if a existing XMP creation date shall
     *                              be replaced with the EXIF date.
     *                              Default: false.
     */
    public SetExifDateToXmpDateCreated(boolean replaceXmpDateCreated) {
        this.replaceXmpDateCreated = replaceXmpDateCreated;
        setName("Refreshing EXIF in the database of known files @ " + getClass().getSimpleName());
        setInfo(Bundle.getString("RefreshExifInDbOfKnownFiles.Info"));
    }

    public SetExifDateToXmpDateCreated() {
        replaceXmpDateCreated = false;
    }

    @Override
    public void run() {

        List<String> filenames = DatabaseImageFiles.INSTANCE.getAllImageFiles();
        int          fileCount = filenames.size();

        progressStarted(0, 0, fileCount, fileCount > 0 ? filenames.get(0) : null);

        for (int i = 0; !stop && i < fileCount; i++) {

            String filename = filenames.get(i);
            set(filename, replaceXmpDateCreated);
            progressPerformed(i + 1, filename);
        }
        progressEnded(null);
    }

    /**
     * Sets the EXIF creation date of an image file to the XMP sidecar file and
     * updates the database.
     * <p>
     * If a XMP sidecar file does not exist, it will be created. If the image
     * file does not have EXIF metadata or the EXIF metadata does not have a
     * creation date, nothing will be done.
     *
     * @param filename              name of the image file
     * @param replaceXmpDateCreated true, if a existing XMP creation date shall
     *                              be replaced with the EXIF date
     */
    public static void set(String filename, boolean replaceXmpDateCreated) {
        Exif exif = ExifMetadata.getExif(new File(filename));
        Xmp xmp   = XmpMetadata.getXmpOfImageFile(filename);

        if (xmp == null) xmp = new Xmp();
        if (exif != null && exif.getDateTimeOriginal() != null) {
            if (isSet(xmp, replaceXmpDateCreated)) {
                xmp.setIptc4XmpCoreDateCreated(exif.getXmpDateCreated());
                String xmpFilename = XmpMetadata.suggestSidecarFilenameForImageFile(filename);
                if (XmpMetadata.writeMetadataToSidecarFile(xmpFilename, xmp)) {
                    ImageFile imageFile = new ImageFile();

                    xmp.setLastModified(new File(xmpFilename).lastModified());
                    imageFile.setFilename(filename);
                    imageFile.setXmp(xmp);
                    imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.XMP);

                    DatabaseImageFiles.INSTANCE.insertOrUpdateImageFile(imageFile);
                }
            }
        }
    }

    private static boolean isSet(Xmp xmp, boolean replaceXmpDateCreated) {
        return replaceXmpDateCreated || xmp.getIptc4XmpCoreDateCreated() == null;
    }

    @Override
    protected void stopRequested() {
        stop = true;
    }
}
