package org.jphototagger.maintainance;

import java.io.File;
import java.util.List;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.concurrent.HelperThread;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * Refreshes the EXIF metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author Elmar Baumann
 */
public final class RefreshExifOfKnownFilesInRepository extends HelperThread {

    private volatile boolean cancel;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public RefreshExifOfKnownFilesInRepository() {
        super("JPhotoTagger: Refreshing EXIF in the repository of known files");
        setInfo(Bundle.getString(RefreshExifOfKnownFilesInRepository.class, "RefreshExifOfKnownFilesInRepository.Info"));
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
            Exif exif = ExifUtil.readExif(imageFile);

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

    public boolean isCanceled() {
        return cancel;
    }
}
