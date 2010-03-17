/*
 * @(#)FilenameFormatFilenamePostfix.java    2008-10-13
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

package de.elmar_baumann.jpt.controller.filesystem;

/**
 * Returns the postfix of a file.
 *
 * @author  Elmar Baumann
 */
public final class FilenameFormatFilenamePostfix extends FilenameFormat {

    /**
     * Postfix prepanded by a period.
     *
     * @return postfix
     */
    @Override
    public String format() {
        String filename = getFile().getName();
        int    index    = filename.lastIndexOf(".");

        return (index >= 0)
               ? filename.substring(index)
               : "";
    }
}
