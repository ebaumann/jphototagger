package org.jphototagger.program.model;

import org.jphototagger.lib.model.TableModelExt;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.cache.ExifCache;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTagDisplayComparator;
import org.jphototagger.program.image.metadata.exif.ExifTags;
import org.jphototagger.program.image.metadata.exif.ExifTagsToDisplay;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsMetadata;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsUtil;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.resource.Translation;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;

/**
 * Elements are {@link ExifTag}s ore {@link String}s in case of GPS information
 * retrieved through {@link ExifMetadata#getExifTags(java.io.File)}.
 *
 * This model displays EXIF information of <em>one</em> image file.
 *
 * The first row is a "prompt", the second contains the data. Both are
 * containing the same objects (both either <code>ExifTag</code> instances or
 * <code>String</code> instances).
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class TableModelExif extends TableModelExt {
    private static final long serialVersionUID = -5656774233855745962L;
    private File file;
    private transient ExifGpsMetadata exifGpsMetadata;
    private transient ExifTags exifTags;
    private static final Translation TRANSLATION = new Translation("ExifTagIdTagNameTranslations");

    public TableModelExif() {
        setRowHeaders();
    }

    private void setRowHeaders() {
        addColumn(JptBundle.INSTANCE.getString("TableModelExif.HeaderColumn.1"));
        addColumn(JptBundle.INSTANCE.getString("TableModelExif.HeaderColumn.2"));
    }

    /**
     * Returns the file.
     *
     * @return file or null if not set
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file with exif metadata to be displayed.
     *
     * @param file file
     */
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.file = file;
        removeAllRows();

        try {
            setExifTags();
        } catch (Exception ex) {
            AppLogger.logSevere(TableModelExif.class, ex);
        }
    }

    private void setExifTags() {
        exifTags = ExifCache.getExifTags(file);

        if (exifTags == null) {
            return;
        }

        addExifTags(exifTags.getExifTags());
        addExifTags(exifTags.getInteroperabilityTags());
        addGpsTags();
        addExifTags(exifTags.getMakerNoteTags());
    }

    private void addExifTags(Collection<ExifTag> tags) {
        List<ExifTag> exifTagsToDisplay = ExifTagsToDisplay.get(tags);

        if (exifTagsToDisplay != null) {
            Collections.sort(exifTagsToDisplay, ExifTagDisplayComparator.INSTANCE);

            for (ExifTag exifTagToDisplay : exifTagsToDisplay) {
                String value = exifTagToDisplay.stringValue();

                if (value.length() > 0) {
                    addTableRow(exifTagToDisplay);
                }
            }
        }
    }

    private void addGpsTags() {
        exifGpsMetadata = ExifGpsUtil.gpsMetadata(exifTags);

        if (exifGpsMetadata.latitude() != null) {
            final String tagId = Integer.toString(ExifTag.Id.GPS_LATITUDE.value());
            final String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[] { tagName, exifGpsMetadata.latitude().localizedString() });
        }

        if (exifGpsMetadata.longitude() != null) {
            final String tagId = Integer.toString(ExifTag.Id.GPS_LONGITUDE.value());
            final String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[] { tagName, exifGpsMetadata.longitude().localizedString() });
        }

        if (exifGpsMetadata.altitude() != null) {
            final String tagId = Integer.toString(ExifTag.Id.GPS_ALTITUDE.value());
            final String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[] { tagName, exifGpsMetadata.altitude().localizedString() });
        }

        if ((exifGpsMetadata.longitude() != null) && (exifGpsMetadata.latitude() != null)) {
            final JButton button = new JButton(JptBundle.INSTANCE.getString("TableModelExif.Button.GoogleMaps"));

            button.addActionListener(new GpsButtonListener());
            super.addRow(new Object[] { exifGpsMetadata, button });
        }
    }

    private void addTableRow(ExifTag exifTag) {
        List<ExifTag> row = new ArrayList<ExifTag>();

        row.add(exifTag);
        row.add(exifTag);
        super.addRow(row.toArray(new ExifTag[row.size()]));
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private class GpsButtonListener implements ActionListener {
        GpsButtonListener() {}

        @Override
        public void actionPerformed(ActionEvent evt) {
            browse();
        }

        private void browse() {
            if (exifGpsMetadata != null) {
                String url = ExifGpsUtil.googleMapsUrl(exifGpsMetadata.longitude(), exifGpsMetadata.latitude());

                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    AppLogger.logSevere(TableModelExif.class, ex);
                }
            }
        }
    }
}
