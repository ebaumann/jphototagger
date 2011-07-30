package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.domain.exif.ExifTag;
import org.jphototagger.domain.exif.ExifIfdType;
import com.imagero.reader.ImageReader;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.tiff.EXIF;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.types.FileType;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.cache.ExifCache;

/**
 * Extracts EXIF metadata from images as {@link ExifTag} and
 * {@link EXIF} objects.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ExifMetadata {

    private static final Logger LOGGER = Logger.getLogger(ExifMetadata.class.getName());

    private ExifMetadata() {
    }

    /**
     * Returns {@link ExifTag} instances of an image file.
     *
     * @param  imageFile image file or null
     * @return           EXIF entries or null if errors occured
     */
    public static ExifTags getExifTags(File imageFile) {
        if ((imageFile == null) || !imageFile.exists()) {
            return null;
        }

        ExifTags exifTags = new ExifTags();

        try {
            LOGGER.log(Level.INFO, "Reading EXIF from image file ''{0}'', size {1} Bytes", new Object[]{imageFile, imageFile.length()});
            addExifTags(imageFile, exifTags);
        } catch (Exception ex) {
            Logger.getLogger(ExifMetadata.class.getName()).log(Level.SEVERE, null, ex);
        }

        ExifMakerNotesFactory.add(imageFile, exifTags);

        return exifTags;
    }

    private static void addExifTags(File imageFile, ExifTags exifTags) throws IOException {
        ImageReader imageReader = null;

        if (FileType.isJpegFile(imageFile.getName())) {
            AppLogger.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.JPEG.Info", imageFile);
            imageReader = new JpegReader(imageFile);
            addAllExifTags((JpegReader) imageReader, exifTags);
        } else {
            AppLogger.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.TIFF.Info", imageFile);
            imageReader = new TiffReader(imageFile);

            int count = ((TiffReader) imageReader).getIFDCount();

            // FIXME: IfdType.EXIF: How to determine the IFD type of an IFD
            // (using not IfdType.EXIF)?
            for (int i = 0; i < count; i++) {
                addTagsOfIfd(((TiffReader) imageReader).getIFD(i), ExifIfdType.EXIF, exifTags);
            }
        }

        close(imageReader);
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

    private static void close(ImageReader imageReader) {
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

        ExifTags exifTags = getExifTags(imageFile);

        return ExifFactory.getExif(exifTags);
    }

    public static Exif getCachedExif(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return ExifFactory.getExif(ExifCache.INSTANCE.getExifTags(imageFile));
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

        Exif exif = null;

        if (!AppFileFilters.INSTANCE.isUserDefinedFileType(imageFile)) {
            exif = getCachedExif(imageFile);
        }

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
