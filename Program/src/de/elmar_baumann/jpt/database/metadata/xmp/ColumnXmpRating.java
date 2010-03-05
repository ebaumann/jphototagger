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

package de.elmar_baumann.jpt.database.metadata.xmp;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Column.DataType;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.componentutil.InputVerifierNumberRange;

import javax.swing.InputVerifier;

/**
 * Spalte <code>rating</code> der Tabelle <code>xmp</code>.
 *
 * @author  Martin Pohlack
 * @version 2009-07-31
 */
public final class ColumnXmpRating extends Column {
    public static final ColumnXmpRating INSTANCE = new ColumnXmpRating();

    private ColumnXmpRating() {
        super(TableXmp.INSTANCE, "rating", DataType.BIGINT);
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
     * @return minimum rating value.
     */
    public static int getMinValue() {
        return 1;
    }

    /**
     * Returns the minimum rating value. Higher values shoul be set to this
     * value.
     *
     * @return minimum rating value.
     */
    public static int getMaxValue() {
        return 5;
    }

    @Override
    public InputVerifier getInputVerifier() {
        return new InputVerifierNumberRange(1, 5);
    }
}
