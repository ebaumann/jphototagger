package org.jphototagger.program.filefilter;

import org.jphototagger.program.image.metadata.xmp.XmpMetadata;

import java.io.File;
import java.io.FileFilter;

/**
 * Accepts files with no sidecar files
 * ({@link XmpMetadata#hasImageASidecarFile(File)} == false).
 *
 * @author Elmar Baumann
 */
public final class NoXmpFileFilter implements FileFilter {
    public static final NoXmpFileFilter INSTANCE = new NoXmpFileFilter();

    @Override
    public boolean accept(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return !XmpMetadata.hasImageASidecarFile(imageFile);
    }

    private NoXmpFileFilter() {}
}
