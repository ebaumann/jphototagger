package org.jphototagger.program.image.metadata.exif.gps;

import org.jphototagger.program.image.metadata.exif.GPSImageInfo;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.UnsupportedCharsetException;

import java.util.Collection;

import javax.swing.filechooser.FileFilter;

/**
 * Exports GPS metadata into a specefic format.
 *
 * @author Elmar Baumann
 */
public interface GPSLocationExporter {

    /**
     * Exports GPS metadata.
     *
     * @param gpsImageInfo GPS metadata to export
     * @param os           output stream to export to
     * @throws             IOException on I/O errors
     * @throws             UnsupportedCharsetException if the system does not
     *                     support UTF-8 strings
     */
    void export(Collection<? extends GPSImageInfo> gpsImageInfo,
                OutputStream os)
            throws IOException, UnsupportedCharsetException;

    /**
     * Returns the filter for exported files.
     *
     * @return filter
     */
    FileFilter getFileFilter();

    /**
     * Returns the file name suffix.
     *
     * @return suffix, e.g. <code>".kml"</code>
     */
    String getFilenameExtension();

    /**
     * Returns the display name of the exporter.
     *
     * @return display name
     */
    String getDisplayName();
}
