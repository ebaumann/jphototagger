package org.jphototagger.program.model;

import org.jphototagger.lib.model.TableModelExt;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.cache.ExifCache;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTagDisplayComparator;
import org.jphototagger.program.image.metadata.exif.ExifTags;
import org.jphototagger.program.image.metadata.exif.ExifTagsToDisplay;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsAltitude;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsLatitude;
import org.jphototagger.program.image.metadata.exif.tag.ExifGpsLongitude;
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
        exifTags = ExifCache.INSTANCE.getExifTags(file);

        if (exifTags == null) {
            return;
        }

        addExifTags(exifTags.getExifTags());
        addExifTags(exifTags.getInteroperabilityTags());
        addGpsTags();
        addExifTags(exifTags.getMakerNoteTags());
    }

    private void addExifTags(Collection<? extends ExifTag> tags) {
        List<ExifTag> displayableExifTags = ExifTagsToDisplay.getDisplayableExifTagsOf(tags);

        if (displayableExifTags != null) {
            Collections.sort(displayableExifTags, ExifTagDisplayComparator.INSTANCE);

            for (ExifTag displayableExifTag : displayableExifTags) {
                String tagValue = displayableExifTag.getStringValue();

                if (tagValue.length() > 0) {
                    addTableRow(displayableExifTag);
                }
            }
        }
    }

    private void addGpsTags() {
        exifGpsMetadata = ExifGpsUtil.createGpsMetadataFromExifTags(exifTags);
        ExifGpsLatitude latitude = exifGpsMetadata.getLatitude();
        ExifGpsLongitude longitude = exifGpsMetadata.getLongitude();
        ExifGpsAltitude altitude = exifGpsMetadata.getAltitude();

        if (latitude != null) {
            String tagId = Integer.toString(ExifTag.Id.GPS_LATITUDE.getTagId());
            String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[] { tagName, exifGpsMetadata.getLatitude().getLocalizedString() });
        }

        if (longitude != null) {
            String tagId = Integer.toString(ExifTag.Id.GPS_LONGITUDE.getTagId());
            String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[] { tagName, exifGpsMetadata.getLongitude().toLocalizedString() });
        }

        if (altitude != null) {
            String tagId = Integer.toString(ExifTag.Id.GPS_ALTITUDE.getTagId());
            String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[] { tagName, exifGpsMetadata.getAltitude().getLocalizedString() });
        }

        if (longitude != null && latitude != null) {
            JButton button = new JButton(JptBundle.INSTANCE.getString("TableModelExif.Button.GoogleMaps"));

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
                String url = ExifGpsUtil.getGoogleMapsUrl(exifGpsMetadata.getLongitude(), exifGpsMetadata.getLatitude());

                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    AppLogger.logSevere(TableModelExif.class, ex);
                }
            }
        }
    }
}
