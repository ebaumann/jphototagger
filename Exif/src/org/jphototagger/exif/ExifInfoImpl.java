package org.jphototagger.exif;

import java.io.File;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.image.exif.ExifInfo;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifInfo.class)
public final class ExifInfoImpl implements ExifInfo {

    @Override
    public double getRotationAngleOfEmbeddedThumbnail(File file) {
        ExifTags exifTags = ExifMetadata.getExifTagsPreferCached(file);

        if (exifTags != null) {
            ExifTag exifTag = exifTags.findExifTagByTagId(274);

            if (exifTag != null) {
                return ExifThumbnailUtil.getThumbnailRotationAngle(exifTag);
            }
        }

        return 0.0;
    }
}
