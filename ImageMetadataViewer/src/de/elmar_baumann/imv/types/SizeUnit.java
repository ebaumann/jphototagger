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
package de.elmar_baumann.imv.types;

/**
 * Size units as n multiplied by one byte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-16
 */
public enum SizeUnit {

    BYTE(1, "B"), // NOI18N
    KILO_BYTE(1024, "KB"), // NOI18N
    MEGA_BYTE(1024 * 1024, "MB"), // NOI18N
    GIGA_BYTE(1024 * 1024 * 1024, "GB"); // NOI18N
    private final long bytes;
    private final String string;

    private SizeUnit(long bytes, String string) {
        this.bytes = bytes;
        this.string = string;
    }

    public long bytes() {
        return bytes;
    }

    public static SizeUnit unit(long size) {
        return size >= GIGA_BYTE.bytes
               ? GIGA_BYTE
               : size >= MEGA_BYTE.bytes
                 ? MEGA_BYTE
                 : size >= KILO_BYTE.bytes
                   ? KILO_BYTE
                   : BYTE;
    }

    @Override
    public String toString() {
        return string;
    }
}
