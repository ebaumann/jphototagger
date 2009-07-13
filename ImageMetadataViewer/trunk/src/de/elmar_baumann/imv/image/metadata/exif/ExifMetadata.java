package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.types.FileType;
import de.elmar_baumann.imv.resource.Bundle;
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
 * @version 2008/10/05
 */
public final class ExifMetadata {

    /**
     * Returns {@link IdfEntryProxy} instances of an image file.
     * 
     * @param  imageFile image file
     * @return           EXIF entries or null if errors occured
     */
    public static List<IdfEntryProxy> getExifEntries(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            return null;
        }
        List<IdfEntryProxy> exifEntries = new ArrayList<IdfEntryProxy>();
        try {
            addIFDEntries(imageFile, exifEntries);
        } catch (Exception ex) {
            AppLog.logWarning(ExifMetadata.class, ex);
        }
        return exifEntries;
    }

    private static void addIFDEntries(
            File imageFile, List<IdfEntryProxy> exifEntries) throws IOException {
        ImageReader reader = null;
        if (FileType.isJpegFile(imageFile.getName())) {
            AppLog.logInfo(ExifMetadata.class, Bundle.getString(
                    "ExifMetadata.AddIFDEntries.JPEG.Info", imageFile)); // NOI18N
            reader = new JpegReader(imageFile);
            IFDEntry[][] allEntries = MetadataUtils.getExif((JpegReader) reader);
            if (allEntries != null) {
                for (int i = 0; i < allEntries.length; i++) {
                    IFDEntry[] currentEntries = allEntries[i];
                    for (int j = 0; j < currentEntries.length; j++) {
                        exifEntries.add(new IdfEntryProxy(currentEntries[j]));
                    }
                }
            }
        } else {
            AppLog.logInfo(ExifMetadata.class, Bundle.getString(
                    "ExifMetadata.AddTIFFEntries.JPEG.Info", imageFile)); // NOI18N
            reader = new TiffReader(imageFile);
            int count = ((TiffReader) reader).getIFDCount();
            for (int i = 0; i < count; i++) {
                addIfdEntriesOfDirectory(((TiffReader) reader).getIFD(i),
                        exifEntries);
            }
        }
        close(reader);
    }

    private static void close(ImageReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    private static void addIfdEntriesOfDirectory(
            ImageFileDirectory ifd, List<IdfEntryProxy> exifEntries) {
        int entryCount = ifd.getEntryCount();
        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);
            if (ifdEntry != null) {
                exifEntries.add(new IdfEntryProxy(ifdEntry));
            }
        }
        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory ifd0 = ifd.getIFDAt(i);
            addIfdEntriesOfDirectory(ifd0, exifEntries);
        }
        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addIfdEntriesOfDirectory(exifIFD, exifEntries);
        }
        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addIfdEntriesOfDirectory(gpsIFD, exifEntries);
        }
        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            addIfdEntriesOfDirectory(interoperabilityIFD, exifEntries);
        }
    }

    /**
     * Finds in a list an EXIF entry of a specific EXIF tag value.
     * 
     * @param entries  EXIF Entries to look in
     * @param tagValue tag value as defined in the EXIF standard
     * @return         first matching Entry or null if not found
     */
    public static IdfEntryProxy findEntryWithTag(
            List<IdfEntryProxy> entries, int tagValue) {
        for (IdfEntryProxy entry : entries) {
            if (entry.getTag() == tagValue) {
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
        Exif exif = null;
        List<IdfEntryProxy> exifEntries = getExifEntries(imageFile);
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
        if (datestring != null && datestring.trim().length() >= 11) {
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
