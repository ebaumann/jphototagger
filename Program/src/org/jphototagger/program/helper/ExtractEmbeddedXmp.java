package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.io.RuntimeUtil;
import org.jphototagger.program.types.FileEditor;
import org.jphototagger.lib.image.metadata.xmp.XmpFileReader;
import org.jphototagger.lib.io.FileLock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;

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

        if (!RuntimeUtil.lockLogWarning(file, this)) {
            return;
        }

        File sidecarFile = XmpMetadata.getSidecarFile(file);

        if ((sidecarFile != null) &&!confirmRemove(sidecarFile)) {
            return;
        }

        writeSidecarFile(file);
        FileLock.INSTANCE.unlock(file, this);
    }

    private boolean confirmRemove(File file) {
        if (getConfirmOverwrite()) {
            return MessageDisplayer.confirmYesNo(null,
                    "ExtractEmbeddedXmp.Confirm.Overwrite", file);
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
        String           xmp = XmpFileReader.readFile(file);
        FileOutputStream fos = null;

        if (xmp != null) {
            try {
                create(file);
                fos = new FileOutputStream(
                    XmpMetadata.suggestSidecarFile(file));
                fos.getChannel().lock();
                fos.write(xmp.getBytes());
                fos.flush();
                updateDatabase(file);
            } catch (Exception ex) {
                AppLogger.logSevere(ExtractEmbeddedXmp.class, ex);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ex) {
                        AppLogger.logSevere(ExtractEmbeddedXmp.class, ex);
                    }
                }
            }
        }
    }

    private void updateDatabase(File imageFile) {
        InsertImageFilesIntoDatabase insert =
            new InsertImageFilesIntoDatabase(Arrays.asList(imageFile),
                Insert.XMP);

        insert.run();    // run in this thread!
    }
}
