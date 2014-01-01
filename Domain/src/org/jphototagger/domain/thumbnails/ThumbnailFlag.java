package org.jphototagger.domain.thumbnails;

import java.awt.Color;
import java.io.File;
import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public enum ThumbnailFlag implements PositionProvider {

    HAS_SIDECAR_FILE(new Color(98, 98, 98), Color.WHITE, 0, "ThumbnailFlag.HasSidecarFile"),
    ERROR_FILE_NOT_FOUND(Color.RED, Color.WHITE, 1, "ThumbnailFlag.FileNotFound"),;

    private final Color fillColor;
    private final Color borderColor;
    private final String displayName;
    private final int position;
    private final XmpSidecarFileResolver sidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    private ThumbnailFlag(Color fillColor, Color borderColor, int position, String bundleKey) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.position = position;
        this.displayName = Bundle.getString(ThumbnailFlag.class, bundleKey);
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public int getPosition() {
        return position;
    }

    /**
     * @param file null allowed (return: false)
     * @return
     */
    public boolean matches(File file) {
        if (file == null) {
            return false;
        }
        switch (this) {
            case ERROR_FILE_NOT_FOUND:
                return !file.exists();
            case HAS_SIDECAR_FILE:
                return sidecarFileResolver != null && sidecarFileResolver.hasXmpSidecarFile(file);
            default:
                throw new IllegalStateException("Not handled: " + this);
        }
    }
}
