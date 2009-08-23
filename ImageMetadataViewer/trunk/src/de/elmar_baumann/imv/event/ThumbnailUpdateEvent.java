package de.elmar_baumann.imv.event;

import java.io.File;

/**
 * Event signaling that a certain thumbnail has been modified somewhere up
 * the hierarchy.  This usually means, that a local representation needs to
 * be updated or recreated.
 *
 * @author  Martin Pohlack  <martinp@gmx.de>
 * @version 2009-08-18
 */
public final class ThumbnailUpdateEvent {

    private Type type;
    private File source;

    public enum Type {

        /** New thumbnail data available */
        THUMBNAIL_UPDATE,
        /** New XMP metadata available */
        XMP_UPDATE,
        /** New empty XMP metadata available */
        XMP_EMPTY_UPDATE,
        /** New rendered thumbnail data available */
        RENDERED_THUMBNAIL_UPDATE,
    };

    public ThumbnailUpdateEvent(File _file, Type _type) {
        source = _file;
        type = _type;
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

    /**
     * @param source the source to set
     */
    public void setSource(File source) {
        this.source = source;
    }
}
