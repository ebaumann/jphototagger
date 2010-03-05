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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.resource.JptBundle;

/**
 * Filename format that returns exactly a set string (formats nothing).
 *
 * @author  Elmar Baumann
 * @version 2008-10-13
 */
public final class FilenameFormatConstantString extends FilenameFormat {

    public FilenameFormatConstantString(String string) {
        setFormat(string);
    }

    /**
     * Returns false.
     *
     * @return false
     */
    @Override
    public boolean isDynamic() {
        return false;
    }

    /**
     * Returns {@link FilenameFormat#getFormat()}.
     *
     * @return format
     */
    @Override
    public String format() {
        return getFormat();
    }

    @Override
    public String toString() {
        return JptBundle.INSTANCE.getString("FilenameFormatConstantString.String");
    }

    private FilenameFormatConstantString() {
    }
}
