package org.jphototagger.program.module.maintainance;

import java.io.File;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.exif.ExifMetadata;
import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.helper.HelperThread;

/**
 * Refreshes the EXIF metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author Elmar Baumann
 */
public final class RefreshExifInDbOfKnownFiles extends HelperThread {

    private volatile boolean cancel;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public RefreshExifInDbOfKnownFiles() {
        super("JPhotoTagger: Refreshing EXIF in the repository of known files");
        setInfo(Bundle.getString(RefreshExifInDbOfKnownFiles.class, "RefreshExifInDbOfKnownFiles.Info"));
    }

    @Override
    public void run() {
        List<File> imageFiles = repo.findAllImageFiles();
        int fileCount = imageFiles.size();

        progressStarted(0, 0, fileCount, (fileCount > 0)
                ? imageFiles.get(0)
                : null);

        for (int i = 0; !cancel && !isInterrupted() && (i < fileCount); i++) {
            File imageFile = imageFiles.get(i);
            Exif exif = null;

            if (!AppFileFilters.INSTANCE.isUserDefinedFileType(imageFile)) {
                ExifCache.INSTANCE.deleteCachedExifTags(imageFile);
                exif = ExifMetadata.getExif(imageFile);
            }

            if (exif != null) {
                repo.saveOrUpdateExif(imageFile, exif);
            }

            progressPerformed(i + 1, imageFile.getName());
        }

        progressEnded(null);
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
