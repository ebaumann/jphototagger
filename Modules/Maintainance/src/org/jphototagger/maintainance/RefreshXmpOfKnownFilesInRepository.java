package org.jphototagger.maintainance;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpModifier;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.metadata.xmp.XmpToImageWriters;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.concurrent.HelperThread;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;

/**
 * Refreshes the XMP metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author Elmar Baumann
 */
public final class RefreshXmpOfKnownFilesInRepository extends HelperThread {

    private volatile boolean cancel;
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final Collection<? extends XmpModifier> xmpModifiers = Lookup.getDefault().lookupAll(XmpModifier.class);
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public RefreshXmpOfKnownFilesInRepository() {
        super("JPhotoTagger: Refreshing XMP in the repository of known files");
        setInfo(Bundle.getString(RefreshXmpOfKnownFilesInRepository.class, "RefreshXmpOfKnownFilesInRepository.Info"));
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
                xmp = xmpSidecarFileResolver.hasXmpSidecarFile(imageFile)
                        ? XmpMetadata.getXmpFromSidecarFileOf(imageFile)
                        : isScanForEmbeddedXmp()
                        ? XmpMetadata.getEmbeddedXmp(imageFile)
                        : null;
            } catch (IOException ex) {
                Logger.getLogger(RefreshXmpOfKnownFilesInRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (xmp != null) {
                File suggestedXmpFile = xmpSidecarFileResolver.suggestXmpSidecarFile(imageFile);
                File xmpFile = xmpSidecarFileResolver.findSidecarFile(suggestedXmpFile);
                if (modifyXmp(xmpFile, xmp)) {
                    XmpToImageWriters.write(xmpFile, imageFile);
                }
                setLastModified(xmpFile, xmp);
                repo.saveOrUpdateXmpOfImageFile(imageFile, xmp);
            }
            progressPerformed(i + 1, imageFile.getName());
        }
        progressEnded(null);
    }

    private boolean modifyXmp(File xmpFile, Xmp xmp) {
        if (xmpFile == null || xmp == null) {
            return false;
        }
        boolean modified = false;
        for (XmpModifier xmpModifier : xmpModifiers) {
            if (xmpModifier.modifyXmp(xmpFile, xmp)) {
                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFile)) {
                    modified = true;
                }
            }
        }
        return modified;
    }

    private void setLastModified(File xmpFile, Xmp xmp) {
        if (xmpFile == null || xmp == null) {
            return;
        }
        xmp.setValue(XmpLastModifiedMetaDataValue.INSTANCE, xmpFile.lastModified());
    }

    private boolean isScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
