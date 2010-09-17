/*
 * @(#)ImageProperties.java    Created on 2008-02-17
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

package org.jphototagger.program.resource;

import com.imagero.util.R3;

/**
 *
 * @author Elmar Baumann
 */
public final class ImageProperties implements R3 {
    @Override
    public String[] get() {
        String[] s = new String[res.length];
        System.arraycopy(res, 0, s, 0, s.length);

        return s;
    }

    private final String[] res = { "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAABurO0ABXQADUVsbWFyIEJhdW1hbm50",
                                   "AApYTVAtVmlld2VydAAAdAAEbWV0YXQAF25vbi1jb21tZXJjaWFsIHVzZSBvbmx5",
                                   "dAACbm9xAH4AAnQACDA1LjExLjA3dAAKMTAuMTAuMjEwNnQABDIuOTl1cQB+AAAA",
                                   "AAAuMCwCFG0cderl/m4EJh5x2zEqI5nORpE/AhR3YHmZhUHoTfndNxTnrXQuQWve",
                                   "0Q==", }
    ;
}
