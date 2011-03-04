package org.jphototagger.program.app;

import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.program.filefilter.NoXmpFileFilter;
import org.jphototagger.program.filefilter.XmpRatingFileFilter;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.io.FileFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Special file filters used in the application.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class AppFileFilters {
    private static final boolean experimental = UserSettings.INSTANCE.isUseExperimentalFileFormats();

    /**
     * Filter of all computable image file formats
     */
    public static final RegexFileFilter ACCEPTED_IMAGE_FILENAMES = new RegexFileFilter(
          ".*\\.[cC][rR][wW];"                          // Canon RAW
        + ".*\\.[cC][rR]2;"                             // Canon RAW 2
        + ".*\\.[dD][cC][rR];"                          // Kodak RAW
        + ".*\\.[dD][nN][gG];"                          // Digal Negative
        + ".*\\.[jJ][pP][gG];"                          // Joint Photographic Experts Group
        + ".*\\.[jJ][pP][eE][gG];"                      // Joint Photographic Experts Group
        + ".*\\.[mM][rR][wW];"                          // Minolta RAW
        + ".*\\.[nN][eE][fF];"                          // Nikon RAW
        + (experimental ? ".*\\.[sS][rR][wW];" : "")    // Samsung RAW
        + ".*\\.[tT][hH][mM];"                          // EXIF Info
        + ".*\\.[tT][iI][fF];"                          // Tagged Image File Format
        + ".*\\.[tT][iI][fF][fF];"                      // Tagged Image File Format
        , ";");
    public static final RegexFileFilter RAW_FILENAMES = new RegexFileFilter(
          ".*\\.[cC][rR][wW];"                          // Canon RAW
        + ".*\\.[cC][rR]2;"                             // Canon RAW 2
        + ".*\\.[dD][cC][rR];"                          // Kodak RAW
        + ".*\\.[mM][rR][wW];"                          // Minolta RAW
        + ".*\\.[nN][eE][fF];"                          // Nikon RAW
        + (experimental ? ".*\\.[sS][rR][wW];" : "")    // Samsung RAW
        , ";");
    public static final RegexFileFilter DNG_FILENAMES = new RegexFileFilter(
          ".*\\.[dD][nN][gG];"                          // Digal Negative
        , ";");
    public static final RegexFileFilter JPEG_FILENAMES = new RegexFileFilter(
          ".*\\.[jJ][pP][gG];"                          // Joint Photographic Experts Group
        + ".*\\.[jJ][pP][eE][gG];"                      // Joint Photographic Experts Group
            , ";");
    public static final RegexFileFilter TIFF_FILENAMES = new RegexFileFilter(
          ".*\\.[tT][iI][fF];"                          // Tagged Image File Format
        + ".*\\.[tT][iI][fF][fF];"                      // Tagged Image File Format
            , ";");
    public static final NoXmpFileFilter NO_XMP = NoXmpFileFilter.INSTANCE;
    public static final FileFilter XMP_RATING_1_STAR = new XmpRatingFileFilter(1);
    public static final FileFilter XMP_RATING_2_STARS = new XmpRatingFileFilter(2);
    public static final FileFilter XMP_RATING_3_STARS = new XmpRatingFileFilter(3);
    public static final FileFilter XMP_RATING_4_STARS = new XmpRatingFileFilter(4);
    public static final FileFilter XMP_RATING_5_STARS = new XmpRatingFileFilter(5);
    private static final Map<FileFilter, String> DISPLAY_NAME_OF_FILTER = new HashMap<FileFilter, String>();

    static {

        // UPDATE IF new filters added
        DISPLAY_NAME_OF_FILTER.put(ACCEPTED_IMAGE_FILENAMES, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.ALL"));
        DISPLAY_NAME_OF_FILTER.put(DNG_FILENAMES, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.DNG"));
        DISPLAY_NAME_OF_FILTER.put(JPEG_FILENAMES, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.JPEG"));
        DISPLAY_NAME_OF_FILTER.put(RAW_FILENAMES, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.RAW"));
        DISPLAY_NAME_OF_FILTER.put(TIFF_FILENAMES, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.TIFF"));
        DISPLAY_NAME_OF_FILTER.put(NO_XMP, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.NoXmp"));
        DISPLAY_NAME_OF_FILTER.put(XMP_RATING_1_STAR, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.1Star"));
        DISPLAY_NAME_OF_FILTER.put(XMP_RATING_2_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.2Stars"));
        DISPLAY_NAME_OF_FILTER.put(XMP_RATING_3_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.3Stars"));
        DISPLAY_NAME_OF_FILTER.put(XMP_RATING_4_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.4Stars"));
        DISPLAY_NAME_OF_FILTER.put(XMP_RATING_5_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.5Stars"));
    }

    /**
     * Returns the localized display name of a file filter.
     *
     * @param  filter one of the filters of this class
     * @return        display name or null if the filter is not a field of this
     *                class
     */
    public static String getDisplaynameOf(FileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        return DISPLAY_NAME_OF_FILTER.get(filter);
    }

    private AppFileFilters() {}
}
