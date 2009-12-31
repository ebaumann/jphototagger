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
package de.elmar_baumann.jpt.image.metadata.exif.tag;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifLong;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.jpt.image.metadata.exif.formatter.ExifRawValueFormatter;
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
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-30
 */
public final class ExifMakerNote {

    private final ResourceBundle bundle;
    private       int            byteOffsetToIfd;
    private       byte[][]       magicBytePatterns = null;

    public ExifMakerNote(ResourceBundle bundle) {

        this.bundle = bundle;

        setMagicBytePatterns();
        setByteOffsetToIfd();
    }

    public boolean matches(byte[] makerNoteRawValue) {

        if (magicBytePatterns == null) return false;

        for (byte[] bytes : magicBytePatterns) {

            byte[] cmpBytes = makerNoteRawValue;

            if (makerNoteRawValue.length > bytes.length) {
                cmpBytes = new byte[bytes.length];
                System.arraycopy(makerNoteRawValue, 0, cmpBytes, 0, bytes.length);
            }

            if (ArrayUtil.byteArraysEquals(bytes, cmpBytes)) return true;
        }
        return false;
    }

    public void addMakerNotes(Collection<ExifTag> exifTags) {

        List<ExifTag> makerNoteTags = ExifMetadata.getExifMakerNoteTagsIn(exifTags, byteOffsetToIfd);

        if (makerNoteTags != null) {
            try {
            add(makerNoteTags, exifTags);
            } catch (Exception ex) {
                AppLog.logSevere(ExifMakerNote.class, ex);
            }
        }
    }

    private void add(
            List<ExifTag> makerNoteTags, Collection<ExifTag> allTags
            )
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        List<String>       keys  = RegexUtil.getMatches(bundleKeys(), "^Tag[0-9]+");
        List<MakerTagInfo> infos = new ArrayList<MakerTagInfo>();

        for (String key : keys) {
            try {
                infos.add(new MakerTagInfo(indexOfTag(key), bundle.getString(key)));
            } catch (ClassNotFoundException ex) {
                AppLog.logSevere(ExifMakerNote.class, ex);
            }
        }
        add(makerNoteTags, allTags, infos);
    }

    private List<String> bundleKeys() {
        List<String> keys = new ArrayList<String>();
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
            keys.add((String) e.nextElement());
        }
        return keys;
    }

    private int indexOfTag(String tag) {
        return Integer.parseInt(tag.substring(3));
    }

    @SuppressWarnings("unchecked")
    private void add(
            List<ExifTag>       makerNoteTags,
            Collection<ExifTag> allTags,
            List<MakerTagInfo>  infos
            )
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        for (ExifTag exifTag : makerNoteTags) {
            int pos = indexOf(exifTag.idValue(), infos);
            if (pos >= 0) {
                MakerTagInfo info        = infos.get(pos);
                byte[]       rawValue    = exifTag.rawValue();
                int          offset      = info.rawValueOffset;
                int          bytesToRead = info.readAllBytes() ? rawValue.length - offset : info.bytesToRead;
                byte[]       rV          = new byte[bytesToRead];

                System.arraycopy(rawValue, offset, rV, 0, bytesToRead);

                Class  dataTypeClass = info.exifDataTypeClass;
                Object dataType      = null;

                if  (requiresByteOrder(dataTypeClass)) {
                    Constructor c = dataTypeClass.getConstructor(byte[].class, ByteOrder.class);
                    dataType = c.newInstance(rV, exifTag.byteOrder());
                } else {
                    Constructor c = dataTypeClass.getConstructor(byte[].class);
                    dataType = c.newInstance(rV);
                }

                ExifTag makerNoteTag = new ExifTag(
                        info.equalsToExifTag() ? info.equalsToExifTagId : ExifTag.Id.DISPLAYABLE_MAKER_NOTE.value() + exifTag.idValue(),
                        exifTag.dataType().getValue(),
                        exifTag.valueCount(),
                        exifTag.fileOffset(),
                        rV,
                        info.exifFormatterClass == null ? dataType.toString() : format(info.exifFormatterClass, rV),
                        exifTag.byteOrderValue(),
                        bundle.getString(info.tagNameBundleKey));

                allTags.add(makerNoteTag);
            }
        }
    }

    private String format(Class formatterClass, byte[] rawValue) throws InstantiationException, IllegalAccessException {

        ExifRawValueFormatter formatter = (ExifRawValueFormatter) formatterClass.newInstance();

        return formatter.format(rawValue);
    }

    private int indexOf(int tagIdValue, List<MakerTagInfo> infos) {
        int index = 0;
        for (MakerTagInfo info : infos) {
            if (tagIdValue == info.tagIdValue) return index;
            index++;
        }
        return -1;
    }
    
    private static final Collection<Class> BYTE_ORDER_CLASSES = new HashSet<Class>();
    
    static {
        BYTE_ORDER_CLASSES.add(ExifShort.class);
        BYTE_ORDER_CLASSES.add(ExifRational.class);
        BYTE_ORDER_CLASSES.add(ExifLong.class);
    }

    private boolean requiresByteOrder(Class clazz) {
        return BYTE_ORDER_CLASSES.contains(clazz);
    }

    private static class MakerTagInfo {

        private final int    tagIdValue;
        private       int    rawValueOffset;
        private       int    bytesToRead        = -1;
        private       Class  exifDataTypeClass;
        private       Class  exifFormatterClass;
        private       String tagNameBundleKey;
        private       int    equalsToExifTagId  = -1;

        public MakerTagInfo(int tagIndex, String propertyValue) throws ClassNotFoundException {
            this.tagIdValue    = tagIndex;
            tagNameBundleKey = "Tag" + Integer.toString(tagIndex) + "DisplayName";
            init(propertyValue);
        }

        private void init(String propertyValue) throws ClassNotFoundException {
            StringTokenizer st = new StringTokenizer(propertyValue, ";");

            int index = 0;
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                switch (index++) {
                    case 0: rawValueOffset     = Integer.parseInt(token)                              ; break;
                    case 1: bytesToRead        = token.equals("all")  ? - 1  : Integer.parseInt(token); break;
                    case 2: exifDataTypeClass  = Class.forName(token)                                 ; break;
                    case 3: exifFormatterClass = token.equals("null") ? null : Class.forName(token)   ; break;
                    case 4: equalsToExifTagId  = token.isEmpty()      ? -1   : Integer.parseInt(token); break;
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

    private void setMagicBytePatterns() {
        StringTokenizer tokenizerAllPatterns = new StringTokenizer(bundle.getString("MagicBytePatterns"), ";");
        int             count                = tokenizerAllPatterns.countTokens();

        if (count <= 0) return;

        magicBytePatterns = new byte[count][];

        int index = 0;
        while (tokenizerAllPatterns.hasMoreTokens()) {

            StringTokenizer tokenizer  = new StringTokenizer(tokenizerAllPatterns.nextToken(), ",");
            int             tokenCount = tokenizer.countTokens();
            int             tokenIndex = 0;
            byte[]          byteArray  = null;

            if (tokenCount <= 0) throw new IllegalArgumentException("Ivalid magic byte pattern definition!");

                byteArray  = new byte[tokenCount];

                while (tokenizer.hasMoreTokens()) {
                    byteArray[tokenIndex++] = Byte.parseByte(tokenizer.nextToken(), 16);
                }
                magicBytePatterns[index++] = byteArray;
        }
    }

    private void setByteOffsetToIfd() {
        byteOffsetToIfd = Integer.parseInt(bundle.getString("ByteOffsetToTiffIfd"));
    }

}
