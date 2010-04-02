/*
 * @(#)NoXmpFileFilter.java    Created on 2010-03-30
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

package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;

/**
 * Accepts files with no sidecar files
 * ({@link XmpMetadata#hasImageASidecarFile(File)} == false).
 *
 * @author Elmar Baumann
 */
public final class NoXmpFileFilter implements FileFilter {

    public static final NoXmpFileFilter INSTANCE = new NoXmpFileFilter();

    @Override
    public boolean accept(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        return !XmpMetadata.hasImageASidecarFile(imageFile);
    }

    private NoXmpFileFilter() {
    }

}
