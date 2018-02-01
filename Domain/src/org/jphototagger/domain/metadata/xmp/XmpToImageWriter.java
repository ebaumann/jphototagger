package org.jphototagger.domain.metadata.xmp;

import java.io.File;

/**
 * Service Proider interface for writing metadata of an XMP file into an image
 * file. Multiple writes can exist, e. g. one writer may embed XMP into an image
 * file and another writes IPTC into the same image file.
 * <p>
 * Each writer will be called after an XMP file was created or modified.
 *
 * @author Elmar Baumann
 */
public interface XmpToImageWriter {

    /**
     * Writes the metadata contents of an XMP file into an image file.
     *
     * @param xmpFile   XMP file which contains the metadata (source)
     * @param imageFile image file into which the metadata of the XMP file
     *                  should be written (target)
     *
     * @return true, if the image file was modified. false, if the image file
     *         was not modified, e.g., when the user decided, that a specific
     *         writer should not modify the image file or if the image file has
     *         an unsupported format.
     */
    boolean write(File xmpFile, File imageFile);
}
