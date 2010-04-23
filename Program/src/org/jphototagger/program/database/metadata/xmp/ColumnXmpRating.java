/*
 * @(#)ColumnXmpRating.java    Created on 2009-07-31
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

package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.inputverifier.InputVerifierNumberRange;

import javax.swing.InputVerifier;

/**
 * Spalte <code>rating</code> der Tabelle <code>xmp</code>.
 *
 * @author  Martin Pohlack
 */
public final class ColumnXmpRating extends Column {
    public static final ColumnXmpRating INSTANCE = new ColumnXmpRating();

    private ColumnXmpRating() {
        super("rating", "xmp", DataType.BIGINT);
        setLength(1);
        setDescription(
            JptBundle.INSTANCE.getString("ColumnXmpRating.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString("ColumnXmpRating.LongerDescription"));
    }

    /**
     * Returns the minimum rating value. Lower values are treated as not
     * rated and should be set to null.
     *
     * @return minimum rating value
     */
    public static int getMinValue() {
        return 0;
    }

    /**
     * Returns the minimum rating value. Higher values shoul be set to this
     * value.
     *
     * @return minimum rating value
     */
    public static int getMaxValue() {
        return 5;
    }

    @Override
    public InputVerifier getInputVerifier() {
        return new InputVerifierNumberRange(1, 5);
    }
}
