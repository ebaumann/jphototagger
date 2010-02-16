/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database.metadata.exif;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Column.DataType;
import de.elmar_baumann.jpt.resource.JptBundle;

/**
 * Tabellenspalte <code>exif_recording_equipment</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: Model (Recording Equipment Model)</li>
 * <li>EXIF Tag-ID: 272 (Hex: 110).</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class ColumnExifRecordingEquipment extends Column {

    public static final ColumnExifRecordingEquipment INSTANCE = new ColumnExifRecordingEquipment();

    private ColumnExifRecordingEquipment() {
        super(
            TableExif.INSTANCE,
            "exif_recording_equipment",
            DataType.STRING);

        setLength(125);
        setDescription(JptBundle.INSTANCE.getString("ColumnExifRecordingEquipment.Description"));
    }
}
