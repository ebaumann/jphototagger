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
    private static final Set<String> CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Set<String> CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE = new HashSet<String>();

    static {
        CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE.add("jpeg"); // Joint Photographic Experts Group
        CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE.add("jpg"); // Joint Photographic Experts Group
        CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE.add("tif"); // Tagged Image File Format
        CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE.add("tiff"); // Tagged Image File Format

        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("arw"); // Sony (Alpha) RAW
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("cr2"); // Canon RAW
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("crw"); // Canon RAW 2
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("dcr"); // Kodak RAW
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("dng"); // Digital Negative
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("jpeg"); // Joint Photographic Experts Group
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("jpg"); // Joint Photographic Experts Group
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("mrw"); // Minolta RAW
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("nef"); // Nikon RAW
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("srw"); // Samsung RAW
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("thm"); // EXIF Info
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("tif"); // Tagged Image File Format
        CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.add("tiff"); // Tagged Image File Format

        SUPPORTED_SUFFIXES_LOWERCASE.addAll(CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE);
        SUPPORTED_SUFFIXES_LOWERCASE.addAll(CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE);
    }

    public boolean canCreateThumbnail(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();

        return CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    public boolean canCreateThumbnail(String suffix) {
        return CAN_CREATE_SCALED_IMAGE_SUFFIXES_LOWERCASE.contains(suffix.toLowerCase());
    }

    public boolean canCreateEmbeddedThumbnail(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();

        return CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    public boolean canCreateEmbeddedThumbnail(String suffix) {
        return CAN_CREATE_EMBEDDED_THUMBNAIL_SUFFIXES_LOWERCASE.contains(suffix.toLowerCase());
    }

    public Set<String> getSupportedFileTypeSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_SUFFIXES_LOWERCASE);
    }

    private ThumbnailSupport() {
    }
}
