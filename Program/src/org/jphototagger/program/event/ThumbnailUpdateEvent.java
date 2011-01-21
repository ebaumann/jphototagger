package org.jphototagger.program.event;

import java.io.File;

/**
 * Event signaling that a certain thumbnail has been modified somewhere up
 * the hierarchy.  This usually means, that a local representation needs to
 * be updated or recreated.
 *
 * @author  Martin Pohlack
 */
public final class ThumbnailUpdateEvent {
    private final Type type;
    private final File source;

    public enum Type {

        /** New thumbnail data available */
        THUMBNAIL_UPDATE,

        /** New XMP metadata available */
        XMP_UPDATE,

        /** New empty XMP metadata available */
        XMP_EMPTY_UPDATE,

        /** New rendered thumbnail data available */
        RENDERED_THUMBNAIL_UPDATE,
    }

    ;
    public ThumbnailUpdateEvent(File _file, Type _type) {
        if (_file == null) {
            throw new NullPointerException("_file == null");
        }
        if (_type == null) {
            throw new NullPointerException("_type == null");
        }

        source = _file;
        type   = _type;
    }

    /**
     * @return Typ
     */
    public Type getType() {
        return type;
    }

    /**
     * @return the source
     */
    public File getSource() {
        return source;
    }
}
