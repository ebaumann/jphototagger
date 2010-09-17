/*
 * @(#)MathUtil.java    Created on 2009-02-20
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

package org.jphototagger.lib.util;

/**
 * Math utils.
 *
 * @author Elmar Baumann
 */
public final class MathUtil {

    /**
     * Returns whether a double value is integer (has no decimal places).
     *
     * @param  value  double value
     * @return true if the value is integer
     */
    public static boolean isInteger(double value) {
        return value - java.lang.Math.floor(value) == 0;
    }

    private MathUtil() {}
}
