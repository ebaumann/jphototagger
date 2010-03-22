/*
 * @(#)Version.java    Created on 2009-04-30
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

package org.jphototagger.lib.util;

import java.text.MessageFormat;

import java.util.StringTokenizer;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class Version implements Comparable<Version> {
    private final int major;
    private final int minor1;
    private final int minor2;
    private final int minor3;

    public Version(int major, int minor) {
        this.major  = major;
        this.minor1 = minor;
        this.minor2 = 0;
        this.minor3 = 0;
    }

    public Version(int major, int minor1, int minor2) {
        this.major  = major;
        this.minor1 = minor1;
        this.minor2 = minor2;
        this.minor3 = 0;
    }

    public Version(int major, int minor1, int minor2, int minor3) {
        this.major  = major;
        this.minor1 = minor1;
        this.minor2 = minor2;
        this.minor3 = minor3;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor1() {
        return minor1;
    }

    public int getMinor2() {
        return minor2;
    }

    public int getMinor3() {
        return minor3;
    }

    /**
     * Creates a {@link Version} object from a string.
     *
     * @param version   version string: 2 up to 4 delimited <em>integer numbers</em>,
     *                  e.g. <code>"0.9.1.5"</code>
     * @param delimiter number delimiters, e.g. <code>"."</code>
     * @return          version string
     * @throws          IllegalArgumentException if the number count is invalid
     * @throws          NumberFormatException    if the numbers are not parsable
     *                                           as integers
     */
    public static Version parseVersion(String version, String delimiter)
            throws IllegalArgumentException, NumberFormatException {
        int             major  = 0;
        int             minor1 = 0;
        int             minor2 = 0;
        int             minor3 = 0;
        StringTokenizer st     = new StringTokenizer(version, delimiter);
        int             count  = st.countTokens();

        if ((count < 2) || (count > 4)) {
            throw new IllegalArgumentException("Invalid count");
        }

        int index = 0;

        while (st.hasMoreTokens()) {
            int number = Integer.parseInt(st.nextToken().trim());

            switch (index++) {
            case 0 :
                major = number;

                break;

            case 1 :
                minor1 = number;

                break;

            case 2 :
                minor2 = number;

                break;

            case 3 :
                minor3 = number;

                break;

            default :
                assert false;
            }
        }

        return new Version(major, minor1, minor2, minor3);
    }

    @Override
    public int compareTo(Version o) {
        if (major > o.major) {
            return 1;
        }

        if (major < o.major) {
            return -1;
        }

        // Both major versions are equal
        if (minor1 > o.minor1) {
            return 1;
        }

        if (minor1 < o.minor1) {
            return -1;
        }

        // Both first minor versions are equal
        if (minor2 > o.minor2) {
            return 1;
        }

        if (minor2 < o.minor2) {
            return -1;
        }

        // Both second minor versions are equal
        if (minor3 > o.minor3) {
            return 1;
        }

        if (minor3 < o.minor3) {
            return -1;
        }

        // Both third minor versions are equal
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Version other = (Version) obj;

        if (this.major != other.major) {
            return false;
        }

        if (this.minor1 != other.minor1) {
            return false;
        }

        if (this.minor2 != other.minor2) {
            return false;
        }

        if (this.minor3 != other.minor3) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 97 * hash + this.major;
        hash = 97 * hash + this.minor1;
        hash = 97 * hash + this.minor2;
        hash = 97 * hash + this.minor3;

        return hash;
    }

    /**
     * Returns the first 2 version numbers.
     *
     * @return version number <code>1.2</code>
     */
    public String toString2() {
        return MessageFormat.format("{0}.{1}", new Object[] { major, minor1 });
    }

    /**
     * Returns the first 3 version numbers.
     *
     * @return version number <code>1.2.3</code>
     */
    public String toString3() {
        return MessageFormat.format("{0}.{1}.{2}", new Object[] { major, minor1,
                minor2 });
    }

    /**
     * Returns the all 4 version numbers.
     *
     * @return version number <code>1.2.3.4</code>
     */
    @Override
    public String toString() {
        return MessageFormat.format("{0}.{1}.{2}.{3}", new Object[] { major,
                minor1, minor2, minor3 });
    }
}
