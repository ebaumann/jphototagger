package org.jphototagger.domain.metadata.xmp;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface XmpSidecarFileResolver {

    /**
     * @param contentFile
     * @return takes care of {@link #isUseLongXmpSidecarFilenames()}
     */
    File suggestXmpSidecarFile(File contentFile);

    File suggestDefaultSidecarFile(File contentFile);

    File suggestLongSidecarFile(File contentFile);

    /**
     * @param contentFile
     * @return takes care of {@link #isUseLongXmpSidecarFilenames()}
     */
    File getXmpSidecarFileOrNullIfNotExists(File contentFile);

    File getDefaultXmpSidecarFileOrNullIfNotExists(File contentFile);

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
