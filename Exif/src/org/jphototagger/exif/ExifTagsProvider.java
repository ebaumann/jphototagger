package org.jphototagger.exif;

import java.io.File;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public interface ExifTagsProvider {

    /**
     * @param fromFile
     * @param toExifTags
     * @return count of added tags
     * @throws Exception
     */
    int addToExifTags(File fromFile, ExifTags toExifTags) throws Exception;

    boolean canReadExifTags(File file);

    Set<String> getSupportedFilenameSuffixes();
}
