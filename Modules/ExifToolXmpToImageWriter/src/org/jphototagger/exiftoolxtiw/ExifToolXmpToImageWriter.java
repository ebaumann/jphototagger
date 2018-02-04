package org.jphototagger.exiftoolxtiw;

import java.io.File;
import java.util.Arrays;
import org.jphototagger.domain.metadata.xmp.XmpToImageWriter;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = XmpToImageWriter.class)
public final class ExifToolXmpToImageWriter implements XmpToImageWriter {

    private final Settings settings = new Settings();
    private final ExifTooolXmpToImageWriterModel model = new ExifTooolXmpToImageWriterModel();

    @Override
    public boolean write(File xmpFile, File imageFile) {
        if (!settings.isSelfResponsible() || !settings.isExifToolEnabled() || !settings.isWriteOnEveryXmpFileModification()) {
            return false;
        }
        model.setXmpFile(xmpFile);
        model.setImageFiles(Arrays.asList(imageFile));
        return model.execute() > 0;
    }
}
