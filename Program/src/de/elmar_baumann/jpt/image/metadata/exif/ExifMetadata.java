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
package de.elmar_baumann.jpt.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.types.FileType;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Extracts EXIF metadata from images as {@link IfdEntryProxy} and
 * {@link Exif} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifMetadata {

    /**
     * Returns {@link IfdEntryProxy} instances of an image file.
     * 
     * @param  imageFile image file
     * @return           EXIF entries or null if errors occured
     */
    public static List<IfdEntryProxy> getExifEntries(File imageFile) {

        if (imageFile == null || !imageFile.exists()) return null;

        List<IfdEntryProxy> entries = new ArrayList<IfdEntryProxy>();
        try {
            addEntries(imageFile, entries);
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
        return entries;
    }

    private static void addEntries(File imageFile, List<IfdEntryProxy> entries) throws IOException {
        ImageReader reader = null;
        if (FileType.isJpegFile(imageFile.getName())) {
            AppLog.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.JPEG.Info", imageFile);
            reader = new JpegReader(imageFile);
            addAllEntries((JpegReader) reader, entries);
        } else {
            AppLog.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.TIFF.Info", imageFile);
            reader = new TiffReader(imageFile);
            int count = ((TiffReader) reader).getIFDCount();
            for (int i = 0; i < count; i++) {
                addAllEntries(((TiffReader) reader).getIFD(i), entries);
            }
        }
        close(reader);
    }

    private static void addAllEntries(JpegReader reader, List<IfdEntryProxy> entries) {
        IFDEntry[][] allEntries = MetadataUtils.getExif(reader);
        if (allEntries != null) {
            for (int i = 0; i < allEntries.length; i++) {
                IFDEntry[] currentEntries = allEntries[i];
                for (int j = 0; j < currentEntries.length; j++) {
                    entries.add(new IfdEntryProxy(currentEntries[j]));
                }
            }
        }
    }

    private static void close(ImageReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    private static void addAllEntries(ImageFileDirectory ifd, List<IfdEntryProxy> entries) {

        addEntries(ifd, entries);

        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);
            addAllEntries(subIfd, entries); // recursive
        }

        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addAllEntries(exifIFD, entries); // recursive
        }

        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addAllEntries(gpsIFD, entries); // recursive
        }

        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            addAllEntries(interoperabilityIFD, entries); // recursive
        }
    }

    private static void addEntries(ImageFileDirectory ifd, List<IfdEntryProxy> entries) {
        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (ifdEntry != null) {
                entries.add(new IfdEntryProxy(ifdEntry));
            }
        }
    }

    /**
     * Finds in a list an EXIF entry of a specific EXIF tag value.
     * 
     * @param entries  EXIF Entries to look in
     * @param tagValue tag value as defined in the EXIF standard
     * @return         first matching Entry or null if not found
     */
    public static IfdEntryProxy getEntry(List<IfdEntryProxy> entries, int tagValue) {

        for (IfdEntryProxy entry : entries) {
            if (entry.tagId() == tagValue) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Returns EXIF metadata of a image file.
     * 
     * @param  imageFile image file
     * @return           EXIF metadata or null if errors occured
     */
    public static Exif getExif(File imageFile) {

        Exif                exif    = null;
        List<IfdEntryProxy> entries = getExifEntries(imageFile);

        if (entries != null) {
            exif = new Exif();
            try {
                IfdEntryProxy dateTimeOriginal = getEntry(ExifTag.DATE_TIME_ORIGINAL, entries);
                IfdEntryProxy focalLength      = getEntry(ExifTag.FOCAL_LENGTH      , entries);
                IfdEntryProxy isoSpeedRatings  = getEntry(ExifTag.ISO_SPEED_RATINGS , entries);
                IfdEntryProxy model            = getEntry(ExifTag.MODEL             , entries);

                if (dateTimeOriginal != null) {
                    setExifDateTimeOriginal(exif, dateTimeOriginal);
                }
                if (focalLength != null) {
                    setExifFocalLength(exif, focalLength);
                }
                if (isoSpeedRatings != null) {
                    setExifIsoSpeedRatings(exif, isoSpeedRatings);
                }
                setExifEquipment(exif, model);
            } catch (Exception ex) {
                AppLog.logSevere(ExifMetadata.class, ex);
                exif = null;
            }
        }
        return exif;
    }

    private static IfdEntryProxy getEntry(ExifTag tag, List<IfdEntryProxy> entries) {
        return ExifMetadata.getEntry(entries, tag.tagId());
    }

    private static void setExifDateTimeOriginal(Exif exifData, IfdEntryProxy entry) {

        String datestring = entry.toString(); // did throw a null pointer exception

        if (datestring != null && datestring.trim().length() >= 11) {
            try {
                int      year     = new Integer(datestring.substring(0, 4)).intValue();
                int      month    = new Integer(datestring.substring(5, 7)).intValue();
                int      day      = new Integer(datestring.substring(8, 10)).intValue();
                Calendar calendar = new GregorianCalendar();

                calendar.set(year, month - 1, day);
                try {
                    exifData.setDateTimeOriginal(new Date(calendar.getTimeInMillis()));
                } catch (Exception ex) {
                    AppLog.logSevere(ExifMetadata.class, ex);
                }
            } catch (NumberFormatException ex) {
                AppLog.logSevere(ExifMetadata.class, ex);
            }
        }
    }

    private static void setExifEquipment(Exif exifData, IfdEntryProxy entry) {
        if (entry != null) {
            exifData.setRecordingEquipment(entry.toString().trim());
        }
    }

    private static void setExifFocalLength(Exif exifData, IfdEntryProxy entry) {
        try {
            String          length    = entry.toString().trim();
            StringTokenizer tokenizer = new StringTokenizer(length, "/:");

            if (tokenizer.countTokens() >= 1) {
                String denominatorString = tokenizer.nextToken();
                String numeratorString   = null;
                if (tokenizer.hasMoreTokens()) {
                    numeratorString = tokenizer.nextToken();
                }
                double denominator = Double.valueOf(denominatorString);
                double focalLength = denominator;
                if (numeratorString != null) {
                    double numerator = new Double(numeratorString);
                    focalLength      = denominator / numerator;
                }
                exifData.setFocalLength(focalLength);
            }
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exifData, IfdEntryProxy enry) {
        try {
            exifData.setIsoSpeedRatings(new Short(enry.toString().trim()).shortValue());
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
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

        Exif exif = getExif(imageFile);

        if (exif == null || exif.getDateTimeOriginal() == null) {
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

        Exif exif = DatabaseImageFiles.INSTANCE.getExifOfFile(imageFile.getAbsolutePath());

        if (exif == null || exif.getDateTimeOriginal() == null) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }

    private ExifMetadata() {
    }
}
