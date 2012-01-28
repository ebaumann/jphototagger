package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeUpdatedEvent;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.util.Bundle;

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
    private final Set<String> allAcceptedSuffixes = new HashSet<String>();
    private final Set<String> acceptedRawSuffixes = new HashSet<String>();
    private final Set<String> userDefinedFileTypesSuffixes = new HashSet<String>();
    private RegexFileFilter allAcceptedImageFilesFilter;
    private final RegexFileFilter acceptedRawFilesFilter;
    private final RegexFileFilter acceptedDngFilesFilter;
    private final RegexFileFilter acceptedJpegFilesFilter;
    private final RegexFileFilter acceptedTiffFilesFilter;
    private RegexFileFilter userDefinedFileTypesFilter;
    public static final AppFileFilters INSTANCE = new AppFileFilters();

    private AppFileFilters() {
        acceptedDngFilesFilter = createAcceptedDngFilesFiter();
        acceptedJpegFilesFilter = createAcceptedJpegFilesFiter();
        acceptedTiffFilesFilter = createAcceptedTiffFilesFiter();
        acceptedRawFilesFilter = createAcceptedRawFilesFilter();
        userDefinedFileTypesFilter = createUserDefinedFileFilter();
        // Has invoked after all others!
        allAcceptedImageFilesFilter = createAllAcceptedImagesFileFilter();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    public RegexFileFilter createAcceptedRawFilesFilter() {
        setAcceptedRawFilesSuffixes();
        RegexFileFilter filter = createRegexFileFilterFromSuffixes(acceptedRawSuffixes);
        filter.setDisplayname(Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.RAW"));

        return filter;
    }

    private RegexFileFilter createAcceptedDngFilesFiter() {
        RegexFileFilter filter = new RegexFileFilter(
                ".*\\.[dD][nN][gG];" // Digal Negative
                , ";");

        filter.setDisplayname(Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.DNG"));

        return filter;
    }

    private RegexFileFilter createAcceptedJpegFilesFiter() {
        RegexFileFilter filter = new RegexFileFilter(
                ".*\\.[jJ][pP][gG];" // Joint Photographic Experts Group
                + ".*\\.[jJ][pP][eE][gG];" // Joint Photographic Experts Group
                , ";");
        filter.setDisplayname(Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.JPEG"));

        return filter;
    }

    private RegexFileFilter createAcceptedTiffFilesFiter() {
        RegexFileFilter filter = new RegexFileFilter(
                ".*\\.[tT][iI][fF];" // Tagged Image File Format
                + ".*\\.[tT][iI][fF][fF];" // Tagged Image File Format
                , ";");
        filter.setDisplayname(Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.TIFF"));

        return filter;
    }

    private synchronized void setAcceptedRawFilesSuffixes() {
        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        acceptedRawSuffixes.clear();
        for (ThumbnailCreator tnCreator : tnCreators) {
            Set<String> tnCreatorSuffixes = tnCreator.getSupportedRawFormatFileTypeSuffixes();

            acceptedRawSuffixes.addAll(tnCreatorSuffixes);
        }
    }

    public synchronized RegexFileFilter createUserDefinedFileFilter() {
        setUserDefindedFileTypesSuffixes();
        RegexFileFilter filter = userDefinedFileTypesSuffixes.isEmpty()
                ? null
                : createRegexFileFilterFromSuffixes(userDefinedFileTypesSuffixes);

        if (filter != null) {
            filter.setDisplayname(Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.UserDefinedFileTypes"));
        }

        return filter;
    }

    private synchronized void setUserDefindedFileTypesSuffixes() {
        UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);

        if (repo == null) {
            return;
        }

        List<UserDefinedFileType> fileTypes = repo.findAllUserDefinedFileTypes();

        userDefinedFileTypesSuffixes.clear();
        for (UserDefinedFileType fileType : fileTypes) {
            userDefinedFileTypesSuffixes.add(fileType.getSuffix());
        }
    }

    public synchronized RegexFileFilter createAllAcceptedImagesFileFilter() {
        setAllAcceptedImagesFileSuffixes();
        RegexFileFilter filter = createRegexFileFilterFromSuffixes(allAcceptedSuffixes);
        filter.setDisplayname(Bundle.getString(AppFileFilters.class, "AppFileFilters.DisplayName.ALL"));

        return filter;
    }

    private synchronized void setAllAcceptedImagesFileSuffixes() {
        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        allAcceptedSuffixes.clear();
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

    public synchronized RegexFileFilter getAllAcceptedImageFilesFilter() {
        return new RegexFileFilter(allAcceptedImageFilesFilter);
    }

    /**
     *
     * @return maybe null
     */
    public synchronized RegexFileFilter getUserDefinedFileTypesFilter() {
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

    @EventSubscriber(eventClass = UserDefinedFileTypeInsertedEvent.class)
    public void userDefinedFileFilterInserted(UserDefinedFileTypeInsertedEvent evt) {
        reCreateUserDefinedAndAllAcceptedFilters();
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeUpdatedEvent.class)
    public void userDefinedFileFilterUpdated(UserDefinedFileTypeUpdatedEvent evt) {
        reCreateUserDefinedAndAllAcceptedFilters();
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeDeletedEvent.class)
    public void userDefinedFileFilterDeleted(UserDefinedFileTypeDeletedEvent evt) {
        reCreateUserDefinedAndAllAcceptedFilters();
    }

    private synchronized void reCreateUserDefinedAndAllAcceptedFilters() {
        userDefinedFileTypesFilter = createUserDefinedFileFilter();
        allAcceptedImageFilesFilter = createAllAcceptedImagesFileFilter();
    }
}
