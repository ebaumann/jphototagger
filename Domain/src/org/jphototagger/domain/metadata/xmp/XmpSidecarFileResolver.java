package org.jphototagger.domain.metadata.xmp;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface XmpSidecarFileResolver {

    /**
     * Uses {@link #suggestDefaultSidecarFile(java.io.File)} or {@link #suggestLongSidecarFile(java.io.File)} in
     * dependency of {@link #isUseLongXmpSidecarFilenames()}.
     *
     * @param contentFile
     * @return
     */
    File suggestXmpSidecarFile(File contentFile);

    /**
     * @param contentFile
     * @return content file name without suffix plus suffix "xmp". If the content file name is "myfile.tif", the sidecar
     * file name is "myfile.xmp".
     */
    File suggestDefaultSidecarFile(File contentFile);

    /**
     * @param contentFile
     * @return content file name with it's suffix plus the suffix "xmp". If the content file name is "myfile.tif", the
     * sidecar file name is "myfile.tif.xmp".
     */
    File suggestLongSidecarFile(File contentFile);

    /**
     * Finds the sidecar file if it exists whithin it's directory. Ignores the case of the filename.
     *
     * @param sidecarFile with a name such as "myfile.xmp" or "myfile.XMP"
     * @return existing sidecar file or null if a sidecar file does not exist. May be "myfile.XMP" (uppercase suffix) if
     * the name of {@code sidecarFile} is "myfile.xmp" (lowercase suffix).
     */
    File findSidecarFile(File sidecarFile);

    /**
     * Uses {@link #getDefaultXmpSidecarFileOrNullIfNotExists(java.io.File)} or {@link #getLongXmpSidecarFileOrNullIfNotExists(java.io.File)}
     * in dependency of {@link #isUseLongXmpSidecarFilenames()}.
     *
     * @param contentFile
     * @return
     */
    File getXmpSidecarFileOrNullIfNotExists(File contentFile);

    /**
     * Uses {@link #suggestDefaultSidecarFile(java.io.File)} and {@link #findSidecarFile(java.io.File)}.
     *
     * @param contentFile
     * @return
     */
    File getDefaultXmpSidecarFileOrNullIfNotExists(File contentFile);

    /**
     * Uses {@link #suggestLongSidecarFile(java.io.File)} and {@link #findSidecarFile(java.io.File)}.
     *
     * @param contentFile
     * @return
     */
    File getLongXmpSidecarFileOrNullIfNotExists(File contentFile);

    /**
     * @param contentFile
     * @return takes care of {@link #isUseLongXmpSidecarFilenames()}
     */
    boolean hasXmpSidecarFile(File contentFile);

    /**
     * @return true, if sidecar file names are the complete image file name plus ".xmp" ("image.jpg.xmp"), false, if the
     * sidecar file names are the image file name without extension (suffix) plus ".xmp" ("image.xmp")
     */
    boolean isUseLongXmpSidecarFilenames();
}
