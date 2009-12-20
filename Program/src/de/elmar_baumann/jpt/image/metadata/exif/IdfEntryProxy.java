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
import java.util.Arrays;

/**
 * Proxy for {@link com.imagero.reader.tiff.IFDEntry}. Reason: Files are
 * locked if used and could not be deleted and renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-15
 */
public final class IdfEntryProxy implements Comparable<IdfEntryProxy> {

    private int tag;
    private byte[] rawValue;
    private String string;
    private String name;
    private ExifByteOrder byteOrder;

    public IdfEntryProxy(IFDEntry entry) {
        try {
            string = entry.toString();
            tag = entry.getEntryMeta().getTag();
            name = entry.getEntryMeta().getName();
            rawValue = Arrays.copyOf(
                    entry.getRawValue(), entry.getRawValue().length);
            byteOrder = entry.parent.getByteOrder() == 0x4949 // 18761
                        ? ExifByteOrder.LITTLE_ENDIAN
                        : ExifByteOrder.BIG_ENDIAN;
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
    }

    public ExifByteOrder getByteOrder() {
        return byteOrder;
    }

    public String getName() {
        return name;
    }

    public byte[] getRawValue() {
        return Arrays.copyOf(rawValue, rawValue.length);
    }

    @Override
    public String toString() {
        return string;
    }

    public int getTag() {
        return tag;
    }

    @Override
    public int compareTo(IdfEntryProxy o) {
        return tag - o.tag;
    }
}
