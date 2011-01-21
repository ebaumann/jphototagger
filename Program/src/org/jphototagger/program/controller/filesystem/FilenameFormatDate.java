package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Formatted date in the order YYYY-MM-dd.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatDate extends FilenameFormat {
    private String delimiter;
    private String name;

    /**
     * Creates an instance with a delimiter between year, month and day.
     *
     * @param delimiter delimiter
     */
    public FilenameFormatDate(String delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        this.delimiter = delimiter;
    }

    /**
     * Sets the delimiter between year, month and day.
     *
     * @param delimiter delimiter
     */
    public void setDelimiter(String delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        this.delimiter = delimiter;
    }

    @Override
    public String format() {
        formatDate(getFile());

        return name;
    }

    /**
     * Sets the date from the EXIF date of a file, If this is not possible
     * the last modification time of the file system will be used.
     *
     * @param file file
     */
    private void formatDate(File file) {
        Exif exif = ExifMetadata.getExif(file);

        if (exif == null) {
            setFromFilesystem(file);
        } else {
            java.sql.Date date = exif.getDateTimeOriginal();

            if (date == null) {
                setFromFilesystem(file);
            } else {
                formatDate(new Date(date.getTime()));
            }
        }
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
        name = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter
                                    + "dd").format(date);
    }

    @Override
    public String toString() {
        return JptBundle.INSTANCE.getString("FilenameFormatDate.String");
    }

    private FilenameFormatDate() {}
}
