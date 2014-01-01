package org.jphototagger.domain.metadata.xmp;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface XmpModifier {

    /**
     * Intented to read proprietary metadata from the sidecar file and set it as standardized
     * {@link org.jphototagger.domain.metadata.MetaDataValue} to a {@link Xmp} object.
     *
     * @param sidecarFile existing XMP file
     * @param xmp to modify, usually contains metadata
     * @return true if modified (so that the caller can re write the sidecar file)
     */
    boolean modifyXmp(File sidecarFile, Xmp xmp);
}
