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
package de.elmar_baumann.jpt.image.metadata.exif.datatype;

/**
 * Count of an EXIF data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-06
 */
public final class ExifCount {

    public static final ExifCount NUMBER_1 = new ExifCount(1);
    public static final ExifCount NUMBER_2 = new ExifCount(2);
    public static final ExifCount NUMBER_3 = new ExifCount(3);
    public static final ExifCount NUMBER_4 = new ExifCount(4);
    public static final ExifCount NUMBER_11 = new ExifCount(11);
    public static final ExifCount NUMBER_20 = new ExifCount(20);
    public static final ExifCount NUMBER_33 = new ExifCount(33);
    public static final ExifCount ANY = new ExifCount(Type.ANY);

    public enum Type {

        ANY, NUMBER
    };
    private final Integer count;
    private final Type type;

    private ExifCount(int count) {
        this.count = count;
        type = Type.NUMBER;
    }

    private ExifCount(Type type) {
        this.type = type;
        count = null;
    }

    /**
     * Returns the count.
     *
     * @return count if {@link #getType()} equals {@link Type#NUMBER}, else null
     */
    public Integer getValue() {
        return count;
    }

    /**
     * Returns the type of the count.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }
}
