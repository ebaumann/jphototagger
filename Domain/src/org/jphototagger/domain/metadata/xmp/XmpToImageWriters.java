package org.jphototagger.domain.metadata.xmp;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class XmpToImageWriters {

    /**
     * Calls {@link XmpToImageWriter#write(java.io.File, java.io.File)} of each
     * writer provided by the Service Provider Interface. Catches and logs
     * Exceptions. If one writer throws an exception, an other can write.
     *
     * @param xmpFile   XMP file, which contents should be written into the
     *                  image file
     * @param imageFile image file in which the XMP contets should be written
     *
     * @return true, if the image file was modified by a writer
     */
    public static boolean write(File xmpFile, File imageFile) {
        boolean written = false;

        for (XmpToImageWriter writer : Lookup.getDefault().lookupAll(XmpToImageWriter.class)) {
            try {
                if (writer.write(xmpFile, imageFile)) {
                    written = true;
                }
            } catch (Throwable t) {
                Logger.getLogger(XmpToImageWriters.class.getName()).log(Level.SEVERE, null, t);
            }
        }

        return written;
    }

    private XmpToImageWriters() {
    }
}
