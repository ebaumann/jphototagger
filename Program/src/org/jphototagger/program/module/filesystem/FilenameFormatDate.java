package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Formatted date in the order YYYY-MM-dd.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatDate extends FilenameFormat {

    private String delimiter;
    private String name;

    public FilenameFormatDate() {
        this("-");
    }

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
     * Sets the date from the EXIF date of a file, If this is not possible the last modification time of the file system
     * will be used.
     *
     * @param file file
     */
    private void formatDate(File file) {
        Exif exif = ExifUtil.readExifPreferCached(file);
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
        name = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd").format(date);
    }

    @Override
    public String toString() {
        return Bundle.getString(FilenameFormatDate.class, "FilenameFormatDate.String");
    }
}
