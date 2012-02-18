package org.jphototagger.program.misc;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.concurrent.HelperThread;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class RenameLongXmpSidecarFilenames extends HelperThread {

    private static final String INFO = Bundle.getString(RenameLongXmpSidecarFilenames.class, "RenameLongXmpSidecarFilenames.Info");
    private static final Logger LOGGER = Logger.getLogger(RenameLongXmpSidecarFilenames.class.getName());
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private volatile boolean cancel;
    private final boolean setLongNames;

    /**
     * @param setLongNames if false default names will be set
     */
    public RenameLongXmpSidecarFilenames(boolean setLongNames) {
        super("JPhotoTagger: Setting " + (setLongNames ? " long " : " default ") + " sidecar filenames");
        this.setLongNames = setLongNames;
    }

    @Override
    public void run() {
        LOGGER.log(java.util.logging.Level.INFO, "Setting {0} XMP sidecar filenames", setLongNames ? " long " : " default ");
        ImageFilesRepository fileRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);
        List<File> files = fileRepo.findAllImageFiles();
        int filecount = files.size();
        progressStarted(0, 0, filecount, INFO);
        for (int index = 0; !cancel && index < filecount; index++) {
            renameXmpSidecarFile(files.get(index));
            progressPerformed(index + 1, INFO);
        }
        progressEnded(INFO);
        LOGGER.log(java.util.logging.Level.INFO, "{0} XMP sidecar filenames has been set", setLongNames ? " long " : " default ");
    }

    private void renameXmpSidecarFile(File file) {
        File longSidecarFile = xmpSidecarFileResolver.suggestLongSidecarFile(file);
        File defaultSidecarFile = xmpSidecarFileResolver.suggestDefaultSidecarFile(file);
        File fromFile = setLongNames ? defaultSidecarFile : longSidecarFile;
        File toFile = setLongNames ? longSidecarFile : defaultSidecarFile;
        boolean isRename = fromFile.isFile() && !toFile.exists();
        if (isRename) {
            LOGGER.log(Level.INFO, "Renaming XMP sidecar file ''{0}'' to ''{1}", new Object[]{fromFile, toFile});
            boolean renamed = fromFile.renameTo(toFile);
            if (!renamed) {
                LOGGER.log(Level.WARNING, "Sidecar file ''{0}'' couldn''t be renamed to ''{1}''", new Object[]{fromFile, toFile});
            }
        }
    }

    @Override
    public void cancel() {
        cancel = true;
    }
}
