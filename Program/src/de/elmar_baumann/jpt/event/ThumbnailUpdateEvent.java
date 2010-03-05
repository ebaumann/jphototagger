/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.event;

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
