/*
 * @(#)CanonMakerNotes.java    Created on 2010-01-13
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

package org.jphototagger.program.image.metadata.exif.formatter.canon;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.metadata.exif.ExifMakerNotes;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTags;

import java.io.File;

import java.util.ResourceBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class CanonMakerNotes implements ExifMakerNotes {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(
            "org/jphototagger/program/image/metadata/exif/formatter/canon/Bundle");

    @Override
    public void add(File file, ExifTags exifTags, ExifTag makerNoteTag) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        if (makerNoteTag == null) {
            throw new NullPointerException("makerNoteTag == null");
        }

        CanonIfd ifd = new CanonIfd(makerNoteTag.rawValue(),
                                    makerNoteTag.byteOrder());
        short[] tag1Values = CanonMakerNote.getTag1Values(file, ifd);
        short[] tag4Values = CanonMakerNote.getTag4Values(file, ifd);

        if (tag1Values != null) {
            CanonMakerNoteTag1Formatter.add(tag1Values, exifTags);
        }

        if (tag4Values != null) {
            CanonMakerNoteTag4Formatter.add(tag4Values, exifTags);
        }
    }

    static int tagId(int canonTag, int offset) {
        return ExifTag.Id.MAKER_NOTE_CANON_START.value() + canonTag * 100
               + offset;
    }

    static void addTag(ExifTags exifTags, int tagId, String nameBundleKey,
                       String value) {
        try {
            exifTags.addMakerNoteTag(new ExifTag(tagId, -1, -1, -1, null,
                    value, -1, BUNDLE.getString(nameBundleKey),
                    IfdType.MAKER_NOTE));
        } catch (Exception ex) {
            AppLogger.logSevere(CanonMakerNotes.class, ex);
        }
    }
}
