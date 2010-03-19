/*
 * @(#)ExifMakerNotesFactory.java    Created on 2010-01-13
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

package de.elmar_baumann.jpt.image.metadata.exif;

import de.elmar_baumann.jpt.image.metadata.exif.formatter.canon.CanonMakerNotes;
import de.elmar_baumann.jpt.image.metadata.exif.formatter.nikon.NikonMakerNotes;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  Elmar Baumann
 */
public final class ExifMakerNotesFactory {
    private static final Map<String, ExifMakerNotes> makerNotesOfMake =
        new HashMap<String, ExifMakerNotes>();

    static {
        makerNotesOfMake.put("nikon", new NikonMakerNotes());
        makerNotesOfMake.put("canon", new CanonMakerNotes());
    }

    static void add(File file, ExifTags exifTags) {
        ExifTag makerNoteTag =
            exifTags.exifTagById(ExifTag.Id.MAKER_NOTE.value());
        ExifTag makeTag = exifTags.exifTagById(ExifTag.Id.MAKE.value());
        String  make    = (makeTag == null)
                          ? null
                          : makeTag.stringValue().toLowerCase();

        if ((makeTag != null) && (makerNoteTag != null)) {
            for (String mk : makerNotesOfMake.keySet()) {
                if (make.contains(mk)) {
                    makerNotesOfMake.get(mk).add(file, exifTags, makerNoteTag);
                }
            }
        }
    }

    private ExifMakerNotesFactory() {}
}
