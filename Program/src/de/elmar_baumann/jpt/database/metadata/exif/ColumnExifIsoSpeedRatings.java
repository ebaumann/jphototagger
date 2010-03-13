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

package de.elmar_baumann.jpt.database.metadata.exif;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.resource.JptBundle;

/**
 * Tabellenspalte <code>exif_iso_speed_ratings</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: ISOSpeedRatings;</li>
 * <li>EXIF Tag-ID: 34855 (Hex: 8827); EXIF-IFD: .</li>
 * </ul>
 *
 * @author  Elmar Baumann
 * @version 2008-08-27
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
