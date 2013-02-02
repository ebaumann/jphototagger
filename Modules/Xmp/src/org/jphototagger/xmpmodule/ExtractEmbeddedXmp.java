package org.jphototagger.xmpmodule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.repository.SaveToOrUpdateFilesInRepository;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpFileReader;
import org.openide.util.Lookup;

/**
 * Extracts in images embedded XMP metadata into sidecar files.
 *
 * @author Elmar Baumann
 */
public final class ExtractEmbeddedXmp extends FileEditor {

    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    @Override
    public void edit(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (!FileLock.INSTANCE.lockLogWarning(file, this)) {
            return;
        }
        File sidecarFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(file);

        if ((sidecarFile != null) && !confirmRemove(sidecarFile)) {
            return;
        }
        writeSidecarFile(file);
        FileLock.INSTANCE.unlock(file, this);
    }

    private boolean confirmRemove(File file) {
        if (getConfirmOverwrite()) {
            String message = Bundle.getString(ExtractEmbeddedXmp.class, "ExtractEmbeddedXmp.Confirm.Overwrite", file);
            return MessageDisplayer.confirmYesNo(null, message);
        }

        return true;
    }

    private void create(File file) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("File couldn't be created: " + file);
            }
        }
    }

    private void writeSidecarFile(File file) {
        String xmp = XmpFileReader.readFile(file);
        FileOutputStream fos = null;
        if (xmp != null) {
            try {
                create(file);
                fos = new FileOutputStream(xmpSidecarFileResolver.suggestXmpSidecarFile(file));
                fos.getChannel().lock();
                fos.write(xmp.getBytes());
                fos.flush();
                updateRepository(file);
            } catch (Throwable t) {
                Logger.getLogger(ExtractEmbeddedXmp.class.getName()).log(Level.SEVERE, null, t);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Throwable t) {
                        Logger.getLogger(ExtractEmbeddedXmp.class.getName()).log(Level.SEVERE, null, t);
                    }
                }
            }
        }
    }

    private void updateRepository(File imageFile) {
        SaveToOrUpdateFilesInRepository updater = Lookup.getDefault().lookup(SaveToOrUpdateFilesInRepository.class)
                .createInstance(Arrays.asList(imageFile), SaveOrUpdate.XMP);
        updater.saveOrUpdateWaitForTermination();
    }
}
