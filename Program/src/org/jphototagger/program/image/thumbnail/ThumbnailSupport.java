package org.jphototagger.program.image.thumbnail;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jphototagger.lib.io.FileUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ThumbnailSupport {

    public static final ThumbnailSupport INSTANCE = new ThumbnailSupport();
    private static final Set<String> SUPPORTED_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Set<String> SCALED_IMAGE_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Set<String> EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Set<String> RAW_FORMAT_SUFFIXES_LOWERCASE = new HashSet<String>();

    static {
        SCALED_IMAGE_SUFFIXES_LOWERCASE.add("jpeg"); // Joint Photographic Experts Group
        SCALED_IMAGE_SUFFIXES_LOWERCASE.add("jpg"); // Joint Photographic Experts Group
        SCALED_IMAGE_SUFFIXES_LOWERCASE.add("tif"); // Tagged Image File Format
        SCALED_IMAGE_SUFFIXES_LOWERCASE.add("tiff"); // Tagged Image File Format

        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("arw"); // Sony (Alpha) RAW
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("cr2"); // Canon RAW
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("crw"); // Canon RAW 2
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("dcr"); // Kodak RAW
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("dng"); // Digital Negative
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("jpeg"); // Joint Photographic Experts Group
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("jpg"); // Joint Photographic Experts Group
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("mrw"); // Minolta RAW
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("nef"); // Nikon RAW
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("srw"); // Samsung RAW
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("thm"); // EXIF Info
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("tif"); // Tagged Image File Format
        EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.add("tiff"); // Tagged Image File Format

        RAW_FORMAT_SUFFIXES_LOWERCASE.add("arw"); // Sony (Alpha) RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("cr2"); // Canon RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("crw"); // Canon RAW 2
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("dcr"); // Kodak RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("dng"); // Digital Negative
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("mrw"); // Minolta RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("nef"); // Nikon RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("srw"); // Samsung RAW

        SUPPORTED_SUFFIXES_LOWERCASE.addAll(SCALED_IMAGE_SUFFIXES_LOWERCASE);
        SUPPORTED_SUFFIXES_LOWERCASE.addAll(EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE);
        SUPPORTED_SUFFIXES_LOWERCASE.addAll(RAW_FORMAT_SUFFIXES_LOWERCASE);
    }

    public boolean canCreateThumbnail(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();

        return SCALED_IMAGE_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    public boolean canCreateThumbnail(String suffix) {
        return SCALED_IMAGE_SUFFIXES_LOWERCASE.contains(suffix.toLowerCase());
    }

    public boolean canCreateEmbeddedThumbnail(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();

        return EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    public boolean canCreateEmbeddedThumbnail(String suffix) {
        return EMBEDDED_THUMBNAILS_SUFFIXES_LOWERCASE.contains(suffix.toLowerCase());
    }

    public Set<String> getSupportedFileTypeSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_SUFFIXES_LOWERCASE);
    }

    public Set<String> getSupportedRawFormatFileTypeSuffixes() {
        return Collections.unmodifiableSet(RAW_FORMAT_SUFFIXES_LOWERCASE);
    }

    private ThumbnailSupport() {
    }
}
