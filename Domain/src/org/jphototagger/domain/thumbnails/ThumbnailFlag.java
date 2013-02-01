package org.jphototagger.domain.thumbnails;

import java.awt.Color;
import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public enum ThumbnailFlag implements PositionProvider {

    HAS_SIDECAR_FILE(Color.BLUE, 0, "ThumbnailFlag.HasSidecarFile"),
    ERROR_FILE_NOT_FOUND(Color.RED, 1, "ThumbnailFlag.FileNotFound"),
    ;

    private final Color color;
    private final String displayName;
    private final int position;

    private ThumbnailFlag(Color color, int position, String bundleKey) {
        this.color = color;
        this.position = position;
        this.displayName = Bundle.getString(ThumbnailFlag.class, bundleKey);
    }

    public Color getColor() {
        return color;
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
}
