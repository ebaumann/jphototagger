package org.jphototagger.program.app;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.program.data.UserDefinedFileType;
import org.jphototagger.program.filefilter.NoXmpFileFilter;
import org.jphototagger.program.filefilter.XmpRatingFileFilter;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jphototagger.program.database.DatabaseUserDefinedFileTypes;

/**
 * Special file filters used in the application.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class AppFileFilters {

    public static final NoXmpFileFilter NO_XMP = NoXmpFileFilter.INSTANCE;
    public static final FileFilter XMP_RATING_1_STAR = new XmpRatingFileFilter(1);
    public static final FileFilter XMP_RATING_2_STARS = new XmpRatingFileFilter(2);
    public static final FileFilter XMP_RATING_3_STARS = new XmpRatingFileFilter(3);
    public static final FileFilter XMP_RATING_4_STARS = new XmpRatingFileFilter(4);
    public static final FileFilter XMP_RATING_5_STARS = new XmpRatingFileFilter(5);
    private final Map<FileFilter, String> displayNameOfFilter = new HashMap<FileFilter, String>();
    private static final List<String> EXPERIMENTAL_FILE_FORMAT_DESCRIPTIONS = new ArrayList<String>();
    private final RegexFileFilter allAcceptedImageFilesFilter;
    private final RegexFileFilter acceptedRawFilesFilter;
    private final RegexFileFilter acceptedDngFilesFilter;
    private final RegexFileFilter acceptedJpegFilesFilter;
    private final RegexFileFilter acceptedTiffFilesFilter;
    private final RegexFileFilter userDefinedFileTypesFilter;
    public static final AppFileFilters INSTANCE = new AppFileFilters();

    private AppFileFilters() {
        allAcceptedImageFilesFilter = createAllAcceptedImagesFileFilter();
        acceptedRawFilesFilter = createAcceptedRawFilesFiter();
        acceptedDngFilesFilter = createAcceptedDngFilesFiter();
        acceptedJpegFilesFilter = createAcceptedJpegFilesFiter();
        acceptedTiffFilesFilter = createAcceptedTiffFilesFiter();
        userDefinedFileTypesFilter = createUserDefindedFileTypesFilter();
        boolean experimental = UserSettings.INSTANCE.isUseExperimentalFileFormats();

        initExperimentalFileFormatDescriptions();

        if (experimental) {
            addRawImageFileExperimentalPatterns(acceptedRawFilesFilter);
            addRawImageFileExperimentalPatterns(allAcceptedImageFilesFilter);
        }

        if (userDefinedFileTypesFilter != null) {
            allAcceptedImageFilesFilter.addAcceptPatternsOf(userDefinedFileTypesFilter);
        }

        initDisplaynames();
    }

    private RegexFileFilter createAllAcceptedImagesFileFilter() {
        return new RegexFileFilter(
                ".*\\.[cC][rR][wW];" // Canon RAW
                + ".*\\.[cC][rR]2;" // Canon RAW 2
                + ".*\\.[dD][cC][rR];" // Kodak RAW
                + ".*\\.[dD][nN][gG];" // Digal Negative
                + ".*\\.[jJ][pP][gG];" // Joint Photographic Experts Group
                + ".*\\.[jJ][pP][eE][gG];" // Joint Photographic Experts Group
                + ".*\\.[mM][rR][wW];" // Minolta RAW
                + ".*\\.[nN][eE][fF];" // Nikon RAW
                + ".*\\.[tT][hH][mM];" // EXIF Info
                + ".*\\.[tT][iI][fF];" // Tagged Image File Format
                + ".*\\.[tT][iI][fF][fF];" // Tagged Image File Format
                , ";");
    }

    private RegexFileFilter createAcceptedRawFilesFiter() {
        return new RegexFileFilter(
                ".*\\.[cC][rR][wW];" // Canon RAW
                + ".*\\.[cC][rR]2;" // Canon RAW 2
                + ".*\\.[dD][cC][rR];" // Kodak RAW
                + ".*\\.[mM][rR][wW];" // Minolta RAW
                + ".*\\.[nN][eE][fF];" // Nikon RAW
                , ";");
    }

    private RegexFileFilter createAcceptedDngFilesFiter() {
        return new RegexFileFilter(
                ".*\\.[dD][nN][gG];" // Digal Negative
                , ";");
    }

    private RegexFileFilter createAcceptedJpegFilesFiter() {
        return new RegexFileFilter(
                ".*\\.[jJ][pP][gG];" // Joint Photographic Experts Group
                + ".*\\.[jJ][pP][eE][gG];" // Joint Photographic Experts Group
                , ";");
    }

    private RegexFileFilter createAcceptedTiffFilesFiter() {
        return new RegexFileFilter(
                ".*\\.[tT][iI][fF];" // Tagged Image File Format
                + ".*\\.[tT][iI][fF][fF];" // Tagged Image File Format
                , ";");
    }

    private RegexFileFilter createUserDefindedFileTypesFilter() {
        List<UserDefinedFileType> fileTypes = DatabaseUserDefinedFileTypes.INSTANCE.getAll();

        if (fileTypes.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String delimiter = ";";
        boolean isFirst = true;

        for (UserDefinedFileType fileType : fileTypes) {
            String ignoreCaseuffix = toIgnoreCasePattern(fileType.getSuffix());

            sb.append(isFirst ? "" : ";").append(".*\\.").append(ignoreCaseuffix);
            isFirst = false;
        }

        return new RegexFileFilter(sb.toString(), delimiter);
    }

    private String toIgnoreCasePattern(String pattern) {
        int patternLength = pattern.length();

        StringBuilder sb = new StringBuilder(patternLength * 2);

        for (int index = 0; index < patternLength; index++) {
            char character = pattern.charAt(index);

            sb.append("[");
            sb.append(Character.toUpperCase(character));
            sb.append(Character.toLowerCase(character));
            sb.append("]");
        }

        return sb.toString();
    }

    private void addRawImageFileExperimentalPatterns(RegexFileFilter filter) {
        filter.addAcceptPattern(".*\\.[sS][rR][wW]");    // Samsung RAW
        filter.addAcceptPattern(".*\\.[aA][rR][wW]");    // Sony (Alpha) RAW
    }

    private void initExperimentalFileFormatDescriptions() {
        EXPERIMENTAL_FILE_FORMAT_DESCRIPTIONS.add("Samsung RAW (*.srw)");
        EXPERIMENTAL_FILE_FORMAT_DESCRIPTIONS.add("Sony Alpha RAW (*.arw)");
    }

    public static List<String> getExperimentalFileFormatDescriptions() {
        return Collections.unmodifiableList(EXPERIMENTAL_FILE_FORMAT_DESCRIPTIONS);
    }

    private void initDisplaynames() {
        displayNameOfFilter.put(allAcceptedImageFilesFilter, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.ALL"));
        displayNameOfFilter.put(acceptedDngFilesFilter, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.DNG"));
        displayNameOfFilter.put(acceptedJpegFilesFilter, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.JPEG"));
        displayNameOfFilter.put(acceptedRawFilesFilter, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.RAW"));
        displayNameOfFilter.put(acceptedTiffFilesFilter, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.TIFF"));
        displayNameOfFilter.put(userDefinedFileTypesFilter, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.UserDefinedFileTypes"));
        displayNameOfFilter.put(NO_XMP, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.NoXmp"));
        displayNameOfFilter.put(XMP_RATING_1_STAR, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.1Star"));
        displayNameOfFilter.put(XMP_RATING_2_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.2Stars"));
        displayNameOfFilter.put(XMP_RATING_3_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.3Stars"));
        displayNameOfFilter.put(XMP_RATING_4_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.4Stars"));
        displayNameOfFilter.put(XMP_RATING_5_STARS, JptBundle.INSTANCE.getString("AppFileFilters.DisplayName.5Stars"));
    }

    public RegexFileFilter getAcceptedDngFilesFilter() {
        return new RegexFileFilter(acceptedDngFilesFilter);
    }

    public RegexFileFilter getAcceptedJpegFilesFilter() {
        return new RegexFileFilter(acceptedJpegFilesFilter);
    }

    public RegexFileFilter getAcceptedRawFilesFilter() {
        return new RegexFileFilter(acceptedRawFilesFilter);
    }

    public RegexFileFilter getAcceptedTiffFilesFilter() {
        return new RegexFileFilter(acceptedTiffFilesFilter);
    }

    public RegexFileFilter getAllAcceptedImageFilesFilter() {
        return new RegexFileFilter(allAcceptedImageFilesFilter);
    }

    /**
     *
     * @return maybe null
     */
    public RegexFileFilter getUserDefinedFileTypesFilter() {
        return userDefinedFileTypesFilter == null
                ? null
                : new RegexFileFilter(userDefinedFileTypesFilter);
    }

    public boolean isAcceptedImageFile(File imageFile) {
        return allAcceptedImageFilesFilter.accept(imageFile);
    }

    public boolean isUserDefinedFileType(File imageFile) {
        return userDefinedFileTypesFilter == null
                ? false
                : userDefinedFileTypesFilter.accept(imageFile);
    }

    /**
     * Returns the localized display name of a file filter.
     *
     * @param  filter one of the filters of this class
     * @return        display name or null if the filter is not a field of this
     *                class
     */
    public String getDisplaynameOf(FileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        return displayNameOfFilter.get(filter);
    }
}
