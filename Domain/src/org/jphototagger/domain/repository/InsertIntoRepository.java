package org.jphototagger.domain.repository;

/**
 *
 * @author Elmar Baumann
 */
public enum InsertIntoRepository {

    /**
     * Insert or update EXIF, thumbnail and XMP against these rules:
     *
     * <ul>
     * <li>Update EXIF, if the image file's file system last modified
     *     timestamp is not equal to it's repository's timestamp
     *     <code>files.lastmodified</code></li>
     * <li>Insert or update the thumbnail, if the image file's file system
     *     last modified timestamp is not equal to it's repoistory's timestamp
     *     <code>files.lastmodified</code></li>
     * <li>Insert or update XMP if the XMP sidecar file's file system last
     *     modified timestamp is not equal to it's repository's timestamp
     *     <code>files.xmp_lastmodified</code></li>
     * </ul>
     */
    OUT_OF_DATE,
    /**
     * Insert or update the image file's EXIF metadata regardless of
     * timestamps
     */
    EXIF,
    /**
     * Insert or update the image file's thumbnail regardless of timestamps
     */
    THUMBNAIL,
    /**
     * Insert or update the image file's XMP metadata from it's XMP sidecar
     * file regardless of timestamps
     */
    XMP
}
