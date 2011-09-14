package org.jphototagger.program.app;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jphototagger.api.image.ThumbnailCreator;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.filefilter.NoXmpFileFilter;
import org.jphototagger.program.filefilter.XmpRatingFileFilter;
import org.openide.util.Lookup;

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
    private final Set<String> allAcceptedSuffixes = new HashSet<String>();
    private final Set<String> acceptedRawSuffixes = new HashSet<String>();
    private final Set<String> userDefinedFileTypesSuffixes = new HashSet<String>();
    private final RegexFileFilter allAcceptedImageFilesFilter;
    private final RegexFileFilter acceptedRawFilesFilter;
    private final RegexFileFilter acceptedDngFilesFilter;
    private final RegexFileFilter acceptedJpegFilesFilter;
    private final RegexFileFilter acceptedTiffFilesFilter;
    private final RegexFileFilter userDefinedFileTypesFilter;
    public static final AppFileFilters INSTANCE = new AppFileFilters();

    private AppFileFilters() {
        acceptedDngFilesFilter = createAcceptedDngFilesFiter();
        acceptedJpegFilesFilter = createAcceptedJpegFilesFiter();
        acceptedTiffFilesFilter = createAcceptedTiffFilesFiter();

        setAcceptedRawFilesFiter();
        acceptedRawFilesFilter = createRegexFileFilterFromSuffixes(acceptedRawSuffixes);

        setUserDefindedFileTypesSuffixes();
        userDefinedFileTypesFilter = userDefinedFileTypesSuffixes.isEmpty()
                ? null
                : createRegexFileFilterFromSuffixes(userDefinedFileTypesSuffixes);

        // Has invoked after all others!
        setAllAcceptedImagesFileFilter();
        allAcceptedImageFilesFilter = createRegexFileFilterFromSuffixes(allAcceptedSuffixes);

        initDisplaynames();
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

    private void setAcceptedRawFilesFiter() {
        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        for (ThumbnailCreator tnCreator : tnCreators) {
            Set<String> tnCreatorSuffixes = tnCreator.getSupportedRawFormatFileTypeSuffixes();

            acceptedRawSuffixes.addAll(tnCreatorSuffixes);
        }
    }

    private void setUserDefindedFileTypesSuffixes() {
        UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);

        if (repo == null) {
            return;
        }

        List<UserDefinedFileType> fileTypes = repo.findAllUserDefinedFileTypes();

        for (UserDefinedFileType fileType : fileTypes) {
            userDefinedFileTypesSuffixes.add(fileType.getSuffix());
        }
    }

    private void setAllAcceptedImagesFileFilter() {
        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        for (ThumbnailCreator tnCreator : tnCreators) {
            Set<String> tnCreatorSuffixes = tnCreator.getAllSupportedFileTypeSuffixes();

            allAcceptedSuffixes.addAll(tnCreatorSuffixes);
        }

        allAcceptedSuffixes.addAll(userDefinedFileTypesSuffixes);
    }

    private RegexFileFilter createRegexFileFilterFromSuffixes(Collection<? extends String> suffixes) {
        StringBuilder sb = new StringBuilder();
        String delimiter = ";";
        boolean isFirst = true;

        for (String suffix : suffixes) {
            String ignoreCaseuffix = toIgnoreCasePattern(suffix);

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

    private void initDisplaynames() {
        displayNameOfFilter.put(allAcceptedImageFilesFilter, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.ALL"));
        displayNameOfFilter.put(acceptedDngFilesFilter, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.DNG"));
        displayNameOfFilter.put(acceptedJpegFilesFilter, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.JPEG"));
        displayNameOfFilter.put(acceptedRawFilesFilter, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.RAW"));
        displayNameOfFilter.put(acceptedTiffFilesFilter, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.TIFF"));
        displayNameOfFilter.put(userDefinedFileTypesFilter, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.UserDefinedFileTypes"));
        displayNameOfFilter.put(NO_XMP, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.NoXmp"));
        displayNameOfFilter.put(XMP_RATING_1_STAR, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.1Star"));
        displayNameOfFilter.put(XMP_RATING_2_STARS, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.2Stars"));
        displayNameOfFilter.put(XMP_RATING_3_STARS, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.3Stars"));
        displayNameOfFilter.put(XMP_RATING_4_STARS, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.4Stars"));
        displayNameOfFilter.put(XMP_RATING_5_STARS, Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.5Stars"));
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
