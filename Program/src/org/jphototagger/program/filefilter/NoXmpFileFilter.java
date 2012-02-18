package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;

import org.openide.util.Lookup;

import org.jphototagger.api.component.DisplayNameProvider;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.util.Bundle;

/**
 * Accepts files with no sidecar files
 * ({@code XmpMetadata#hasImageASidecarFile(File)} == false).
 *
 * @author Elmar Baumann
 */
public final class NoXmpFileFilter implements FileFilter, DisplayNameProvider {

    public static final NoXmpFileFilter INSTANCE = new NoXmpFileFilter();
    private static final String DISPLAY_NAME = Bundle.getString(NoXmpFileFilter.class, "NoXmpFileFilter.DisplayName.NoXmp");
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    @Override
    public boolean accept(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        return !xmpSidecarFileResolver.hasXmpSidecarFile(imageFile);
    }

    private NoXmpFileFilter() {
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
}
