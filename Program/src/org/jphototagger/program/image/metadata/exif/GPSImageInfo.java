package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;

import java.io.File;

import java.text.MessageFormat;

/**
 * Contains an image file and it's GPS Metadata.
 *
 * @author Elmar Baumann
 */
public final class GPSImageInfo {
    private final File imageFile;
    private final ExifGpsMetadata gpsMetaData;

    public GPSImageInfo(File imageFile, ExifGpsMetadata gpsMetaData) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (gpsMetaData == null) {
            throw new NullPointerException("gpsMetaData == null");
        }

        this.imageFile = imageFile;
        this.gpsMetaData = gpsMetaData;
    }

    public ExifGpsMetadata getGPSMetaData() {
        return gpsMetaData;
    }

    public File getImageFile() {
        return imageFile;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}: {1}", imageFile, gpsMetaData);
    }
}
