/*
 * @(#)ExifMetadata.java    Created on 2008-10-05
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

package org.jphototagger.program.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.tiff.EXIF;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.types.FileType;

import java.io.File;
import java.io.IOException;

/**
 * Extracts EXIF metadata from images as {@link ExifTag} and
 * {@link EXIF} objects.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ExifMetadata {
    public enum IfdType {
        EXIF, GPS, INTEROPERABILITY, MAKER_NOTE, UNDEFINED,
        ;
    }

    private ExifMetadata() {}

    /**
     * Returns {@link ExifTag} instances of an image file.
     *
     * @param  imageFile image file or null
     * @return           EXIF entries or null if errors occured
     */
    public static ExifTags getExifTags(File imageFile) {
        if ((imageFile == null) ||!imageFile.exists()) {
            return null;
        }

        ExifTags exifTags = new ExifTags();

        try {
            addExifTags(imageFile, exifTags);
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }

        ExifMakerNotesFactory.add(imageFile, exifTags);

        return exifTags;
    }

    private static void addExifTags(File imageFile, ExifTags exifTags)
            throws IOException {
        ImageReader imageReader = null;

        if (FileType.isJpegFile(imageFile.getName())) {
            AppLogger.logInfo(ExifMetadata.class,
                              "ExifMetadata.AddIFDEntries.JPEG.Info",
                              imageFile);
            imageReader = new JpegReader(imageFile);
            addAllExifTags((JpegReader) imageReader, exifTags);
        } else {
            AppLogger.logInfo(ExifMetadata.class,
                              "ExifMetadata.AddIFDEntries.TIFF.Info",
                              imageFile);
            imageReader = new TiffReader(imageFile);

            int count = ((TiffReader) imageReader).getIFDCount();

            // FIXME: IfdType.EXIF: How to determine the IFD type of an IFD
            // (using not IfdType.EXIF)?
            for (int i = 0; i < count; i++) {
                addTagsOfIfd(((TiffReader) imageReader).getIFD(i),
                             IfdType.EXIF, exifTags);
            }
        }

        close(imageReader);
    }

    private static void addAllExifTags(JpegReader jpegReader,
                                       ExifTags exifTags) {
        IFDEntry[][] allIfdEntries = MetadataUtils.getExif(jpegReader);

        if (allIfdEntries != null) {
            for (int i = 0; i < allIfdEntries.length; i++) {
                IFDEntry[] currentIfdEntry = allIfdEntries[i];

                for (int j = 0; j < currentIfdEntry.length; j++) {
                    IFDEntry entry = currentIfdEntry[j];

                    exifTags.addExifTag(new ExifTag(entry, IfdType.EXIF));
                }
            }
        }
    }

    private static void close(ImageReader imageReader) {
        if (imageReader != null) {
            imageReader.close();
        }
    }

    private static void addTagsOfIfd(ImageFileDirectory ifd, IfdType ifdType,
                                     ExifTags exifTags) {
        if (!ifdType.equals(IfdType.UNDEFINED)) {
            addExifTags(ifd, ifdType, exifTags);
        }

        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);

            addTagsOfIfd(subIfd, IfdType.UNDEFINED, exifTags);    // recursive
        }

        ImageFileDirectory exifIFD = ifd.getExifIFD();

        if (exifIFD != null) {
            addTagsOfIfd(exifIFD, IfdType.EXIF, exifTags);    // recursive
        }

        ImageFileDirectory gpsIFD = ifd.getGpsIFD();

        if (gpsIFD != null) {
            addTagsOfIfd(gpsIFD, IfdType.GPS, exifTags);    // recursive
        }

        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();

        if (interoperabilityIFD != null) {
            addTagsOfIfd(interoperabilityIFD, IfdType.INTEROPERABILITY,
                         exifTags);    // recursive
        }
    }

    private static void addExifTags(ImageFileDirectory ifd, IfdType ifdType,
                                    ExifTags exifTags) {
        int entryCount = ifd.getEntryCount();

        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);

            if (ifdEntry != null) {
                ExifTag exifTag = new ExifTag(ifdEntry, ifdType);

                switch (ifdType) {
                case EXIF :
                    exifTags.addExifTag(exifTag);

                    break;

                case GPS :
                    exifTags.addGpsTag(exifTag);

                    break;

                case INTEROPERABILITY :
                    exifTags.addInteroperabilityTag(exifTag);

                    break;

                case MAKER_NOTE :
                    exifTags.addMakerNoteTag(exifTag);

                    break;

                default :
                    assert(false);
                }
            }
        }
    }

    /**
     * Returns EXIF metadata of a image file.
     *
     * @param  imageFile image file
     * @return           EXIF metadata or null if errors occured
     */
    public static Exif getExif(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return ExifFactory.getExif(imageFile);
    }

    /**
     * Returns the milliseconds since 1970 of the time when the image was taken.
     * <p>
     * Reads the EXIF information of the file.
     *
     * @param  imageFile image file
     * @return milliseconds. If the image file has no EXIF metadata or no
     *         date time original information whithin the EXIF metadata the last
     *         modification time of the file will be returned
     */
    public static long timestampDateTimeOriginal(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        Exif exif = getExif(imageFile);

        if ((exif == null) || (exif.getDateTimeOriginal() == null)) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }

    /**
     * Returns the milliseconds since 1970 of the time when the image was taken.
     * <p>
     * Gets the EXIF information from the database. If in the database is no
     * EXIF information, the file's timestamp will be used, regardless whether
     * the file contains EXIF information.
     *
     * @param  imageFile image file
     * @return milliseconds. If the image file has no EXIF metadata or no
     *         date time original information whithin the EXIF metadata the last
     *         modification time of the file will be returned
     */
    public static long timestampDateTimeOriginalDb(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        Exif exif = DatabaseImageFiles.INSTANCE.getExifOfImageFile(imageFile);

        if ((exif == null) || (exif.getDateTimeOriginal() == null)) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }
}
