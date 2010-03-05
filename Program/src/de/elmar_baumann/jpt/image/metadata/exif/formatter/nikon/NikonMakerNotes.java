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
package de.elmar_baumann.jpt.image.metadata.exif.formatter.nikon;

import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMakerNotes;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata.IfdType;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTags;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-30
 */
public final class NikonMakerNotes implements ExifMakerNotes {

    private static final String                     PROPERTY_FILE_PREFIX = "de/elmar_baumann/jpt/resource/properties/NikonExifMakerNote_";
    private static final Collection<NikonMakerNote> MAKER_NOTES          = new ArrayList<NikonMakerNote>();

    static {
        int       index    = 0;
        final int maxIndex = 100;
        while (index <= maxIndex) {

            try {
                // Better than catching an exception?
                //    java.util.jar.JarFile jarFile = new java.util.jar.JarFile(".../JPhotoTagger.jar");
                //    Enumeration entries = jarFile.entries();
                //    while (entries.hasMoreElements()) {
                //        java.util.zip.ZipEntry entry = (java.util.zip.ZipEntry) entries.nextElement();
                //        if (entry.getName().startsWith(PROPERTY_FILE_PREFIX) ...
                //    }
                ResourceBundle bundle = ResourceBundle.getBundle(PROPERTY_FILE_PREFIX + Integer.toString(index++));
                MAKER_NOTES.add(new NikonMakerNote(bundle));
            } catch (Exception ex) {
                index = maxIndex + 1;
            }
        }
    }

    private static NikonMakerNote get(ExifTags exifTags, byte[] rawValue) {

        for (NikonMakerNote makerNote : MAKER_NOTES) {

            if (makerNote.matches(exifTags, rawValue)) {
                return makerNote;
            }
        }
        return null;
    }

    @Override
    public void add(File file, ExifTags exifTags, ExifTag makerNoteTag) {
        add(makerNoteTag, exifTags);
    }

    private void add(
            ExifTag  exifMakerNote,
            ExifTags exifTags
            ) {

        assert exifMakerNote.id().equals(ExifTag.Id.MAKER_NOTE);

        NikonMakerNote nikonMakerNote =
                NikonMakerNotes.get(exifTags, exifMakerNote.rawValue());

        if (nikonMakerNote == null) return;

        List<ExifTag> allMakerNoteTags = new ArrayList<ExifTag>();
        int           offset           = nikonMakerNote.getByteOffsetToIfd();

        try {
            byte[] raw   = exifMakerNote.rawValue();
            byte[] bytes = new byte[raw.length - offset];

            System.arraycopy(raw, offset, bytes, 0, bytes.length);

            TiffReader r = new TiffReader(bytes);
            ImageFileDirectory ifd = r.getIFD(0);

            int count = ifd.getEntryCount();
            for (int i = 0; i < count; i++) {
                allMakerNoteTags.add(new ExifTag(ifd.getEntryAt(i), IfdType.MAKER_NOTE));
            }

            exifTags.addMakerNoteTags(nikonMakerNote.getDisplayableMakerNotesOf(allMakerNoteTags));
            exifTags.setMakerNoteDescription(nikonMakerNote.getDescription());
            mergeMakerNoteTags(exifTags, nikonMakerNote.getTagIdsEqualInExifIfd());

        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }
    }

    private static void mergeMakerNoteTags(ExifTags exifTags, List<Pair<Integer, Integer>> equalTagIds) {

        for (Pair<Integer, Integer> pair : equalTagIds) {
            ExifTag makerNoteTag = exifTags.makerNoteTagById(pair.getFirst());

            if (makerNoteTag != null) {

                ExifTag exifTag = exifTags.exifTagById(pair.getSecond());

                exifTags.removeMakerNoteTag(makerNoteTag);

                // prefering existing tag
                if (exifTag == null) {

                    exifTags.addExifTag(
                            new ExifTag(
                                pair.getSecond(),
                                makerNoteTag.dataTypeId(),
                                makerNoteTag.valueCount(),
                                makerNoteTag.valueOffset(),
                                makerNoteTag.rawValue(),
                                makerNoteTag.stringValue(),
                                makerNoteTag.byteOrderId(),
                                makerNoteTag.name(),
                                IfdType.EXIF
                            ));
                }
            }
        }
    }
}
