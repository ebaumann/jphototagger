package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;

import org.jphototagger.lib.renderer.DisplayNameProvider;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Accepts files with no sidecar files
 * ({@link XmpMetadata#hasImageASidecarFile(File)} == false).
 *
 * @author Elmar Baumann
 */
public final class NoXmpFileFilter implements FileFilter, DisplayNameProvider {

    public static final NoXmpFileFilter INSTANCE = new NoXmpFileFilter();
    private static final String DISPLAY_NAME = Bundle.getString(NoXmpFileFilter.class, "NoXmpFileFilter.DisplayName.NoXmp");

    @Override
    public boolean accept(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return !XmpMetadata.hasImageASidecarFile(imageFile);
    }

    private NoXmpFileFilter() {
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
}
