/*
 * @(#)ColumnExifIsoSpeedRatings.java    Created on 2008-08-27
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

package org.jphototagger.program.database.metadata.exif;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.resource.JptBundle;

/**
 * Tabellenspalte <code>exif_iso_speed_ratings</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: ISOSpeedRatings;</li>
 * <li>EXIF Tag-ID: 34855 (Hex: 8827); EXIF-IFD: .</li>
 * </ul>
 *
 * @author  Elmar Baumann
 */
public final class ColumnExifIsoSpeedRatings extends Column {
    public static final ColumnExifIsoSpeedRatings INSTANCE =
        new ColumnExifIsoSpeedRatings();

    private ColumnExifIsoSpeedRatings() {
        super("exif_iso_speed_ratings", "exif", DataType.SMALLINT);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnExifIsoSpeedRatings.Description"));
    }
}
