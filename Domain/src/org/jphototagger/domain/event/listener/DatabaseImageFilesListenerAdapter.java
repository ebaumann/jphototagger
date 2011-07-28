package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.xmp.Xmp;
import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public class DatabaseImageFilesListenerAdapter implements DatabaseImageFilesListener {

    @Override
    public void imageFileInserted(File imageFile) {
        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        // ignore
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        // ignore
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        // ignore
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        // ignore
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {
        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {
        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {
        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {
        // ignore
    }
}
