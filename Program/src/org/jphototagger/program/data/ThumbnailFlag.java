/*
 * @(#)ThumbnailFlag.java    Created on 2008-09-09
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

package org.jphototagger.program.data;

import org.jphototagger.program.resource.JptBundle;

import java.awt.Color;

/**
 * Flag of a Thumbnail.
 *
 * @author  Elmar Baumann
 */
public final class ThumbnailFlag {
    private final Color               color;
    private final String              string;
    public static final ThumbnailFlag ERROR_FILE_NOT_FOUND =
        new ThumbnailFlag(
            Color.RED,
            JptBundle.INSTANCE.getString("ThumbnailFlag.Error.FileNotFound"));

    public ThumbnailFlag(Color color, String string) {
        this.color  = color;
        this.string = string;
    }

    public Color getColor() {
        return color;
    }

    /**
     *
     * @return Meaning of the flag
     */
    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ThumbnailFlag other = (ThumbnailFlag) obj;

        if ((this.color != other.color)
                && ((this.color == null) ||!this.color.equals(other.color))) {
            return false;
        }

        if ((this.string == null) ||!this.string.equals(other.string)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 71 * hash + ((this.color != null)
                            ? this.color.hashCode()
                            : 0);
        hash = 71 * hash + ((this.string != null)
                            ? this.string.hashCode()
                            : 0);

        return hash;
    }
}
