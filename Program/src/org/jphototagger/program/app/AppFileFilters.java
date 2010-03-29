/*
 * @(#)AppFileFilters.java    Created on 2008-10-05
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

package org.jphototagger.program.app;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.program.resource.JptBundle;

/**
 * Special file filters used in the application.
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class AppFileFilters {

    /**
     * Filter of all computable image file formats
     */
    public static final RegexFileFilter ACCEPTED_IMAGE_FILENAME_FILTER =
        new RegexFileFilter(
          ".*\\.[cC][rR][wW];"               // Canon RAW
        + ".*\\.[cC][rR]2;"                  // Canon RAW 2
        + ".*\\.[dD][cC][rR];"               // Kodak RAW
        + ".*\\.[dD][nN][gG];"               // Digal Negative
        + ".*\\.[jJ][pP][gG];"               // Joint Photographic Experts Group
        + ".*\\.[jJ][pP][eE][gG];"           // Joint Photographic Experts Group
        + ".*\\.[mM][rR][wW];"               // Minolta RAW
        + ".*\\.[nN][eE][fF];"               // Nikon RAW
        + ".*\\.[tT][hH][mM];"               // EXIF Info
        + ".*\\.[tT][iI][fF];"               // Tagged Image File Format
        + ".*\\.[tT][iI][fF][fF];"           // Tagged Image File Format
        , ";");

    public static final RegexFileFilter RAW_FILENAME_FILTER =
        new RegexFileFilter(
          ".*\\.[cC][rR][wW];"               // Canon RAW
        + ".*\\.[cC][rR]2;"                  // Canon RAW 2
        + ".*\\.[dD][cC][rR];"               // Kodak RAW
        + ".*\\.[mM][rR][wW];"               // Minolta RAW
        + ".*\\.[nN][eE][fF];"               // Nikon RAW
        , ";");

    public static final RegexFileFilter DNG_FILENAME_FILTER =
        new RegexFileFilter(
        ".*\\.[dD][nN][gG];"                 // Digal Negative
        , ";");

    public static final RegexFileFilter JPEG_FILENAME_FILTER =
        new RegexFileFilter(
          ".*\\.[jJ][pP][gG];"               // Joint Photographic Experts Group
        + ".*\\.[jJ][pP][eE][gG];"           // Joint Photographic Experts Group
        , ";");

    public static final RegexFileFilter TIFF_FILENAME_FILTER =
        new RegexFileFilter(
          ".*\\.[tT][iI][fF];"               // Tagged Image File Format
        + ".*\\.[tT][iI][fF][fF];"           // Tagged Image File Format
        , ";");


    private static final Map<RegexFileFilter, String> DISPLAY_NAME_OF_FILTER =
            new HashMap<RegexFileFilter, String>();

    static {
        // UPDATE IF new filters added
        DISPLAY_NAME_OF_FILTER.put(ACCEPTED_IMAGE_FILENAME_FILTER,
                JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.ALL"));
        DISPLAY_NAME_OF_FILTER.put(DNG_FILENAME_FILTER,
                JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.DNG"));
        DISPLAY_NAME_OF_FILTER.put(JPEG_FILENAME_FILTER,
                JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.JPEG"));
        DISPLAY_NAME_OF_FILTER.put(RAW_FILENAME_FILTER,
                JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.RAW"));
        DISPLAY_NAME_OF_FILTER.put(TIFF_FILENAME_FILTER,
                JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.TIFF"));
    }

    /**
     * Returns the localized display name of a file filter.
     *
     * @param  filter one of the filters of this class
     * @return        display name or null if the filter is not a field of this
     *                class
     */
    public static String getDisplaynameOf(RegexFileFilter filter) {
        return DISPLAY_NAME_OF_FILTER.get(filter);
    }

    private AppFileFilters() {}
}
