package org.jphototagger.exif;

import java.io.File;
import java.util.Collection;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifReader;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifReader.class)
public final class DefaultExifReader implements ExifReader {

    @Override
    public Exif readExif(File file) {
        if (!canReadExif(file)) {
            return null;
        }
        return ExifSupport.INSTANCE.getExif(file);
    }

    @Override
    public Exif readExifPreferCached(File file) {
        if (!canReadExif(file)) {
            return null;
        }
        return ExifSupport.INSTANCE.getExifPreferCached(file);
    }

    @Override
    public boolean canReadExif(File file) {
        return ExifSupport.INSTANCE.canReadExif(file);
    }

    @Override
    public Collection<? extends String> getExludeFilenameSuffixes() {
        return ExifSupport.INSTANCE.getExludeFilenameSuffixes();
    }
}
