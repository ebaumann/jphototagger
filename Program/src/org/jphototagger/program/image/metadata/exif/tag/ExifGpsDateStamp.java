/*
 * @(#)ExifGpsDateStamp.java    Created on 2010-08-21
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

import org.jphototagger.program.app.AppLogger;

/**
 * EXIF GPS date stamp.
 *
 * @author Elmar Baumann
 */
public final class ExifGpsDateStamp {
    private static final int UNDEFINED = Integer.MIN_VALUE;
    private final String     stringValue;
    private int              year  = UNDEFINED;
    private int              month = UNDEFINED;
    private int              day   = UNDEFINED;

    /**
     * Constructor
     *
     * @param rawValue raw value with the Stamp in the format
     *                 "YYYY:MM:DD.", 11 Bytes, Byte 11 is NULL.
     * @throws IllegalArgumentException if the length of rawValue != 11
     */
    public ExifGpsDateStamp(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        if (rawValue.length != 11) {
            throw new IllegalArgumentException("length of raw value != 11: "
                                               + rawValue.length);
        }

        this.stringValue = new String(rawValue).substring(0, 10);
        setIntegerValues();
    }

    private void unsetIntegerValues() {
        year  = UNDEFINED;
        month = UNDEFINED;
        day   = UNDEFINED;
    }

    private void setIntegerValues() {
        String yearString  = stringValue.substring(0, 4);
        String monthString = stringValue.substring(5, 7);
        String dayString   = stringValue.substring(8, 10);

        try {
            year  = Integer.parseInt(yearString);
            month = Integer.parseInt(monthString);
            day   = Integer.parseInt(dayString);
        } catch (Exception ex) {
            AppLogger.logSevere(ExifGpsDateStamp.class, ex);
            unsetIntegerValues();
        }
    }

    /**
     * Returns the day of the date.
     *
     * @return day
     * @throws IllegalStateException if the date couldn't be set properly
     *         ({@link #isValid()} returns false)
     */
    public int getDay() {
        if (!isValid()) {
            throw new IllegalStateException("date couldn't be set properly");
        }

        return day;
    }

    /**
     * Returns the month of the date.
     *
     * @return month
     * @throws IllegalStateException if the date couldn't be set properly
     *         ({@link #isValid()} returns false)
     */
    public int getMonth() {
        if (!isValid()) {
            throw new IllegalStateException("date couldn't be set properly");
        }

        return month;
    }

    /**
     * Returns the year of the date.
     *
     * @return year
     * @throws IllegalStateException if the date couldn't be set properly
     *         ({@link #isValid()} returns false)
     */
    public int getYear() {
        if (!isValid()) {
            throw new IllegalStateException("date couldn't be set properly");
        }

        return year;
    }

    /**
     * Returns wether the date is valid.
     *
     * @return true if the date is valid
     */
    public boolean isValid() {
        return (year != UNDEFINED) && (month != UNDEFINED)
               && (day != UNDEFINED);
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
