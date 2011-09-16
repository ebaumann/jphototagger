package org.jphototagger.program.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Storage;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Refreshes the XMP metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author Elmar Baumann
 */
public final class RefreshXmpInDbOfKnownFiles extends HelperThread {

    private volatile boolean cancel;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public RefreshXmpInDbOfKnownFiles() {
        super("JPhotoTagger: Refreshing XMP in the database of known files");
        setInfo(Bundle.getString(RefreshXmpInDbOfKnownFiles.class, "RefreshXmpInDbOfKnownFiles.Info"));
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
            Xmp xmp = null;

            try {
                xmp = XmpMetadata.hasImageASidecarFile(imageFile)
                        ? XmpMetadata.getXmpFromSidecarFileOf(imageFile)
                        : isScanForEmbeddedXmp()
                        ? XmpMetadata.getEmbeddedXmp(imageFile)
                        : null;
            } catch (IOException ex) {
                Logger.getLogger(RefreshXmpInDbOfKnownFiles.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (xmp != null) {
                repo.saveOrUpdateXmpOfImageFile(imageFile, xmp);
            }

            progressPerformed(i + 1, imageFile);
        }

        progressEnded(null);
    }

    private boolean isScanForEmbeddedXmp() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? storage.getBoolean(Storage.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
