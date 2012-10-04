package org.jphototagger.exifmodule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.concurrent.HelperThread;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;

/**
 * Sets EXIF metadata to XMP whithout time stamp check, currently only the date created.
 *
 * @author Elmar Baumann
 */
public final class SetExifToXmp extends HelperThread {

    private static final ImageFilesRepository IMAGE_FILES_REPOSITORY = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private static final XmpSidecarFileResolver XMP_SIDECAR_FILE_RESOLVER = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
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
     * @param replaceExistingXmpData true, if existing XMP metadata shall be replaced with EXIF metadata. Default:
     * false.
     */
    public SetExifToXmp(boolean replaceExistingXmpData) {
        this.replaceExistingXmpData = replaceExistingXmpData;
        setInfo();
    }

    /**
     * Checks all known image files.
     *
     * @param imageFiles image files to process instead of processing all known image files
     * @param replaceExistingXmpData true, if existing XMP metadata shall be replaced with EXIF metadata. Default:
     * false.
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
        setInfo(Bundle.getString(SetExifToXmp.class, "SetExifToXmp.Info"));
    }

    @Override
    public void run() {
        List<File> imgFiles = (files == null)
                ? IMAGE_FILES_REPOSITORY.findAllImageFiles()
                : files;
        int fileCount = imgFiles.size();
        progressStarted(0, 0, fileCount, (fileCount > 0)
                ? imgFiles.get(0)
                : null);
        for (int i = 0; !cancel && !isInterrupted() && (i < fileCount); i++) {
            File imgFile = imgFiles.get(i);

            set(imgFile, replaceExistingXmpData);
            progressPerformed(i + 1, imgFile.getName());
        }
        progressEnded(null);
    }

    /**
     * Sets the EXIF metadata of an image file to the XMP sidecar file and updates the repository. <p> If a XMP sidecar
     * file does not exist, it will be created. If the image file does not have EXIF metadata or the EXIF metadata does
     * not have a settable vale, nothing will be done.
     *
     * @param file file
     * @param replaceExistingXmpData true, if existing XMP metadata shall be replaced with EXIF metadata. Default:
     * false.
     */
    public static void set(File file, boolean replaceExistingXmpData) {
        if (file == null) {
            throw new NullPointerException("imgFile == null");
        }
        if (isUserDefinedFileType(file)) {
            return;
        }
        Exif exif = ExifUtil.readExifPreferCached(file);
        Xmp xmp = null;
        try {
            xmp = XmpMetadata.getXmpFromSidecarFileOf(file);
        } catch (IOException ex) {
            Logger.getLogger(SetExifToXmp.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (xmp == null) {
            xmp = new Xmp();
        }
        if ((exif != null) && exifHasValues(exif)) {
            if (isSet(xmp, replaceExistingXmpData)) {
                setDateCreated(xmp, exif);
                File xmpFile = XMP_SIDECAR_FILE_RESOLVER.suggestXmpSidecarFile(file);
                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFile)) {
                    ImageFile imageFile = new ImageFile();
                    xmp.setValue(XmpLastModifiedMetaDataValue.INSTANCE, xmpFile.lastModified());
                    // Avoiding re-reading thumbnails
                    imageFile.setLastmodified(file.lastModified());
                    imageFile.setSizeInBytes(file.length());
                    imageFile.setFile(file);
                    imageFile.setXmp(xmp);
                    imageFile.addToSaveIntoRepository(SaveOrUpdate.XMP);
                    IMAGE_FILES_REPOSITORY.saveOrUpdateImageFile(imageFile);
                }
            }
        }
    }

    private static boolean isUserDefinedFileType(File file) {
        UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
        String suffix = FileUtil.getSuffix(file);
        return repo.existsUserDefinedFileTypeWithSuffix(suffix);
    }

    private static boolean isSet(Xmp xmp, boolean replaceExistingXmpData) {
        return replaceExistingXmpData || !xmp.contains(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
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
            xmp.setValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, exif.getXmpDateCreated());
        }
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
