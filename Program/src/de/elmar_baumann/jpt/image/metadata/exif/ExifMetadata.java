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
import com.imagero.reader.tiff.IFDEntry;
import com.imagero.reader.tiff.ImageFileDirectory;
import com.imagero.reader.tiff.TiffReader;
import com.imagero.uio.bio.ByteArrayRandomAccessIO;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.image.metadata.exif.tag.ExifMakerNote;
import de.elmar_baumann.jpt.image.metadata.exif.tag.ExifMakerNoteFactory;
import de.elmar_baumann.jpt.types.FileType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extracts EXIF metadata from images as {@link ExifTag} and
 * {@link Exif} objects.
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
    public static List<ExifTag> getExifTags(File imageFile) {

        if (imageFile == null || !imageFile.exists()) return null;

        List<ExifTag> exifTags = new ArrayList<ExifTag>();
        try {
            addExifTags(imageFile, exifTags);
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
        return exifTags;
    }

    private static void addExifTags(File imageFile, List<ExifTag> exifTags) throws IOException {

        ImageReader imageReader = null;

        if (FileType.isJpegFile(imageFile.getName())) {

            AppLog.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.JPEG.Info", imageFile);

            imageReader = new JpegReader(imageFile);
            addAllExifTags((JpegReader) imageReader, exifTags);

        } else {

            AppLog.logInfo(ExifMetadata.class, "ExifMetadata.AddIFDEntries.TIFF.Info", imageFile);

            imageReader = new TiffReader(imageFile);

            int count = ((TiffReader) imageReader).getIFDCount();

            for (int i = 0; i < count; i++) {
                addAllExifTags(((TiffReader) imageReader).getIFD(i), exifTags, false);
            }
        }
        addMakerNotesTo(exifTags);
        close(imageReader);
    }

    private static void addAllExifTags(JpegReader jpegReader, List<ExifTag> exifTags) {

        IFDEntry[][] allIfdEntries = MetadataUtils.getExif(jpegReader);

        if (allIfdEntries != null) {

            for (int i = 0; i < allIfdEntries.length; i++) {

                IFDEntry[] currentIfdEntry = allIfdEntries[i];

                for (int j = 0; j < currentIfdEntry.length; j++) {

                    exifTags.add(new ExifTag(currentIfdEntry[j]));
                }
            }
        }
    }

    private static void addMakerNotesTo(List<ExifTag> exifTags) {
        for (ExifTag exifTag : exifTags) {

            if (exifTag.idValue() == ExifTag.Id.MAKER_NOTE.value()) {

                ExifMakerNote makerNote = ExifMakerNoteFactory.INSTANCE.get(exifTag.rawValue());

                if (makerNote != null) {
                    makerNote.addMakerNotes(exifTags);
                }
                return;
            }
        }
    }

    private static void close(ImageReader imageReader) {
        if (imageReader != null) {
            imageReader.close();
        }
    }

    private static void addAllExifTags(
            ImageFileDirectory ifd,
            List<ExifTag>      entries,
            boolean            add
            ) {

        if (add) {
            addExifTags(ifd, entries);
        }

        for (int i = 0; i < ifd.getIFDCount(); i++) {
            ImageFileDirectory subIfd = ifd.getIFDAt(i);
            addAllExifTags(subIfd, entries, false); // recursive
        }

        ImageFileDirectory exifIFD = ifd.getExifIFD();
        if (exifIFD != null) {
            addAllExifTags(exifIFD, entries, true); // recursive
        }

        ImageFileDirectory gpsIFD = ifd.getGpsIFD();
        if (gpsIFD != null) {
            addAllExifTags(gpsIFD, entries, true); // recursive
        }
    }

    private static void addExifTags(ImageFileDirectory ifd, List<ExifTag> exifTags) {

        int entryCount = ifd.getEntryCount();

        for (int i = 0; i < entryCount; i++) {

            IFDEntry ifdEntry = ifd.getEntryAt(i);

            if (ifdEntry != null) {

                exifTags.add(new ExifTag(ifdEntry));
            }
        }
    }

    /**
     * Finds in a list an EXIF entry of a specific EXIF tag value.
     * 
     * @param exifTags  EXIF Entries to look in
     * @param tagValue  tag value as defined in the EXIF standard
     * @return          first matching Entry or null if not found
     */
    public static ExifTag getExifTagIn(Collection<ExifTag> exifTags, int tagValue) {

        for (ExifTag exifTag : exifTags) {
            if (exifTag.idValue() == tagValue) {
                return exifTag;
            }
        }
        return null;
    }

    /**
     * Returns the EXIF maker note entries from a list of entries.
     * <p>
     * Searches the list of entries for the EXIF maker note tag 37500 and
     * returns all properitary entries.
     *
     * @param   exifTags     entries to search
     * @param   byteOffsetToIfd offset to the TIFF image file directory within the
     *                      raw value of the entry with the tag 37500
     * @return              Entries or null
     */
    public static List<ExifTag> getExifMakerNoteTagsIn(
            Collection<ExifTag> exifTags,
            int                 byteOffsetToIfd
            ) {

        List<ExifTag> makerNoteTags = new ArrayList<ExifTag>();
        ExifTag       exifTag        = getExifTagIn(exifTags, ExifTag.Id.MAKER_NOTE.value());

        if (exifTag != null)  {
            byte[] raw   = exifTag.rawValue();
            byte[] bytes = new byte[raw.length - byteOffsetToIfd];

            System.arraycopy(raw, byteOffsetToIfd, bytes, 0, bytes.length);

            try {
                ImageFileDirectory ifd = new ImageFileDirectory(
                        new ByteArrayRandomAccessIO(bytes), exifTag.byteOrderId());

                assert ifd.getIFDCount() == 0 : "References " + ifd.getIFDCount() + " other IFDs!";
                addExifTags(ifd, makerNoteTags);

                return makerNoteTags;
            } catch (Exception ex) {
                AppLog.logSevere(ExifMetadata.class, ex);
            }
        }
        return null;
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
