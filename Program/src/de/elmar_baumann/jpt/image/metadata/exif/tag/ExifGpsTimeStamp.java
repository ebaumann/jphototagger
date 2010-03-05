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
package de.elmar_baumann.jpt.image.metadata.exif.tag;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;

/**
 * The time as UTC (Coordinated Universal Time).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
 */
public final class ExifGpsTimeStamp {

    private ExifRational hours;
    private ExifRational minutes;
    private ExifRational seconds;

    public ExifGpsTimeStamp(ExifRational hours, ExifRational minutes, ExifRational seconds) {
        this.hours   = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public ExifRational hours() {
        return hours;
    }

    public ExifRational minutes() {
        return minutes;
    }

    public ExifRational seconds() {
        return seconds;
    }
}
