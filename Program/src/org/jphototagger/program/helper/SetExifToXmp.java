package org.jphototagger.program.helper;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.ImageFile;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpLastModified;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.program.app.AppFileFilters;

/**
 * Sets EXIF metadata to XMP whithout time stamp check, currently only the date
 * created.
 *
 * @author Elmar Baumann
 */
public final class SetExifToXmp extends HelperThread {
    private List<File> files;
    private final boolean replaceExistingXmpData;
    private volatile boolean cancel;

    /**
     * Checks all known image files and does not replace existing XMP data.
     */
    public SetExifToXmp() {
        replaceExistingXmpData = false;
        setInfo();
    }

    /**
     * Checks all known image files.
     *
     * @param replaceExistingXmpData true, if existing XMP metadata shall
     *                               be replaced with EXIF metadata.
     *                               Default: false.
     */
    public SetExifToXmp(boolean replaceExistingXmpData) {
        this.replaceExistingXmpData = replaceExistingXmpData;
        setInfo();
    }

    /**
     * Checks all known image files.
     *
     * @param imageFiles             image files to process instead of
     *                               processing all known image files
     * @param replaceExistingXmpData true, if existing XMP metadata shall
     *                               be replaced with EXIF metadata.
     *                               Default: false.
     */
    public SetExifToXmp(Collection<? extends File> imageFiles, boolean replaceExistingXmpData) {
        super("JPhotoTagger: Setting EXIF metadata to XMP metadata");

        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        this.replaceExistingXmpData = replaceExistingXmpData;
        files = new ArrayList<File>(imageFiles);
        setInfo();
    }

    private void setInfo() {
        setInfo(JptBundle.INSTANCE.getString("SetExifToXmp.Info"));
    }

    @Override
    public void run() {
        List<File> imgFiles = (files == null)
                              ? DatabaseImageFiles.INSTANCE.getAllImageFiles()
                              : files;
        int fileCount = imgFiles.size();

        progressStarted(0, 0, fileCount, (fileCount > 0)
                                         ? imgFiles.get(0)
                                         : null);

        for (int i = 0; !cancel &&!isInterrupted() && (i < fileCount); i++) {
            File imgFile = imgFiles.get(i);

            set(imgFile, replaceExistingXmpData);
            progressPerformed(i + 1, imgFile);
        }

        progressEnded(null);
    }

    /**
     * Sets the EXIF metadata of an image file to the XMP sidecar file and
     * updates the database.
     * <p>
     * If a XMP sidecar file does not exist, it will be created. If the image
     * file does not have EXIF metadata or the EXIF metadata does not have a
     * settable vale, nothing will be done.
     *
     * @param imgFile              image file
     * @param replaceExistingXmpData true, if existing XMP metadata shall
     *                               be replaced with EXIF metadata.
     *                               Default: false.
     */
    public static void set(File imgFile, boolean replaceExistingXmpData) {
        if (imgFile == null) {
            throw new NullPointerException("imgFile == null");
        }

        if (AppFileFilters.INSTANCE.isUserDefinedFileType(imgFile)) {
            return;
        }

        Exif exif = ExifMetadata.getCachedExif(imgFile);
        Xmp xmp = null;

        try {
            xmp = XmpMetadata.getXmpFromSidecarFileOf(imgFile);
        } catch (IOException ex) {
            Logger.getLogger(SetExifToXmp.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (xmp == null) {
            xmp = new Xmp();
        }

        if ((exif != null) && exifHasValues(exif)) {
            if (isSet(xmp, replaceExistingXmpData)) {
                setDateCreated(xmp, exif);

                File xmpFile = XmpMetadata.suggestSidecarFile(imgFile);

                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFile)) {
                    ImageFile imageFile = new ImageFile();

                    xmp.setValue(ColumnXmpLastModified.INSTANCE, xmpFile.lastModified());

                    // Avoiding re-reading thumbnails
                    imageFile.setLastmodified(imgFile.lastModified());
                    imageFile.setFile(imgFile);
                    imageFile.setXmp(xmp);
                    imageFile.addInsertIntoDb(InsertImageFilesIntoDatabase.Insert.XMP);
                    DatabaseImageFiles.INSTANCE.insertOrUpdate(imageFile);
                }
            }
        }
    }

    private static boolean isSet(Xmp xmp, boolean replaceExistingXmpData) {
        return replaceExistingXmpData ||!xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
    }

    public static boolean exifHasValues(Exif exif) {
        if (exif == null) {
            throw new NullPointerException("exif == null");
        }

        return exif.getDateTimeOriginal() != null;
    }

    public static void setDateCreated(Xmp xmp, Exif exif) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        if (exif == null) {
            throw new NullPointerException("exif == null");
        }

        if (exif.getDateTimeOriginal() != null) {
            xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, exif.getXmpDateCreated());
        }
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
