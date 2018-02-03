package org.jphototagger.exiftoolxtiw;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * Persists and restores the settings of this module.
 *
 * @author Elmar Baumann
 */
public final class Settings {

    private static final String KEY_EXIFTOOL_ENABLED = "ExifToolXmpToImageWriter.ExifToolEnabled";
    private static final String KEY_EXIFTOOL_FILEPATH = "ExifToolXmpToImageWriter.ExifToolFilePath";
    private static final String KEY_WRITE_IPTC = "ExifToolXmpToImageWriter.WriteIptc";
    private static final String KEY_WRITE_XMP = "ExifToolXmpToImageWriter.WriteXmp";
    private static final String KEY_CREATE_BACKUP_FILE = "ExifToolXmpToImageWriter.CreateBackupFile";
    private static final String KEY_WRITE_ON_EVERY_XMP_FILE_MODIFICATION = "ExifToolXmpToImageWriter.WriteOnEveryXmpFileModifcation";
    private static final String KEY_FILE_SUFFIXES_SET = "ExifToolXmpToImageWriter.FileSuffixesSet";
    private static final String KEY_FILE_SUFFIXES = "ExifToolXmpToImageWriter.FileSuffixes";
    private static final Collection<String> DEFAULT_FILE_SUFFIXES = Arrays.asList(".jpg", ".tif", ".tiff");
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

    /**
     * Sets whether ExifTool is enabled.
     *
     * @param enabled true, if ExifTool is enabled. Null is the same as false.
     */
    public void setExifToolEnabled(Boolean enabled) {
        if (enabled == null) {
            prefs.removeKey(KEY_EXIFTOOL_ENABLED);
        } else {
            prefs.setBoolean(KEY_EXIFTOOL_ENABLED, enabled);
        }
    }

    /**
     * @return true, if ExifTool is enabled
     */
    public boolean isExifToolEnabled() {
        return prefs.getBoolean(KEY_EXIFTOOL_ENABLED);
    }

    /**
     * Sets the path to the ExifTool executable file.
     *
     * @param path file path. If null or empty, the path will be removed.
     */
    public void setExifToolFilePath(String path) {
        if (!StringUtil.hasContent(path)) {
            prefs.removeKey(KEY_EXIFTOOL_FILEPATH);
        } else {
            prefs.setString(KEY_EXIFTOOL_FILEPATH, path.trim());
        }
    }

    /**
     * @return path to the ExifTool executable file
     */
    public String getExifToolFilePath() {
        return prefs.getString(KEY_EXIFTOOL_FILEPATH);
    }

    /**
     * Sets, whether ExifTool shall write IPTC into the image file.
     *
     * @param write true, if ExifTool shall write IPTC into the image file. Null
     *              is equals to false.
     */
    public void setWriteIptc(Boolean write) {
        if (write == null) {
            prefs.removeKey(KEY_WRITE_IPTC);
        } else {
            prefs.setBoolean(KEY_WRITE_IPTC, write);
        }
    }

    /**
     * @return true, if ExifTool shall write IPTC into the image file
     */
    public boolean isWriteIptc() {
        return prefs.getBoolean(KEY_WRITE_IPTC);
    }

    /**
     * Sets, whether ExifTool shall write XMP into the image file.
     *
     * @param write true, if ExifTool shall write XMP into the image file. Null
     *              is equals to false.
     */
    public void setWriteXmp(Boolean write) {
        if (write == null) {
            prefs.removeKey(KEY_WRITE_XMP);
        } else {
            prefs.setBoolean(KEY_WRITE_XMP, write);
        }
    }

    /**
     * @return true, if ExifTool shall write XMP into the image file
     */
    public boolean isWriteXmp() {
        return prefs.getBoolean(KEY_WRITE_XMP);
    }

    /**
     * Sets, whether ExifTool shall backup the image file.
     *
     * @param create true, if ExifTool shall backup the image file. Null is
     *               equals to false.
     */
    public void setCreateBackupFile(Boolean create) {
        if (create == null) {
            prefs.removeKey(KEY_CREATE_BACKUP_FILE);
        } else {
            prefs.setBoolean(KEY_CREATE_BACKUP_FILE, create);
        }
    }

    /**
     * @return true, if ExifTool shall backup the image file
     */
    public boolean isCreateBackupFile() {
        return prefs.getBoolean(KEY_CREATE_BACKUP_FILE);
    }

    /**
     * Sets, whether ExifTool shall write metadata into the image file on every
     * modification of the XMP file.
     *
     * @param write true, if ExifTool shall write metadata into the image file
     *              on every modification of the XMP file. Null is equals to
     *              false.
     */
    public void setWriteOnEveryXmpFileModification(Boolean write) {
        if (write == null) {
            prefs.removeKey(KEY_WRITE_ON_EVERY_XMP_FILE_MODIFICATION);
        } else {
            prefs.setBoolean(KEY_WRITE_ON_EVERY_XMP_FILE_MODIFICATION, write);
        }
    }

    /**
     * @return true, if ExifTool shall write metadata into the image file on
     *         every modification of the XMP file
     */
    public boolean isWriteOnEveryXmpFileModification() {
        return prefs.getBoolean(KEY_WRITE_ON_EVERY_XMP_FILE_MODIFICATION);
    }

    /**
     * Sets the suffixes of files into which ExifTool shall write.
     *
     * @param suffixes suffixes of files into which ExifTool shall write
     */
    public void setFileSuffixes(Collection<String> suffixes) {
        if (suffixes != null) {
            prefs.setStringCollection(KEY_FILE_SUFFIXES, suffixes);
            prefs.setBoolean(KEY_FILE_SUFFIXES_SET, true);
        }
    }

    /**
     * @return suffixes of files into which ExifTool shall write
     */
    public Collection<String> getFileSuffixes() {
        return prefs.containsKey(KEY_FILE_SUFFIXES_SET)
                ? prefs.getStringCollection(KEY_FILE_SUFFIXES)
                : DEFAULT_FILE_SUFFIXES;
    }

    /**
     * @return Default file suffixes
     */
    public Collection<String> getDefaultFileSuffixes() {
        return Collections.unmodifiableCollection(DEFAULT_FILE_SUFFIXES);
    }

    /**
     * Checks, whether ExifTool can write into image files. Examines the
     * existence of the ExifTool executable and if at least one metadata option
     * - XMP or IPTC - is enabled and a file suffix is present. <em>Does not
     * consider {@link #isExifToolEnabled()}.</em>
     *
     * @return true, if ExifTool can write into image files
     */
    public boolean canWrite() {
        String filePath = getExifToolFilePath();

        if (!StringUtil.hasContent(filePath)) {
            return false;
        }

        File exifToolFile = new File(filePath);

        if (!exifToolFile.isFile()) {
            return false;
        }

        if (getFileSuffixes().isEmpty()) {
            return false;
        }

        return isWriteIptc() || isWriteXmp();
    }
}
