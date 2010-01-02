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
package de.elmar_baumann.jpt.image.metadata.exif;

import com.imagero.reader.ImageReader;
import com.imagero.reader.MetadataUtils;
import com.imagero.reader.jpeg.JpegReader;
import com.imagero.reader.tiff.EXIF;
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.types.FileType;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts EXIF metadata from images as {@link ExifTag} and
 * {@link EXIF} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifMetadata {

    /**
     * Returns {@link ExifTag} instances of an image file.
     * 
     * @param  imageFile image file
     * @return           EXIF entries or null if errors occured
     */
    public static ExifTags getExifTags(File imageFile) {

        if (imageFile == null || !imageFile.exists()) return null;

        ExifTags exifTags = new ExifTags();

        try {

            addExifTags(imageFile, exifTags);

        } catch (Exception ex) {

            AppLog.logSevere(ExifMetadata.class, ex);
        }

        addExifMakerNoteTags(exifTags);

        return exifTags;
    }

    private static void addExifTags(File imageFile, ExifTags exifTags) throws IOException {

        ImageReader imageReader = null;

        if (FileType.isJpegFile(imageFile.getName())) {

            AppLog.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.JPEG.Info", imageFile);

            imageReader = new JpegReader(imageFile);
            addAllExifTags((JpegReader) imageReader, exifTags);

        } else {

            AppLog.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.TIFF.Info", imageFile);

            imageReader = new TiffReader(imageFile);

            int count = ((TiffReader) imageReader).getIFDCount();

            // FIXME: IfdType.EXIF: How to determine the IFD type of an IFD?
            for (int i = 0; i < count; i++) {
                addTagsOfIfd(((TiffReader) imageReader).getIFD(i), IfdType.EXIF, exifTags);
            }
        }
        close(imageReader);
    }

    private static void addAllExifTags(JpegReader jpegReader, ExifTags exifTags) {

        IFDEntry[][] allIfdEntries = MetadataUtils.getExif(jpegReader);

        if (allIfdEntries != null) {

            for (int i = 0; i < allIfdEntries.length; i++) {

                IFDEntry[] currentIfdEntry = allIfdEntries[i];

                for (int j = 0; j < currentIfdEntry.length; j++) {

                    IFDEntry entry = currentIfdEntry[j];

                    exifTags.addExifTag(new ExifTag(entry, IfdType.EXIF));
                }
            }
        }
    }

    private static void close(ImageReader imageReader) {
        if (imageReader != null) {
            imageReader.close();
        }
    }

    public enum IfdType {
        EXIF            ("EXIF IFD"),
        GPS             ("GPS IFD"),
        INTEROPERABILITY("Interoperability IFD"),
        MAKER_NOTE      ("Maker note"),
        UNDEFINED       ("Undefined"),
        ;

        private final String name;

        private IfdType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static void addTagsOfIfd(
            ImageFileDirectory ifd,
            IfdType            ifdType,
            ExifTags           exifTags
            ) {

        if (!ifdType.equals(IfdType.UNDEFINED)) {
            addExifTags(ifd, ifdType, exifTags);
        }

        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);
            addTagsOfIfd(subIfd, IfdType.UNDEFINED, exifTags); // recursive
        }

        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addTagsOfIfd(exifIFD, IfdType.EXIF, exifTags); // recursive
        }

        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addTagsOfIfd(gpsIFD, IfdType.GPS, exifTags); // recursive
        }

        ImageFileDirectory interoperabilityIFD = ifd.getInteroperabilityIFD();
        if (interoperabilityIFD != null) {
            addTagsOfIfd(interoperabilityIFD, IfdType.INTEROPERABILITY, exifTags); // recursive
        }
    }

    private static void addExifTags(ImageFileDirectory ifd, IfdType ifdType, ExifTags exifTags) {

        int entryCount = ifd.getEntryCount();

        for (int i = 0; i < entryCount; i++) {

            IFDEntry ifdEntry = ifd.getEntryAt(i);

            if (ifdEntry != null) {

                ExifTag exifTag = new ExifTag(ifdEntry, ifdType);
                switch (ifdType) {
                    case EXIF            : exifTags.addExifTag(exifTag)            ; break;
                    case GPS             : exifTags.addGpsTag(exifTag)             ; break;
                    case INTEROPERABILITY: exifTags.addInteroperabilityTag(exifTag); break;
                    case MAKER_NOTE      : exifTags.addMakerNoteTag(exifTag)       ; break;
                    default              : assert(false);
                }
            }
        }
    }

    private static void addExifMakerNoteTags(ExifTags exifTags) {

        ExifTag makerNoteTag = exifTags.exifTagById(ExifTag.Id.MAKER_NOTE.value());

        if (makerNoteTag != null) {
            addExifMakerNoteTags(makerNoteTag, exifTags);
        }
    }

    private static void addExifMakerNoteTags(
            ExifTag  exifMakerNote,
            ExifTags exifTags
            ) {

        assert exifMakerNote.id().equals(ExifTag.Id.MAKER_NOTE);

        DisplayableExifMakerNote makerNote =
                DisplayableExifMakerNoteFactory.INSTANCE.get(exifTags, exifMakerNote.rawValue());

        if (makerNote == null) return;

        List<ExifTag> allMakerNoteTags = new ArrayList<ExifTag>();
        int           offset           = makerNote.getByteOffsetToIfd();

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

            exifTags.addMakerNoteTags(
                    makerNote.getDisplayableMakerNotesOf(allMakerNoteTags));
            exifTags.setMakerNoteDescription(makerNote.getDescription());
            mergeMakerNoteTags(exifTags, makerNote.getTagIdsEqualInExifIfd());

        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
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

    /**
     * Returns EXIF metadata of a image file.
     * 
     * @param  imageFile image file
     * @return           EXIF metadata or null if errors occured
     */
    public static Exif getExif(File imageFile) {
        return ExifFactory.getExif(imageFile);
    }

    /**
     * Returns the milliseconds since 1970 of the time when the image was taken.
     * <p>
     * Reads the EXIF information of the file.
     *
     * @param  imageFile image file
     * @return milliseconds. If the image file has no EXIF metadata or no
     *         date time original information whithin the EXIF metadata the last
     *         modification time of the file will be returned
     */
    public static long timestampDateTimeOriginal(File imageFile) {

        Exif exif = getExif(imageFile);

        if (exif == null || exif.getDateTimeOriginal() == null) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }

    /**
     * Returns the milliseconds since 1970 of the time when the image was taken.
     * <p>
     * Gets the EXIF information from the database. If in the database is no
     * EXIF information, the file's timestamp will be used, regardless whether
     * the file contains EXIF information.
     *
     * @param  imageFile image file
     * @return milliseconds. If the image file has no EXIF metadata or no
     *         date time original information whithin the EXIF metadata the last
     *         modification time of the file will be returned
     */
    public static long timestampDateTimeOriginalDb(File imageFile) {

        Exif exif = DatabaseImageFiles.INSTANCE.getExifOfFile(imageFile.getAbsolutePath());

        if (exif == null || exif.getDateTimeOriginal() == null) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }

    private ExifMetadata() {
    }
}
