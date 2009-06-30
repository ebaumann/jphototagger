package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.image.metadata.xmp.XmpFileReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import javax.swing.JOptionPane;

/**
 * Extracts in images embedded XMP metadata into sidecar files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/05/22
 */
public final class ExtractEmbeddedXmpEditor extends FileEditor {

    @Override
    public void edit(File file) {
        File sidecarFile = XmpMetadata.getSidecarFileOfImageFileIfExists(file);
        if (sidecarFile != null && !confirmRemove(sidecarFile.getAbsolutePath())) return;
        writeSidecarFile(file);
    }

    private boolean confirmRemove(String absolutePath) {
        if (getConfirmOverwrite()) {
            return JOptionPane.showConfirmDialog(null,
                Bundle.getString("ExtractEmbeddedXmpEditor.ConfirmMessage.Overwrite", absolutePath),
                Bundle.getString("ExtractEmbeddedXmpEditor.ConfirmMessage.Overwrite.Title"),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void create(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private void writeSidecarFile(File file) {
        String xmp = XmpFileReader.readFile(file.getAbsolutePath());
        if (xmp != null) {
            try {
                create(file);
                FileOutputStream fos = new FileOutputStream(new File(XmpMetadata.suggestSidecarFilenameForImageFile(file.getAbsolutePath())));
                fos.write(xmp.getBytes());
                fos.flush();
                fos.close();
                updateDatabase(file.getAbsolutePath());
            } catch (Exception ex) {
                AppLog.logWarning(ExtractEmbeddedXmpEditor.class, ex);
            }
        }
    }

    private void updateDatabase(String imageFilename) {
        InsertImageFilesIntoDatabase insert = new InsertImageFilesIntoDatabase(
            Arrays.asList(imageFilename),
            EnumSet.of(InsertImageFilesIntoDatabase.Insert.XMP));
        insert.run(); // Shall run in this thread!
    }
}
