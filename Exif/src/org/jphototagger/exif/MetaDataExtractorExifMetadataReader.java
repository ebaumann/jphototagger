package org.jphototagger.exif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.io.File;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.exif.ExifTag.Properties;
import org.jphototagger.exif.datatype.ExifValueType;
import org.jphototagger.exif.datatype.ExifValueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.ByteUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifTagsProvider.class, position = 100)
public final class MetaDataExtractorExifMetadataReader implements ExifTagsProvider {

    private static final Logger LOGGER = Logger.getLogger(MetaDataExtractorExifMetadataReader.class.getName());
    private static final Set<String> SUPPORTED_FILENAME_SUFFIXES_LOWERCASE = new HashSet<>();
    private static final Map<String, Integer> BYTE_ORDER_OF_FILENAME_SUFFIX = new HashMap<>(); //18761 == little endian, 19789 == big endian

    static {
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("orf");
        BYTE_ORDER_OF_FILENAME_SUFFIX.put("orf", 18761);
        SUPPORTED_FILENAME_SUFFIXES_LOWERCASE.add("dng");
        BYTE_ORDER_OF_FILENAME_SUFFIX.put("dng", 18761);
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
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(fromFile);
            String suffix = FileUtil.getSuffix(fromFile).toLowerCase();
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    int tagId = tag.getTagType();
                    Properties exifProperties = ExifTag.Properties.parseInt(tagId);
                    if (exifProperties != ExifTag.Properties.UNKNOWN) {
                        int byteOrderValue = BYTE_ORDER_OF_FILENAME_SUFFIX.get(suffix);
                        ByteOrder byteOrder = byteOrderValue == 0x4949 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
                        byte[] rawValue = getRawValue(directory, exifProperties, byteOrder);
                        if (rawValue != null) {
                            if (exifProperties.getValueType() == ExifValueType.ASCII && !isNullTerminated(rawValue)) {
                                rawValue = terminateWithNull(rawValue);
                            }
                            ExifTag exifTag = new ExifTag(
                                    tagId,
                                    exifProperties.getValueType().getIntValue(),
                                    exifProperties.getValueCount(),
                                    -1L,
                                    rawValue,
                                    tag.toString(), byteOrderValue,
                                    tag.getTagName(),
                                    ExifIfd.EXIF
                                    );
                            toExifTags.addExifTag(exifTag);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            throw new RuntimeException("Error while reading EXIF with MetaDataExtractor for file " + fromFile);
        }
        return toExifTags.getTagCount() - oldTagCount;
    }

    private byte[] getRawValue(Directory directory, Properties exifProperties, ByteOrder byteOrder) {
        ExifValueType valueType = exifProperties.getValueType();
        int tagId = exifProperties.getTagId();
        Object object = directory.getObject(tagId);
        if (valueType == ExifValueType.ASCII && object instanceof String) {
            byte[] rawValue = directory.getByteArray(tagId);
            if (rawValue != null && !isNullTerminated(rawValue)) {
                return terminateWithNull(rawValue);
            }
            return null;
        } else if (valueType == ExifValueType.SHORT && object instanceof Integer) {
            return ExifValueUtil.createRawValue(((Integer) object).longValue(), byteOrder, ExifValueType.SHORT.getBitCount() / 8);
        } else if ((valueType == ExifValueType.LONG  || valueType == ExifValueType.SHORT_OR_LONG) && object instanceof Long) {
            return ExifValueUtil.createRawValue((Long) object, byteOrder, ExifValueType.LONG.getBitCount() / 8);
        } else if (valueType == ExifValueType.RATIONAL && object instanceof com.drew.lang.Rational) {
            com.drew.lang.Rational rational = (com.drew.lang.Rational) object;
            long numerator = rational.getNumerator();
            long denominator = rational.getDenominator();
            int longByteCount = ExifValueType.LONG.getBitCount() / 8;
            byte[] numeratorBytes = ExifValueUtil.createRawValue(numerator, byteOrder, longByteCount);
            byte[] denominatorBytes = ExifValueUtil.createRawValue(denominator, byteOrder, longByteCount);
            byte[] rawValue = new byte[longByteCount * 2];
            System.arraycopy(numeratorBytes, 0, rawValue, 0, longByteCount);
            System.arraycopy(denominatorBytes, 0, rawValue, longByteCount, longByteCount);
            return rawValue;
        } else { // Unhandled: JPhotoTagger ExifByte and from com.drew.* double, float, string and int arrays
            return null;
        }
    }

    private static boolean isNullTerminated(byte[] rawValue) {
        if (rawValue.length < 1) {
            return false;
        }
        byte lastByte = rawValue[rawValue.length - 1];
        return ByteUtil.toInt(lastByte) == 0;
    }

    private static byte[] terminateWithNull(byte[] rawValue) {
        if (rawValue == null) {
            return null;
        }
        byte[] terminatedRawValue = new byte[rawValue.length + 1];
        if (rawValue.length > 0) {
            System.arraycopy(rawValue, 0, terminatedRawValue, 0, rawValue.length);
        }
        terminatedRawValue[rawValue.length] = 0;
        return terminatedRawValue;
    }
}
