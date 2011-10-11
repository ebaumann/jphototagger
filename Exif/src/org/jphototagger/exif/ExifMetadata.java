package org.jphototagger.exif;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;


import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.image.ImageFileType;
import org.jphototagger.lib.util.NumberUtil;

/**
 * Extracts EXIF metadata from images as {@code ExifTag} and
 * {@code EXIF} objects.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ExifMetadata {

    private static final Logger LOGGER = Logger.getLogger(ExifMetadata.class.getName());

    private ExifMetadata() {
    }

    static ExifTags getExifTags(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            return null;
        }

        ExifTags exifTags = new ExifTags();

        try {
            LOGGER.log(Level.INFO, "Reading EXIF from image file ''{0}'', size {1} Bytes", new Object[]{imageFile, imageFile.length()});
            addExifTags(imageFile, exifTags);
        } catch (Exception ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.SEVERE, null, ex);
        }

        ExifMakerNotesAdder.addMakerNotesToExifTags(imageFile, exifTags);

        return exifTags;
    }

    private static void addExifTags(File imageFile, ExifTags exifTags) throws IOException {
        ImageReader imageReader = null;

        if (ImageFileType.isJpegFile(imageFile.getName())) {
            LOGGER.log(Level.INFO, "Reading EXIF metadata of file ''{0}'''' with JPEG reader", imageFile);
            imageReader = new JpegReader(imageFile);
            addAllExifTags((JpegReader) imageReader, exifTags);
        } else {
            LOGGER.log(Level.INFO, "Reading EXIF metadata of file ''{0}'''' with TIFF reader", imageFile);
            imageReader = new TiffReader(imageFile);
            TiffReader tiffReader = (TiffReader) imageReader;
            int count = tiffReader.getIFDCount();

            for (int i = 0; i < count; i++) {
                // FIXME: IfdType.EXIF: How to determine the IFD type of an IFD (using not IfdType.EXIF)?
                ImageFileDirectory iFD = tiffReader.getIFD(i);
                addTagsOfIfd(iFD, ExifIfdType.EXIF, exifTags);
            }
        }

        closeImageReader(imageReader);
    }

    private static void addAllExifTags(JpegReader jpegReader, ExifTags exifTags) {
        IFDEntry[][] allIfdEntries = MetadataUtils.getExif(jpegReader);

        if (allIfdEntries != null) {
            for (int i = 0; i < allIfdEntries.length; i++) {
                IFDEntry[] currentIfdEntry = allIfdEntries[i];

                for (int j = 0; j < currentIfdEntry.length; j++) {
                    IFDEntry entry = currentIfdEntry[j];
                    ExifTag exifTag = new ExifTag(entry, ExifIfdType.EXIF);
                    ExifTag.Id exifTagId = exifTag.convertTagIdToEnumId();

                    if (exifTagId.isGpsId()) {
                        exifTags.addGpsTag(new ExifTag(entry, ExifIfdType.GPS));
                    } else if (exifTagId.isMakerNoteId()) {
                        exifTags.addMakerNoteTag(new ExifTag(entry, ExifIfdType.MAKER_NOTE));
                    } else {
                        exifTags.addExifTag(exifTag);
                    }
                }
            }
        }
    }

    private static void closeImageReader(ImageReader imageReader) {
        if (imageReader != null) {
            imageReader.close();
        }
    }

    private static void addTagsOfIfd(ImageFileDirectory ifd, ExifIfdType ifdType, ExifTags exifTags) {
        if (!ifdType.equals(ExifIfdType.UNDEFINED)) {
            addExifTags(ifd, ifdType, exifTags);
        }

        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);

            addTagsOfIfd(subIfd, ExifIfdType.UNDEFINED, exifTags);    // recursive
        }

        ImageFileDirectory exifIFD = ifd.getExifIFD();

        if (exifIFD != null) {
            addTagsOfIfd(exifIFD, ExifIfdType.EXIF, exifTags);    // recursive
        }

        ImageFileDirectory gpsIFD = ifd.getGpsIFD();

        if (gpsIFD != null) {
            addTagsOfIfd(gpsIFD, ExifIfdType.GPS, exifTags);    // recursive
        }

        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();

        if (interoperabilityIFD != null) {
            addTagsOfIfd(interoperabilityIFD, ExifIfdType.INTEROPERABILITY, exifTags);    // recursive
        }
    }

    private static void addExifTags(ImageFileDirectory ifd, ExifIfdType ifdType, ExifTags exifTags) {
        int entryCount = ifd.getEntryCount();

        for (int i = 0; i < entryCount; i++) {
            IFDEntry ifdEntry = ifd.getEntryAt(i);

            if (ifdEntry != null) {
                ExifTag exifTag = new ExifTag(ifdEntry, ifdType);

                switch (ifdType) {
                    case EXIF:
                        exifTags.addExifTag(exifTag);

                        break;

                    case GPS:
                        exifTags.addGpsTag(exifTag);

                        break;

                    case INTEROPERABILITY:
                        exifTags.addInteroperabilityTag(exifTag);

                        break;

                    case MAKER_NOTE:
                        exifTags.addMakerNoteTag(exifTag);

                        break;

                    default:
                        assert (false);
                }
            }
        }
    }

    static Exif getExif(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        ExifTags exifTags = getExifTags(imageFile);

        return ExifFactory.getExif(exifTags);
    }

    static Exif getExifPreferCached(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return ExifFactory.getExif(getExifTagsPreferCached(imageFile));
    }

    /**
     * Returns EXIF tags of an image file from the cache if up to date. If the
     * tags are not up to date, they will be created from the image file and cached.
     *
     * @param  imageFile image file
     * @return           tags or null if the tags neither in the cache nor could be
     *                   created from the image file
     */
    public static ExifTags getExifTagsPreferCached(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (ExifCache.INSTANCE.containsUpToDateExifTags(imageFile)) {
            return ExifCache.INSTANCE.getCachedExifTags(imageFile);
        } else {
            ExifTags exifTags = null;

            if (ExifSupport.INSTANCE.canReadExif(imageFile)) {
                exifTags = getExifTags(imageFile);
            }

            if (exifTags == null) {
                ExifCache.INSTANCE.cacheExifTags(imageFile, new ExifTags());
            } else {
                ExifCache.INSTANCE.cacheExifTags(imageFile, exifTags);
            }

            return exifTags;
        }
    }

    static long getTimeTakenInMillis(File file) {
        ExifTags exifTags = getExifTags(file);

        if (exifTags == null) {
            return file.lastModified();
        }

        ExifTag dateTimeOriginalTag = exifTags.findExifTagByTagId(ExifTag.Id.DATE_TIME_ORIGINAL.getTagId());

        if (dateTimeOriginalTag == null) {
            return file.lastModified();
        }

        String dateTimeString = dateTimeOriginalTag.getStringValue().trim();
        int dateTimeStringLength = dateTimeString.length();

        if (dateTimeStringLength < 19) {
            return file.lastModified();
        }

        long timestamp = exifDateTimeStringToTimestamp(dateTimeString);

        return timestamp < 0 ? file.lastModified() : timestamp;
    }

    static long exifDateTimeStringToTimestamp(String dateTimeString) {
        if (dateTimeString.length() < 19) {
            return -1;
        }

        try {
            String yearString = dateTimeString.substring(0, 4);
            String monthString = dateTimeString.substring(5, 7);
            String dayString = dateTimeString.substring(8, 10);
            String hoursString = dateTimeString.substring(11, 13);
            String minutesString = dateTimeString.substring(14, 16);
            String secondsString = dateTimeString.substring(17, 19);

            if (!NumberUtil.allStringsAreIntegers(Arrays.asList(yearString, monthString, dayString, hoursString, minutesString, secondsString))) {
                return -1;
            }

            int year = Integer.parseInt(yearString);
            int month = Integer.parseInt(monthString);
            int day = Integer.parseInt(dayString);
            int hours = Integer.parseInt(hoursString);
            int minutes = Integer.parseInt(minutesString);
            int seconds = Integer.parseInt(secondsString);

            Calendar calendar = new GregorianCalendar();

            if (year < 1839) {
                LOGGER.log(Level.WARNING, "Year {0} is not plausible and EXIF date time taken will not be set!", year);
                return -1;
            }

            calendar.set(year, month - 1, day, hours, minutes, seconds);

            return calendar.getTimeInMillis();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
