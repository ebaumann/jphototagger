/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.image.metadata.exif.formatter.nikon;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifLong;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTags;
import de.elmar_baumann.jpt.image.metadata.exif.formatter.ExifRawValueFormatter;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.util.ArrayUtil;
import de.elmar_baumann.lib.util.RegexUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.nio.ByteOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Nikon EXIF maker type 3.
 * <p>
 * Gathers information from a property file, which format is described in the
 * text file
 * <code>de/elmar_baumann/jpt/resource/properties/ExifMakerNote-Property-File-Format.txt</code>.
 * <p>
 * Picks from a list of maker note {@link ExifTag}s all listed in that file and
 * formats the raw values so that {@link ExifTag#stringValue()} returns a
 * displayable string and {@link ExifTag#name()} can be displayed as description.
 *
 * @author  Elmar Baumann
 * @version 2009-12-30
 */
public final class NikonMakerNote {
    private final String                 description;
    private final String                 exifTagMatchPattern;
    private final int                    exifMatchTagId;
    private final ResourceBundle         bundle;
    private final int                    byteOffsetToTiffHeader;
    private final byte[][]               magicBytePatterns;
    private final List<MakerNoteTagInfo> makerNoteTagInfos =
        new ArrayList<MakerNoteTagInfo>();
    private final List<Pair<Integer, Integer>> equalTagIdsInExifIfd =
        new ArrayList<Pair<Integer, Integer>>();

    /**
     *
     * @param bundle                    resource bundle of a properties file
     *                                  described in
     *                                  <code>de/elmar_baumann/jpt/resource/properties/NikonExifMakerNote-Property-File-Format.txt</code>
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
        this.bundle                 = bundle;
        this.description            = bundle.getString("Description");
        this.exifTagMatchPattern    = bundle.getString("MatchTagPattern");
        this.exifMatchTagId         =
            Integer.parseInt(bundle.getString("MatchTag"));
        this.byteOffsetToTiffHeader =
            Integer.parseInt(bundle.getString("ByteOffsetToTiffHeader"));

        StringTokenizer tokenizerAllPatterns =
            new StringTokenizer(bundle.getString("MagicBytePatterns"), ";");
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
            StringTokenizer tokenizer =
                new StringTokenizer(tokenizerAllPatterns.nextToken(), ",");
            int    tokenCount = tokenizer.countTokens();
            int    tokenIndex = 0;
            byte[] byteArray  = null;

            if (tokenCount <= 0) {
                throw new IllegalArgumentException(
                    "Ivalid magic byte pattern definition!");
            }

            byteArray = new byte[tokenCount];

            while (tokenizer.hasMoreTokens()) {
                byteArray[tokenIndex++] = Byte.parseByte(tokenizer.nextToken(),
                        16);
            }

            magicBytePatterns[indexMagicBytePatterns++] = byteArray;
        }
    }

    boolean matches(ExifTags exifTags, byte[] makerNoteRawValue) {
        ExifTag exifTag = exifTags.exifTagById(exifMatchTagId);

        if (exifTag == null) {
            return false;
        }

        boolean matches = exifTag.stringValue().matches(exifTagMatchPattern);

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
                System.arraycopy(makerNoteRawValue, 0, cmpBytes, 0,
                                 bytes.length);
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

    /**
     * Returns pairs of equal tag IDs: The first value in a pair is the tag ID
     * within the Maker Note IFD, the second value is the tag ID within the
     * EXIF IFD.
     *
     * @return Equal tag IDs
     */
    List<Pair<Integer, Integer>> getTagIdsEqualInExifIfd() {
        return new ArrayList<Pair<Integer, Integer>>(equalTagIdsInExifIfd);
    }

    @SuppressWarnings("unchecked")
    List<ExifTag> getDisplayableMakerNotesOf(List<ExifTag> makerNoteTags)
            throws NoSuchMethodException, InstantiationException,
                   IllegalAccessException, IllegalArgumentException,
                   InvocationTargetException {
        List<ExifTag> displayableTags = new ArrayList<ExifTag>();

        for (ExifTag exifTag : makerNoteTags) {
            int pos = indexOf(exifTag.idValue(), makerNoteTagInfos);

            if (pos >= 0) {
                MakerNoteTagInfo info              = makerNoteTagInfos.get(pos);
                byte[]           rawValue          = exifTag.rawValue();
                int              offset            = info.rawValueOffset;
                int              bytesToRead       = info.readAllBytes()
                        ? rawValue.length - offset
                        : info.bytesToRead;
                byte[]           rawValueMakerNote = new byte[bytesToRead];

                System.arraycopy(rawValue, offset, rawValueMakerNote, 0,
                                 bytesToRead);

                Class<?> dataTypeClass = info.exifDataTypeClass;
                Object   dataType      = null;

                if (requiresByteOrder(dataTypeClass)) {
                    Constructor<?> c =
                        dataTypeClass.getConstructor(byte[].class,
                                                     ByteOrder.class);

                    dataType = c.newInstance(rawValueMakerNote,
                                             exifTag.byteOrder());
                } else {
                    Constructor<?> c =
                        dataTypeClass.getConstructor(byte[].class);

                    dataType = c.newInstance(rawValueMakerNote);
                }

                ExifTag makerNoteTag = new ExifTag(exifTag.idValue(),
                                                   exifTag.dataType().value(),
                                                   exifTag.valueCount(),
                                                   exifTag.valueOffset(),
                                                   rawValueMakerNote,
                                                   info.hasFormatterClass()
                                                   ? format(
                                                       info.exifFormatterClass,
                                                       exifTag)
                                                   : dataType.toString()
                                                       .trim(), exifTag
                                                           .byteOrderId(), bundle
                                                           .getString(info
                                                               .tagNameBundleKey), exifTag
                                                                   .ifdType());

                displayableTags.add(makerNoteTag);
            }
        }

        return displayableTags;
    }

    private List<String> bundleKeys() {
        List<String> keys = new ArrayList<String>();

        for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements(); ) {
            keys.add(e.nextElement());
        }

        return keys;
    }

    private int indexOfTag(String tag) {
        return Integer.parseInt(tag.substring(3));
    }

    private String format(Class<?> formatterClass, ExifTag exifTag)
            throws InstantiationException, IllegalAccessException {
        ExifRawValueFormatter formatter =
            (ExifRawValueFormatter) formatterClass.newInstance();

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
                makerNoteTagInfos.add(new MakerNoteTagInfo(indexOfTag(key),
                        bundle.getString(key)));
            } catch (Exception ex) {
                AppLogger.logSevere(NikonMakerNote.class, ex);
            }
        }
    }

    private static final Collection<Class<?>> BYTE_ORDER_CLASSES =
        new HashSet<Class<?>>();

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
                equalTagIdsInExifIfd.add(new Pair<Integer,
                                                  Integer>(info.tagIdValue,
                                                      info.equalsToExifTagId));
            }
        }
    }

    private static class MakerNoteTagInfo {
        private final int tagIdValue;
        private int       rawValueOffset;
        private int       bytesToRead = -1;
        private Class<?>  exifDataTypeClass;
        private Class<?>  exifFormatterClass;
        private String    tagNameBundleKey;
        private int       equalsToExifTagId = -1;

        public MakerNoteTagInfo(int tagIndex, String propertyValue)
                throws ClassNotFoundException {
            this.tagIdValue  = tagIndex;
            tagNameBundleKey = "Tag" + Integer.toString(tagIndex)
                               + "DisplayName";
            init(propertyValue);
        }

        private void init(String propertyValue) throws ClassNotFoundException {
            StringTokenizer st    = new StringTokenizer(propertyValue, ";");
            int             index = 0;

            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();

                switch (index++) {
                case 0 :
                    rawValueOffset = Integer.parseInt(token);

                    break;

                case 1 :
                    bytesToRead = token.equals("all")
                                  ? -1
                                  : Integer.parseInt(token);

                    break;

                case 2 :
                    exifDataTypeClass = Class.forName(token);

                    break;

                case 3 :
                    exifFormatterClass = token.equals("null")
                                         ? null
                                         : Class.forName(token);

                    break;

                case 4 :
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
