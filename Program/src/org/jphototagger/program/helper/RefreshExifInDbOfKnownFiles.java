package org.jphototagger.program.helper;

import java.io.File;
import java.util.List;

import org.jphototagger.domain.exif.Exif;
import org.jphototagger.exif.ExifMetadata;
import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.database.DatabaseImageFiles;

/**
 * Refreshes the EXIF metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author Elmar Baumann
 */
public final class RefreshExifInDbOfKnownFiles extends HelperThread {
    private volatile boolean cancel;

    public RefreshExifInDbOfKnownFiles() {
        super("JPhotoTagger: Refreshing EXIF in the database of known files");
        setInfo(Bundle.getString(RefreshExifInDbOfKnownFiles.class, "RefreshExifInDbOfKnownFiles.Info"));
    }

    @Override
    public void run() {
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        List<File> imageFiles = db.getAllImageFiles();
        int fileCount = imageFiles.size();

        progressStarted(0, 0, fileCount, (fileCount > 0)
                                         ? imageFiles.get(0)
                                         : null);

        for (int i = 0; !cancel &&!isInterrupted() && (i < fileCount); i++) {
            File imageFile = imageFiles.get(i);
            Exif exif = null;

           if (!AppFileFilters.INSTANCE.isUserDefinedFileType(imageFile)) {
                ExifCache.INSTANCE.deleteCachedExifTags(imageFile);
                exif = ExifMetadata.getExif(imageFile);
            }

            if (exif != null) {
                db.insertOrUpdateExif(imageFile, exif);
            }

            progressPerformed(i + 1, imageFile);
        }

        progressEnded(null);
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
