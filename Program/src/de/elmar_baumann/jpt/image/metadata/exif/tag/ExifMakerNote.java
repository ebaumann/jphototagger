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

    public ExifMakerNote(String bundlePath) {
        this.bundle = ResourceBundle.getBundle(bundlePath);
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

        List<ExifTag> markerNoteTags = ExifMetadata.getExifMakerNoteTagsIn(exifTags, byteOffsetToIfd);

        if (markerNoteTags != null) {
            try {
            add(markerNoteTags, exifTags);
            } catch (Exception ex) {
                AppLog.logSevere(ExifMakerNote.class, ex);
            }
        }
    }

    private void add(List<ExifTag> from, Collection<ExifTag> to) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        List<String>     keys  = RegexUtil.getMatches(bundleKeys(), "^Tag[0-9]+");
        List<MakerTagInfo> infos = new ArrayList<MakerTagInfo>();

        for (String key : keys) {
            try {
                infos.add(new MakerTagInfo(indexOfTag(key), bundle.getString(key)));
            } catch (ClassNotFoundException ex) {
                AppLog.logSevere(ExifMakerNote.class, ex);
            }
        }
        add(from, to, infos);
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
            List<ExifTag>       from,
            Collection<ExifTag> to,
            List<MakerTagInfo>  infos
            ) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        int index = 0;
        for (ExifTag exifTag : from) {
            int pos = indexOf(index++, infos);
            if (pos >= 0) {
                MakerTagInfo info        = infos.get(pos);
                byte[]       rawValue    = exifTag.rawValue();
                int          offset      = info.rawValueOffset();
                int          bytesToRead = info.readAllBytes() ? rawValue.length - offset : info.bytesToRead;
                byte[]       rV          = new byte[bytesToRead];

                System.arraycopy(rawValue, offset, rV, 0, bytesToRead);

                Class  dataTypeClass = info.exifDataTypeClass();
                Object dataType      = null;

                if  (requiresByteOrder(dataTypeClass)) {
                    Constructor c = dataTypeClass.getConstructor(byte[].class, ByteOrder.class);
                    dataType = c.newInstance(rV, exifTag.byteOrder());
                } else {
                    Constructor c = dataTypeClass.getConstructor(byte[].class);
                    dataType = c.newInstance(rV);
                }

                ExifTag markerNoteTag = new ExifTag(
                        info.equalsToExifTag() ? info.equalsToExifTagId : ExifTag.Id.MAKER_NOTE.value(),
                        exifTag.dataType(),
                        rV,
                        dataType.toString(),
                        bundle.getString(info.promptPropertyKey()),
                        exifTag.byteOrder(),
                        exifTag.byteOrderValue());
                markerNoteTag.setFormatterClass(info.exifFormatterClass);
                to.add(markerNoteTag);
            }
        }
    }

    private int indexOf(int tagIndex, List<MakerTagInfo> infos) {
        int index = 0;
        for (MakerTagInfo info : infos) {
            if (tagIndex == info.tagIndex) return index;
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

        private final int    tagIndex;
        private       int    rawValueOffset;
        private       int    bytesToRead        = -1;
        private       Class  exifDataTypeClass;
        private       Class  exifFormatterClass;
        private       String promptPropertyKey;
        private       int    equalsToExifTagId  = -1;

        public MakerTagInfo(int tagIndex, String propertyValue) throws ClassNotFoundException {
            this.tagIndex = tagIndex;
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
                    case 4: promptPropertyKey  = token;                                               ; break;
                    case 5: equalsToExifTagId  = token.isEmpty()      ? -1   : Integer.parseInt(token); break;
                }
            }
        }

        public int bytesToRead() {
            return bytesToRead;
        }

        public int equalExifTagId() {
            return equalsToExifTagId;
        }

        public Class exifDataTypeClass() {
            return exifDataTypeClass;
        }

        public Class exifFormatterClass() {
            return exifFormatterClass;
        }

        public String promptPropertyKey() {
            return promptPropertyKey;
        }

        public int rawValueOffset() {
            return rawValueOffset;
        }

        public int tagIndex() {
            return tagIndex;
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
