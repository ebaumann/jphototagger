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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sets EXIF metadata to XMP whithout time stamp check, currently only the date
 * created.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-02
 */
public final class SetExifToXmp extends HelperThread {

    private volatile boolean      stop;
    private          List<String> files;
    private final    boolean      replaceExistingXmpData;

    /**
     * Checks all known image files.
     *
     * @param replaceExistingXmpData true, if existing XMP metadata shall
     *                               be replaced with EXIF metadata.
     *                               Default: false.
     */
    public SetExifToXmp(boolean replaceExistingXmpData) {
        this.replaceExistingXmpData = replaceExistingXmpData;
        setInfo();
    }

    /**
     * Checks all known image files.
     *
     * @param filenames              names of files to process instead of
     *                               processing all known files
     * @param replaceExistingXmpData true, if existing XMP metadata shall
     *                               be replaced with EXIF metadata.
     *                               Default: false.
     */
    public SetExifToXmp(Collection<String> filenames, boolean replaceExistingXmpData) {
        this.replaceExistingXmpData = replaceExistingXmpData;
        this.files                  = new ArrayList<String>(filenames);
        setInfo();
    }

    /**
     * Checks all known image files and does not replace existing XMP data.
     */
    public SetExifToXmp() {
        replaceExistingXmpData = false;
        setInfo();
    }

    private void setInfo() {
        setName("Setting EXIF metadata to XMP metadata @ " + getClass().getSimpleName());
        setInfo(Bundle.getString("SetExifToXmp.Info"));
    }

    @Override
    public void run() {

        List<String> filenames = files == null ? DatabaseImageFiles.INSTANCE.getAllFilenames() : files;
        int          fileCount = filenames.size();

        progressStarted(0, 0, fileCount, fileCount > 0 ? filenames.get(0) : null);

        for (int i = 0; !stop && i < fileCount; i++) {

            String filename = filenames.get(i);
            set(filename, replaceExistingXmpData);
            progressPerformed(i + 1, filename);
        }
        progressEnded(null);
    }

    /**
     * Sets the EXIF metadata of an image file to the XMP sidecar file and
     * updates the database.
     * <p>
     * If a XMP sidecar file does not exist, it will be created. If the image
     * file does not have EXIF metadata or the EXIF metadata does not have a
     * settable vale, nothing will be done.
     *
     * @param filename               name of the image file
     * @param replaceExistingXmpData true, if existing XMP metadata shall
     *                               be replaced with EXIF metadata.
     *                               Default: false.
     */
    public static void set(String filename, boolean replaceExistingXmpData) {
        Exif exif = ExifMetadata.getExif(new File(filename));
        Xmp  xmp  = XmpMetadata.getXmpFromSidecarFileOf(filename);

        if (xmp == null) xmp = new Xmp();
        if (exif != null && exifHasValues(exif)) {
            if (isSet(xmp, replaceExistingXmpData)) {
                setDateCreated(xmp, exif);
                String xmpFilename = XmpMetadata.suggestSidecarFilename(filename);
                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFilename)) {
                    ImageFile imageFile = new ImageFile();

                    xmp.setLastModified(new File(xmpFilename).lastModified());
                    imageFile.setLastmodified(new File(filename).lastModified()); // Avoids re-reading thumbnails
                    imageFile.setFilename(filename);
                    imageFile.setXmp(xmp);
                    imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.XMP);

                    DatabaseImageFiles.INSTANCE.insertOrUpdate(imageFile);
                }
            }
        }
    }

    private static boolean isSet(Xmp xmp, boolean replaceExistingXmpData) {
        return replaceExistingXmpData ||
               xmp.getIptc4XmpCoreDateCreated() == null;
    }

    public static boolean exifHasValues(Exif exif) {
        return exif.getDateTimeOriginal() != null;
    }

    public static void setDateCreated(Xmp xmp, Exif exif) {
        if (exif.getDateTimeOriginal() != null) {
            xmp.setIptc4XmpCoreDateCreated(exif.getXmpDateCreated());
        }
    }

    @Override
    protected void stopRequested() {
        stop = true;
    }
}
