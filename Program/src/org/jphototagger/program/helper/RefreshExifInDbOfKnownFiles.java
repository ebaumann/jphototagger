package org.jphototagger.program.helper;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.util.List;
import org.jphototagger.program.app.AppFileFilters;

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
        setInfo(JptBundle.INSTANCE.getString("RefreshExifInDbOfKnownFiles.Info"));
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
