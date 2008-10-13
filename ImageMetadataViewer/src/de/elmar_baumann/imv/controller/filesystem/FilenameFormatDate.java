package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formatted date in the order YYYY-MM-dd.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class FilenameFormatDate extends FilenameFormat {

    private String delimiter;
    private String name;

    public FilenameFormatDate(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Sets the date from the EXIF date of a file, If this is not possible
     * the last modification time of the file system will be used.
     * 
     * @param file file
     */
    private void setFromExif(File file) {
        Exif exif = ExifMetadata.getExif(file.getAbsolutePath());
        if (exif != null) {
            java.sql.Date date = exif.getDateTimeOriginal();
            if (date == null) {
                setFromFilesystem(file);
            } else {
                formatDate(new Date(date.getTime()));
            }
        }
    }
    
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Sets the date from the last modification time of the file system.
     * 
     * @param file file
     */
    private void setFromFilesystem(File file) {
        formatDate(new Date(file.lastModified()));
    }

    private void formatDate(Date date) {
        name = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd").format(date); // NOI18N
    }

    @Override
    public String format() {
        setFromExif(getFile());
        return name;
    }
    
    @Override
    public String toString() {
        return Bundle.getString("DateFilenameFormat.String");
    }
}
