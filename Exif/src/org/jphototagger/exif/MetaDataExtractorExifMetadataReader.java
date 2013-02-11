package org.jphototagger.exif;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jphototagger.lib.io.FileUtil;

/**
 * @author Elmar Baumann
 */
public final class MetaDataExtractorExifMetadataReader implements ExifTagsProvider {

    private static final Logger LOGGER = Logger.getLogger(MetaDataExtractorExifMetadataReader.class.getName());
    private static final Set<String> SUPPORTED_FILENAME_SUFFIXES_LOWERCASE = new HashSet<>();

    static {
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("orf");
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
        int oldTagCount = toExifTags.getTagCount();
//        try {
//            Metadata metadata = ImageMetadataReader.readMetadata(file);
//            for (Directory directory : metadata.getDirectories()) {
//                for (Tag tag : directory.getTags()) {
//                    ExifTag exifTag = new ExifTag(
//                            tag.getTagType(),
//                            -1,
//                            -1,
//                            0L,
//                            directory.getByteArray(tag.getTagType()),
//                            tag.toString(),
//                            18761,
//                            tag.getTagName(),
//                            ExifIfdType.EXIF
//                            );
//                    exifTags.addExifTag(exifTag);
//                }
//            }
//        } catch (Throwable t) {
//            LOGGER.log(Level.SEVERE, null, t);
//            throw new RuntimeException("Error while reading EXIF with MetaDataExtractor for file " + file);
//        }
        return toExifTags.getTagCount() - oldTagCount;
    }
}
