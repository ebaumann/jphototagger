package org.jphototagger.program.module.exif.comparators;

import java.io.File;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.repository.ImageFilesRepository;

/**
 * @author Elmar Baumann
 */
public final class ExifCompareUtil {

    private static final ImageFilesRepository IMAGE_FILES_REPOSITORY = Lookup.getDefault().lookup(ImageFilesRepository.class);

    static long getTimestampDateTimeOriginalFromRepository(File imageFile) {
        Exif exif = IMAGE_FILES_REPOSITORY.findExifOfImageFile(imageFile);

        if (exif == null || exif.getDateTimeOriginal() == null) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }

    private ExifCompareUtil() {
    }
}
