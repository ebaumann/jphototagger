package org.jphototagger.exif.formatter.nikon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifMakerNotes;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.ExifTags;

import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;

/**
 * @author Elmar Baumann
 */
public final class NikonMakerNotes implements ExifMakerNotes {

    private static final String PROPERTY_FILE_PREFIX = "org/jphototagger/exif/formatter/nikon/NikonExifMakerNote_";
    private static final Collection<NikonMakerNote> MAKER_NOTES = new ArrayList<NikonMakerNote>();

    static {
        int index = 0;
        final int maxIndex = 100;

        while (index <= maxIndex) {
            try {

                // Better than catching an exception?
                // java.util.jar.JarFile jarFile = new java.util.jar.JarFile(".../JPhotoTagger.jar");
                // Enumeration entries = jarFile.entries();
                // while (entries.hasMoreElements()) {
                // java.util.zip.ZipEntry entry = (java.util.zip.ZipEntry) entries.nextElement();
                // if (entry.getName().startsWith(PROPERTY_FILE_PREFIX) ...
                // }
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
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        if (makerNoteTag == null) {
            throw new NullPointerException("makerNoteTag == null");
        }

        add(makerNoteTag, exifTags);
    }

    private void add(ExifTag exifMakerNote, ExifTags exifTags) {
        assert exifMakerNote.convertTagIdToEnumId().equals(ExifTag.Id.MAKER_NOTE);

        NikonMakerNote nikonMakerNote = NikonMakerNotes.get(exifTags, exifMakerNote.getRawValue());

        if (nikonMakerNote == null) {
            return;
        }

        List<ExifTag> allMakerNoteTags = new ArrayList<ExifTag>();
        int offset = nikonMakerNote.getByteOffsetToIfd();

        try {
            byte[] raw = exifMakerNote.getRawValue();
            byte[] bytes = new byte[raw.length - offset];

            System.arraycopy(raw, offset, bytes, 0, bytes.length);

            TiffReader r = new TiffReader(bytes);
            ImageFileDirectory ifd = r.getIFD(0);
            int count = ifd.getEntryCount();

            for (int i = 0; i < count; i++) {
                allMakerNoteTags.add(new ExifTag(ifd.getEntryAt(i), ExifIfdType.MAKER_NOTE));
            }

            exifTags.addMakerNoteTags(nikonMakerNote.getDisplayableMakerNotesOf(allMakerNoteTags));
            exifTags.setMakerNoteDescription(nikonMakerNote.getDescription());
            mergeMakerNoteTags(exifTags, nikonMakerNote.getTagIdsEqualInExifIfd());
        } catch (Exception ex) {
            Logger.getLogger(NikonMakerNotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void mergeMakerNoteTags(ExifTags exifTags, List<NikonMakerNoteTagIdExifTagId> equalTagIds) {
        for (NikonMakerNoteTagIdExifTagId nikonMakerNoteTagIdExifTagId : equalTagIds) {
            ExifTag makerNoteTag = exifTags.findmakerNoteTagByTagId(nikonMakerNoteTagIdExifTagId.getNikonMakerNoteTagId());

            if (makerNoteTag != null) {
                ExifTag exifTag = exifTags.findExifTagByTagId(nikonMakerNoteTagIdExifTagId.getExifTagId());

                exifTags.removeFromMakerNoteTags(makerNoteTag);

                // prefering existing tag
                if (exifTag == null) {
                    exifTags.addExifTag(new ExifTag(nikonMakerNoteTagIdExifTagId.getExifTagId(), makerNoteTag.getDataTypeId(),
                            makerNoteTag.getValueCount(), makerNoteTag.getValueOffset(),
                            makerNoteTag.getRawValue(), makerNoteTag.getStringValue(),
                            makerNoteTag.getByteOrderId(), makerNoteTag.getName(), ExifIfdType.EXIF));
                }
            }
        }
    }
}
