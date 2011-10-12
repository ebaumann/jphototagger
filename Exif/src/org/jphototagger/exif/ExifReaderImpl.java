package org.jphototagger.exif;

import java.io.File;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifReader;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifReader.class)
public final class ExifReaderImpl implements ExifReader {

    @Override
    public Exif readExif(File file) {
        if (!canReadExif(file)) {
            return null;
        }

        return ExifMetadata.getExif(file);
    }

    @Override
    public Exif readExifPreferCached(File file) {
        if (!canReadExif(file)) {
            return null;
        }

        return ExifMetadata.getExifPreferCached(file);
    }

    @Override
    public boolean canReadExif(File file) {
        return ExifSupport.INSTANCE.canReadExif(file);
    }
}
