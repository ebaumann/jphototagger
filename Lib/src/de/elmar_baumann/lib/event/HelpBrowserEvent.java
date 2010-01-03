/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.event;

import java.net.URL;

/**
 * Action of an {@link de.elmar_baumann.lib.dialog.HelpBrowser} instance.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-04
 */
public final class HelpBrowserEvent {

    private final Object source;
    private final Type type;
    private final URL url;

    public HelpBrowserEvent(Object source, Type type, URL url) {
        if (source == null)
            throw new NullPointerException("source == null");
        if (type == null)
            throw new NullPointerException("type == null");
        if (url == null)
            throw new NullPointerException("url == null");

        this.source = source;
        this.type = type;
        this.url = url;
    }

    public enum Type {

        /**
         * The URL displayed changed
         */
        URL_CHANGED
    }

    /**
     * Returns the action type.
     *
     * @return action type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the url if the action was {@link Type#URL_CHANGED}.
     *
     * @return URL or null if unaprobriate action
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns the source of the action, usually an instance of
     * {@link de.elmar_baumann.lib.dialog.HelpBrowser}
     *
     * @return instance
     */
    public Object getSource() {
        return source;
    }
}
