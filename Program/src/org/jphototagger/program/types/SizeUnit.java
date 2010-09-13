/*
 * @(#)SizeUnit.java    Created on 2009-07-16
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.types;

/**
 * Size units as n multiplied by one byte.
 *
 * @author  Elmar Baumann
 */
public enum SizeUnit {
    BYTE(1, "B"), KILO_BYTE(1024, "KB"), MEGA_BYTE(1024 * 1024, "MB"),
    GIGA_BYTE(1024 * 1024 * 1024, "GB");

    private final long   bytes;
    private final String string;

    private SizeUnit(long bytes, String string) {
        this.bytes  = bytes;
        this.string = string;
    }

    public long bytes() {
        return bytes;
    }

    public static SizeUnit unit(long size) {
        return (size >= GIGA_BYTE.bytes)
               ? GIGA_BYTE
               : (size >= MEGA_BYTE.bytes)
                 ? MEGA_BYTE
                 : (size >= KILO_BYTE.bytes)
                   ? KILO_BYTE
                   : BYTE;
    }

    @Override
    public String toString() {
        return string;
    }
}
