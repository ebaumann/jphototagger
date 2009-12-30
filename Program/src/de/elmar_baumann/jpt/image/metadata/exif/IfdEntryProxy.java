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
package de.elmar_baumann.jpt.image.metadata.exif;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifByteOrder;
import com.imagero.reader.tiff.IFDEntry;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy for {@link com.imagero.reader.tiff.IFDEntry}. Reason: Files are
 * locked if used and could not be deleted and renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-15
 */
public final class IfdEntryProxy implements Comparable<IfdEntryProxy> {

    private static final Map<Integer, ExifType> DATA_TYPE_OF_TAG_ID = new HashMap<Integer, ExifType>();

    static {
        for (ExifType type : ExifType.values()) {
            DATA_TYPE_OF_TAG_ID.put(type.getValue(), type);
        }
    }

    private int           tagId;
    private ExifType      type;
    private byte[]        rawValue;
    private String        string;
    private String        name;
    private ExifByteOrder byteOrder;
    private int           byteOrderValue;
    private Class         formatterClass;

    public IfdEntryProxy(IFDEntry entry) {
        try {
            string         = entry.toString();
            tagId          = entry.getEntryMeta().getTag();
            type           = dataTypeOfTagId(entry.getType());
            name           = entry.getEntryMeta().getName();
            byteOrderValue = entry.parent.getByteOrder();
            rawValue       = Arrays.copyOf(entry.getRawValue(), entry.getRawValue().length);
            byteOrder      = byteOrderValue == 0x4949 // 18761
                                ? ExifByteOrder.LITTLE_ENDIAN
                                : ExifByteOrder.BIG_ENDIAN;
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
    }

    public IfdEntryProxy(
            int           tagId,
            ExifType      type,
            byte[]        rawValue,
            String        string,
            String        name,
            ExifByteOrder byteOrder,
            int           byteOrderValue
            ) {
        this.tagId          = tagId;
        this.type           = type;
        this.rawValue       = rawValue;
        this.string         = string;
        this.name           = name;
        this.byteOrder      = byteOrder;
        this.byteOrderValue = byteOrderValue;
    }

    public ExifByteOrder byteOrder() {
        return byteOrder;
    }

    public int byteOrderValue() {
        return byteOrderValue;
    }

    public String name() {
        return name;
    }

    public byte[] rawValue() {
        return Arrays.copyOf(rawValue, rawValue.length);
    }

    public String stringValue() {
        return string;
    }

    public Class getFormatterClass() {
        return formatterClass;
    }

    public void setFormatterClass(Class formatterClass) {
        this.formatterClass = formatterClass;
    }

    @Override
    public String toString() {
        return "Tag " + tagId +
                " "   + type.toString() +
                " "   + (name   == null ? " no name " : name  ) +
                " "   + (string == null ? ""          : string)
                ;
    }

    public int tagId() {
        return tagId;
    }

    public ExifType type() {
        return type;
    }

    @Override
    public int compareTo(IfdEntryProxy o) {
        return tagId - o.tagId;
    }

    private ExifType dataTypeOfTagId(int tagId) {
        ExifType t = DATA_TYPE_OF_TAG_ID.get(tagId);
        if (t == null) return ExifType.UNDEFINED;
        return t;
    }
}
