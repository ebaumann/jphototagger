package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.io.FileType;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Extracts EXIF metadata from images as {@link IdfEntryProxy} and
 * {@link Exif} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifMetadata {

    public enum ByteOrder {

        LITTLE_ENDIAN,
        BIG_ENDIAN
    }

    /**
     * Returns the EXIF metadata of a file.
     * 
     * @param  file  file
     * @return metadata oder null if errors occured
     */
    public static List<IdfEntryProxy> getMetadata(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        List<IdfEntryProxy> metadata = new ArrayList<IdfEntryProxy>();
        try {
            addIFDEntries(file, metadata);
        } catch (Exception ex) {
            AppLog.logWarning(ExifMetadata.class, ex);
        }
        return metadata;
    }

    private static void addIFDEntries(File file, List<IdfEntryProxy> metadata)
            throws IOException {
        ImageReader reader = null;
        if (FileType.isJpegFile(file.getName())) {
            reader = new JpegReader(file);
            IFDEntry[][] allEntries = MetadataUtils.getExif((JpegReader) reader);
            if (allEntries != null) {
                for (int i = 0; i < allEntries.length; i++) {
                    IFDEntry[] currentEntries = allEntries[i];
                    for (int j = 0; j < currentEntries.length; j++) {
                        metadata.add(new IdfEntryProxy(currentEntries[j]));
                    }
                }
            }
        } else {
            reader = new TiffReader(file);
            int count = ((TiffReader) reader).getIFDCount();
            for (int i = 0; i < count; i++) {
                addIfdEntriesOfDirectory(((TiffReader) reader).getIFD(i),
                        metadata);
            }
        }
        close(reader);
    }

    private static void close(ImageReader reader) {
        // causes exceptions e.g. in TableModelExif#setExifData(): entry.toString() ?
        if (reader != null) {
            reader.close();
        }
    }

    private static void addIfdEntriesOfDirectory(ImageFileDirectory ifd,
            List<IdfEntryProxy> metadata) {
        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (ifdEntry != null) {
                metadata.add(new IdfEntryProxy(ifdEntry));
            }
        }
        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory ifd0 = ifd.getIFDAt(i);
            addIfdEntriesOfDirectory(ifd0, metadata);
        }
        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addIfdEntriesOfDirectory(exifIFD, metadata);
        }
        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addIfdEntriesOfDirectory(gpsIFD, metadata);
        }
        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            addIfdEntriesOfDirectory(interoperabilityIFD, metadata);
        }
    }

    /**
     * Findet einen Entry mit bestimmten Tag.
     * 
     * @param entries Zu durchsuchende Entries
     * @param tag     Tag
     * @return        Erster passender Entry oder null, falls nicht gefunden
     */
    public static IdfEntryProxy findEntryWithTag(List<IdfEntryProxy> entries,
            int tag) {
        for (IdfEntryProxy entry : entries) {
            if (entry.getTag() == tag) {
                return entry;
            }
        }
        return null;
    }

    public static ExifGpsMetadata getGpsMetadata(List<IdfEntryProxy> entries) {
        ExifGpsMetadata data = new ExifGpsMetadata();

        setGpsLatitude(data, entries);
        setGpsLongitude(data, entries);
        setGpsAltitude(data, entries);

        return data;
    }

    private static void setGpsAltitude(ExifGpsMetadata data,
            List<IdfEntryProxy> entries) {
        IdfEntryProxy entryAltitudeRef = findEntryWithTag(entries,
                ExifTag.GPS_ALTITUDE_REF.getId());
        IdfEntryProxy entryAltitude = findEntryWithTag(entries,
                ExifTag.GPS_ALTITUDE.getId());
        if (entryAltitudeRef != null && entryAltitude != null) {
            data.setAltitude(new ExifGpsAltitude(entryAltitudeRef.getRawValue(),
                    entryAltitude.getRawValue(), entryAltitude.getByteOrder()));
        }
    }

    private static void setGpsLatitude(ExifGpsMetadata data,
            List<IdfEntryProxy> entries) {
        IdfEntryProxy entryLatitudeRef = findEntryWithTag(entries,
                ExifTag.GPS_LATITUDE_REF.getId());
        IdfEntryProxy entryLatitude = findEntryWithTag(entries,
                ExifTag.GPS_LATITUDE.getId());
        if (entryLatitudeRef != null && entryLatitude != null) {
            data.setLatitude(new ExifGpsLatitude(entryLatitudeRef.getRawValue(),
                    entryLatitude.getRawValue(), entryLatitude.getByteOrder()));
        }
    }

    private static void setGpsLongitude(ExifGpsMetadata data,
            List<IdfEntryProxy> entries) {
        IdfEntryProxy entryLongitudeRef = findEntryWithTag(entries,
                ExifTag.GPS_LONGITUDE_REF.getId());
        IdfEntryProxy entryLongitude = findEntryWithTag(entries,
                ExifTag.GPS_LONGITUDE.getId());
        if (entryLongitudeRef != null && entryLongitude != null) {
            data.setLongitude(new ExifGpsLongitude(
                    entryLongitudeRef.getRawValue(),
                    entryLongitude.getRawValue(), entryLongitude.getByteOrder()));
        }
    }

    /**
     * Returns the EXIF metadata of a file.
     * 
     * @param  file  file
     * @return EXIF metadata or null if errors occured
     */
    public static Exif getExif(File file) {
        Exif exif = null;
        List<IdfEntryProxy> exifEntries = getMetadata(file);
        if (exifEntries != null) {
            exif = new Exif();
            try {
                IdfEntryProxy dateTimeOriginalEntry = ExifMetadata.
                        findEntryWithTag(exifEntries,
                        ExifTag.DATE_TIME_ORIGINAL.getId());
                IdfEntryProxy focalLengthEntry = ExifMetadata.findEntryWithTag(
                        exifEntries, ExifTag.FOCAL_LENGTH.getId());
                IdfEntryProxy isoSpeedRatingsEntry = ExifMetadata.
                        findEntryWithTag(exifEntries, ExifTag.ISO_SPEED_RATINGS.
                        getId());
                IdfEntryProxy modelEntry = ExifMetadata.findEntryWithTag(
                        exifEntries, ExifTag.MODEL.getId());
                if (dateTimeOriginalEntry != null) {
                    setExifDateTimeOriginal(exif, dateTimeOriginalEntry);
                }
                if (focalLengthEntry != null) {
                    setExifFocalLength(exif, focalLengthEntry);
                }
                if (isoSpeedRatingsEntry != null) {
                    setExifIsoSpeedRatings(exif, isoSpeedRatingsEntry);
                }
                setExifEquipment(exif, modelEntry);
            } catch (Exception ex) {
                AppLog.logWarning(ExifMetadata.class, ex);
                exif = null;
            }
        }
        return exif;
    }

    private static void setExifDateTimeOriginal(Exif exifData,
            IdfEntryProxy entry) {
        String datestring = null;
        datestring = entry.toString(); // had thrown a null pointer exception
        if (datestring != null && datestring.length() >= 11) {
            try {
                int year = new Integer(datestring.substring(0, 4)).intValue();
                int month = new Integer(datestring.substring(5, 7)).intValue();
                int day = new Integer(datestring.substring(8, 10)).intValue();
                Calendar calendar = new GregorianCalendar();
                calendar.set(year, month - 1, day);
                try {
                    exifData.setDateTimeOriginal(new Date(calendar.
                            getTimeInMillis()));
                } catch (Exception ex) {
                    AppLog.logWarning(ExifMetadata.class, ex);
                }
            } catch (NumberFormatException ex) {
                AppLog.logWarning(ExifMetadata.class, ex);
            }
        }
    }

    private static void setExifEquipment(Exif exifData, IdfEntryProxy entry) {
        if (entry != null) {
            exifData.setRecordingEquipment(entry.toString().trim());
        }
    }

    private static void setExifFocalLength(Exif exifData, IdfEntryProxy entry) {
        try {
            String lengthString = entry.toString().trim();
            StringTokenizer tokenizer = new StringTokenizer(lengthString, "/:"); // NOI18N
            if (tokenizer.countTokens() >= 1) {
                String dividentString = tokenizer.nextToken();
                String divisorString = null;
                if (tokenizer.hasMoreTokens()) {
                    divisorString = tokenizer.nextToken();
                }
                Double divident = new Double(dividentString);
                double focalLength = divident;
                if (divisorString != null) {
                    Double divisor = new Double(divisorString);
                    focalLength = divident / divisor;
                }
                exifData.setFocalLength(focalLength);
            }
        } catch (Exception ex) {
            AppLog.logWarning(ExifMetadata.class, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exifData, IdfEntryProxy enry) {
        try {
            exifData.setIsoSpeedRatings(new Short(enry.toString().trim()).
                    shortValue());
        } catch (Exception ex) {
            AppLog.logWarning(ExifMetadata.class, ex);
        }
    }

    private ExifMetadata() {
    }
}
