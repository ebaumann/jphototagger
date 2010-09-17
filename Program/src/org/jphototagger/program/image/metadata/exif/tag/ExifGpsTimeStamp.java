/*
 * @(#)ExifGpsTimeStamp.java    Created on 2009-03-17
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;

import java.text.MessageFormat;

/**
 * The time as UTC (Coordinated Universal Time).
 *
 * @author Elmar Baumann
 */
public final class ExifGpsTimeStamp {
    private ExifRational hours;
    private ExifRational minutes;
    private ExifRational seconds;

    public ExifGpsTimeStamp(ExifRational hours, ExifRational minutes,
                            ExifRational seconds) {
        if (hours == null) {
            throw new NullPointerException("hours == null");
        }

        if (minutes == null) {
            throw new NullPointerException("minutes == null");
        }

        if (seconds == null) {
            throw new NullPointerException("seconds == null");
        }

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

    @Override
    public String toString() {
        return MessageFormat.format("{0}:{1}:{2}", hours, minutes, seconds);
    }
}
