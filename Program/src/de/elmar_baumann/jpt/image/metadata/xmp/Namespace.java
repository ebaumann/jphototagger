/*
 * @(#)Namespace.java    Created on 2010-01-27
 *
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

package de.elmar_baumann.jpt.image.metadata.xmp;

/**
 * Namespaces not existing in {@link com.adobe.xmp.XMPConst}.
 *
 * @author  Elmar Baumann
 */
public enum Namespace {
    LIGHTROOM("http://ns.adobe.com/lightroom/1.0/", "lr"),
    ;

    private final String uri;
    private final String prefix;

    private Namespace(String uri, String prefix) {
        this.uri    = uri;
        this.prefix = prefix;
    }

    public String getUri() {
        return uri;
    }

    public String getPrefix() {
        return prefix;
    }
}
