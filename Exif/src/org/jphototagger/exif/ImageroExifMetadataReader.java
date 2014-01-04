package org.jphototagger.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.image.ImageFileType;
import org.jphototagger.lib.io.FileUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifTagsProvider.class, position = 0)
public final class ImageroExifMetadataReader implements ExifTagsProvider {

    private static final Logger LOGGER = Logger.getLogger(ImageroExifMetadataReader.class.getName());
    private static final Set<String> SUPPORTED_FILENAME_SUFFIXES_LOWERCASE = new HashSet<>();

    static {
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("arw");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("crw");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("cr2");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("dcr");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("dng");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("jpg");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("jpeg");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("mrw");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("nef");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("thm");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("tif");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("tiff");
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("srw");
    }

    @Override
    public boolean canReadExifTags(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();
        return SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    @Override
    public Set<String> getSupportedFilenameSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_FILENAME_SUFFIXES_LOWERCASE);
    }

    @Override
    public int addToExifTags(File fromFile, ExifTags toExifTags) throws Exception {
        ImageReader imageReader = null;
        int oldTagCount = toExifTags.getTagCount();
        try {
            if (ImageFileType.isJpegFile(fromFile.getName())) {
                LOGGER.log(Level.INFO, "Reading EXIF metadata of file ''{0}'''' with JPEG reader", fromFile);
                imageReader = new JpegReader(fromFile);
                addAllExifTags((JpegReader) imageReader, toExifTags);
            } else {
                LOGGER.log(Level.INFO, "Reading EXIF metadata of file ''{0}'''' with TIFF reader", fromFile);
                imageReader = new TiffReader(fromFile);
                TiffReader tiffReader = (TiffReader) imageReader;
                int count = tiffReader.getIFDCount();
                for (int i = 0; i < count; i++) {
                    ImageFileDirectory iFD = tiffReader.getIFD(i);
                    addTagsOfIfd(iFD, ExifIfd.EXIF, toExifTags);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Imagero can't read EXIF of image file '" + fromFile + "'", t);
        } finally {
            closeImageReader(imageReader);
        }
        return toExifTags.getTagCount() - oldTagCount;
    }

    private void addAllExifTags(JpegReader jpegReader, ExifTags exifTags) {
        IFDEntry[][] allIfdEntries = MetadataUtils.getExif(jpegReader);
        if (allIfdEntries != null) {
            for (IFDEntry[] currentIfdEntry : allIfdEntries) {
                for (IFDEntry entry : currentIfdEntry) {
                    ExifTag exifTag = new ExifTag(entry, ExifIfd.EXIF);
                    ExifTag.Properties exifTagId = exifTag.parseProperties();
                    if (exifTagId.isGpsTag()) {
                        exifTags.addGpsTag(new ExifTag(entry, ExifIfd.GPS));
                    } else if (exifTagId.isMakerNoteTag()) {
                        exifTags.addMakerNoteTag(new ExifTag(entry, ExifIfd.MAKER_NOTE));
                    } else {
                        exifTags.addExifTag(exifTag);
                    }
                }
            }
        }
    }

    private void closeImageReader(ImageReader imageReader) {
        if (imageReader != null) {
            imageReader.close();
        }
    }

    private void addTagsOfIfd(ImageFileDirectory ifd, ExifIfd ifdType, ExifTags exifTags) {
        if (!ifdType.equals(ExifIfd.UNDEFINED)) {
            addExifTags(ifd, ifdType, exifTags);
        }
        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);
            addTagsOfIfd(subIfd, ExifIfd.UNDEFINED, exifTags); // recursive
        }
        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addTagsOfIfd(exifIFD, ExifIfd.EXIF, exifTags); // recursive
        }
        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addTagsOfIfd(gpsIFD, ExifIfd.GPS, exifTags); // recursive
        }
        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            addTagsOfIfd(interoperabilityIFD, ExifIfd.INTEROPERABILITY, exifTags); // recursive
        }
    }

    private void addExifTags(ImageFileDirectory ifd, ExifIfd ifdType, ExifTags exifTags) {
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
}
