package org.jphototagger.program.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.types.FileEditor;
import org.jphototagger.xmp.XmpFileReader;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Extracts in images embedded XMP metadata into sidecar files.
 *
 * @author Elmar Baumann
 */
public final class ExtractEmbeddedXmp extends FileEditor {

    @Override
    public void edit(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (!FileLock.INSTANCE.lockLogWarning(file, this)) {
            return;
        }

        File sidecarFile = XmpMetadata.getSidecarFile(file);

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
                fos = new FileOutputStream(XmpMetadata.suggestSidecarFile(file));
                fos.getChannel().lock();
                fos.write(xmp.getBytes());
                fos.flush();
                updateDatabase(file);
            } catch (Exception ex) {
                Logger.getLogger(ExtractEmbeddedXmp.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ex) {
                        Logger.getLogger(ExtractEmbeddedXmp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void updateDatabase(File imageFile) {
        InsertImageFilesIntoDatabase insert = new InsertImageFilesIntoDatabase(Arrays.asList(imageFile), InsertIntoRepository.XMP);

        insert.run();    // run in this thread!
    }
}
