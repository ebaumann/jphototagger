package org.jphototagger.exif.formatter.nikon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.ExifTags;
import org.jphototagger.exif.datatype.ExifLong;
import org.jphototagger.exif.datatype.ExifRational;
import org.jphototagger.exif.datatype.ExifShort;
import org.jphototagger.exif.formatter.ExifRawValueFormatter;
import org.jphototagger.lib.util.ArrayUtil;
import org.jphototagger.lib.util.RegexUtil;

/**
 * Nikon EXIF maker type 3.
 * <p>
 * Gathers information from a property file, which format is described in the
 * text file
 * <code>org/jphototagger/program/resource/properties/ExifMakerNote-Property-File-Format.txt</code>.
 *
 * @author Elmar Baumann
 */
public final class NikonMakerNote {

    private final String description;
    private final String exifTagMatchPattern;
    private final int exifMatchTagId;
    private final ResourceBundle bundle;
    private final int byteOffsetToTiffHeader;
    private final byte[][] magicBytePatterns;
    private final List<MakerNoteTagInfo> makerNoteTagInfos = new ArrayList<>();
    private final List<NikonMakerNoteTagIdExifTagId> equalTagIdsInExifIfd = new ArrayList<>();

    /**
     *
     * @param bundle                    resource bundle of a properties file
     *                                  described in
     *                                  <code>org/jphototagger/program/resource/properties/NikonExifMakerNote-Property-File-Format.txt</code>
     * @throws MissingResourceException if the bundle does not contain the keys
     *                                  <code>"ByteOffsetToTiffHeader"</code>
     *                                  and <code>"Description"</code>
     * @throws ClassCastException       if the bundle value of the key
     *                                  <code>"ByteOffsetToTiffHeader"</code> isn't
     *                                  a java string
     * @throws NumberFormatException    if the string of the bundle key
     *                                  <code>"ByteOffsetToTiffHeader"</code> can't
     *                                  be formatted into an integer value
     */
    NikonMakerNote(ResourceBundle bundle) throws MissingResourceException {
        this.bundle = bundle;
        this.description = bundle.getString("Description");
        this.exifTagMatchPattern = bundle.getString("MatchTagPattern");
        this.exifMatchTagId = Integer.parseInt(bundle.getString("MatchTag"));
        this.byteOffsetToTiffHeader = Integer.parseInt(bundle.getString("ByteOffsetToTiffHeader"));

        StringTokenizer tokenizerAllPatterns = new StringTokenizer(bundle.getString("MagicBytePatterns"), ";");
        int countMagicBytePatterns = tokenizerAllPatterns.countTokens();

        if (countMagicBytePatterns > 0) {
            magicBytePatterns = new byte[countMagicBytePatterns][];
            setMagicBytePatterns(tokenizerAllPatterns);
        } else {
            magicBytePatterns = null;
        }

        setMakerNoteTagInfos();
        setTagIdsEqualInExifIfd();
    }

    private void setMagicBytePatterns(StringTokenizer tokenizerAllPatterns) {
        int indexMagicBytePatterns = 0;

        while (tokenizerAllPatterns.hasMoreTokens()) {
            StringTokenizer tokenizer = new StringTokenizer(tokenizerAllPatterns.nextToken(), ",");
            int tokenCount = tokenizer.countTokens();
            int tokenIndex = 0;
            byte[] byteArray = null;

            if (tokenCount <= 0) {
                throw new IllegalArgumentException("Ivalid magic byte pattern definition!");
            }

            byteArray = new byte[tokenCount];

            while (tokenizer.hasMoreTokens()) {
                byteArray[tokenIndex++] = Byte.parseByte(tokenizer.nextToken(), 16);
            }

            magicBytePatterns[indexMagicBytePatterns++] = byteArray;
        }
    }

    boolean matches(ExifTags exifTags, byte[] makerNoteRawValue) {
        ExifTag exifTag = exifTags.findExifTagByTagId(exifMatchTagId);

        if (exifTag == null) {
            return false;
        }

        boolean matches = exifTag.getStringValue().matches(exifTagMatchPattern);

        if (magicBytePatterns == null) {
            return matches;
        } else {
            return matches && matchesMagicBytePattern(makerNoteRawValue);
        }
    }

    private boolean matchesMagicBytePattern(byte[] makerNoteRawValue) {
        if (magicBytePatterns == null) {
            return false;
        }

        for (byte[] bytes : magicBytePatterns) {
            byte[] cmpBytes = makerNoteRawValue;

            if (makerNoteRawValue.length > bytes.length) {
                cmpBytes = new byte[bytes.length];
                System.arraycopy(makerNoteRawValue, 0, cmpBytes, 0, bytes.length);
            }

            if (ArrayUtil.byteArraysEquals(bytes, cmpBytes)) {
                return true;
            }
        }

        return false;
    }

    int getByteOffsetToIfd() {
        return byteOffsetToTiffHeader;
    }

    String getDescription() {
        return description;
    }

    List<NikonMakerNoteTagIdExifTagId> getTagIdsEqualInExifIfd() {
        return Collections.unmodifiableList(equalTagIdsInExifIfd);
    }

    @SuppressWarnings("unchecked")
    List<ExifTag> getDisplayableMakerNotesOf(List<ExifTag> makerNoteTags)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        List<ExifTag> displayableTags = new ArrayList<>();

        for (ExifTag exifTag : makerNoteTags) {
            int pos = indexOf(exifTag.getTagId(), makerNoteTagInfos);

            if (pos >= 0) {
                MakerNoteTagInfo info = makerNoteTagInfos.get(pos);
                byte[] rawValue = exifTag.getRawValue();
                int offset = info.rawValueOffset;
                int bytesToRead = info.readAllBytes()
                        ? rawValue.length - offset
                        : info.bytesToRead;
                byte[] rawValueMakerNote = new byte[bytesToRead];

                System.arraycopy(rawValue, offset, rawValueMakerNote, 0, bytesToRead);

                Class<?> dataTypeClass = info.exifDataTypeClass;
                Object dataType = null;

                if (requiresByteOrder(dataTypeClass)) {
                    Constructor<?> c = dataTypeClass.getConstructor(byte[].class, ByteOrder.class);

                    dataType = c.newInstance(rawValueMakerNote, exifTag.convertByteOrderIdToByteOrder());
                } else {
                    Constructor<?> c = dataTypeClass.getConstructor(byte[].class);

                    dataType = c.newInstance(rawValueMakerNote);
                }

                ExifTag makerNoteTag = new ExifTag(exifTag.getTagId(), exifTag.convertDataTypeIdToExifDataType().getValue(), exifTag.getValueCount(),
                        exifTag.getValueOffset(), rawValueMakerNote, info.hasFormatterClass()
                        ? format(info.exifFormatterClass, exifTag)
                        : dataType.toString().trim(), exifTag.getByteOrderId(), bundle.getString(info.tagNameBundleKey),
                        exifTag.getIfdType());

                displayableTags.add(makerNoteTag);
            }
        }

        return displayableTags;
    }

    private List<String> bundleKeys() {
        List<String> keys = new ArrayList<>();

        for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements();) {
            keys.add(e.nextElement());
        }

        return keys;
    }

    private int indexOfTag(String tag) {
        return Integer.parseInt(tag.substring(3));
    }

    private String format(Class<?> formatterClass, ExifTag exifTag)
            throws InstantiationException, IllegalAccessException {
        ExifRawValueFormatter formatter = (ExifRawValueFormatter) formatterClass.newInstance();

        return formatter.format(exifTag);
    }

    private int indexOf(int tagIdValue, List<MakerNoteTagInfo> infos) {
        int index = 0;

        for (MakerNoteTagInfo info : infos) {
            if (tagIdValue == info.tagIdValue) {
                return index;
            }

            index++;
        }

        return -1;
    }

    private void setMakerNoteTagInfos() {
        List<String> keys = RegexUtil.getMatches(bundleKeys(), "^Tag[0-9]+");

        for (String key : keys) {
            try {
                makerNoteTagInfos.add(new MakerNoteTagInfo(indexOfTag(key), bundle.getString(key)));
            } catch (Throwable t) {
                Logger.getLogger(NikonMakerNote.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }
    private static final Collection<Class<?>> BYTE_ORDER_CLASSES = new HashSet<>();

    static {
        BYTE_ORDER_CLASSES.add(ExifShort.class);
        BYTE_ORDER_CLASSES.add(ExifRational.class);
        BYTE_ORDER_CLASSES.add(ExifLong.class);
    }

    private boolean requiresByteOrder(Class<?> clazz) {
        return BYTE_ORDER_CLASSES.contains(clazz);
    }

    private void setTagIdsEqualInExifIfd() {
        for (MakerNoteTagInfo info : makerNoteTagInfos) {
            if (info.equalsToExifTag()) {
                equalTagIdsInExifIfd.add(new NikonMakerNoteTagIdExifTagId(info.tagIdValue, info.equalsToExifTagId));
            }
        }
    }

    private static class MakerNoteTagInfo {

        private final int tagIdValue;
        private int rawValueOffset;
        private int bytesToRead = -1;
        private Class<?> exifDataTypeClass;
        private Class<?> exifFormatterClass;
        private String tagNameBundleKey;
        private int equalsToExifTagId = -1;

        private MakerNoteTagInfo(int tagIndex, String propertyValue) throws ClassNotFoundException {
            this.tagIdValue = tagIndex;
            tagNameBundleKey = "Tag" + Integer.toString(tagIndex) + "DisplayName";
            init(propertyValue);
        }

        private void init(String propertyValue) throws ClassNotFoundException {
            StringTokenizer st = new StringTokenizer(propertyValue, ";");
            int index = 0;

            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();

                switch (index++) {
                    case 0:
                        rawValueOffset = Integer.parseInt(token);

                        break;

                    case 1:
                        bytesToRead = token.equals("all")
                                ? -1
                                : Integer.parseInt(token);

                        break;

                    case 2:
                        exifDataTypeClass = Class.forName(token);

                        break;

                    case 3:
                        exifFormatterClass = token.equals("null")
                                ? null
                                : Class.forName(token);

                        break;

                    case 4:
                        equalsToExifTagId = token.isEmpty()
                                ? -1
                                : Integer.parseInt(token);

                        break;
                }
            }
        }

        public boolean hasFormatterClass() {
            return exifFormatterClass != null;
        }

        public boolean equalsToExifTag() {
            return equalsToExifTagId > 0;
        }

        public boolean readAllBytes() {
            return bytesToRead <= 0;
        }
    }
}
