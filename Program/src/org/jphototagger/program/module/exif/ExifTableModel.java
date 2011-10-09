package org.jphototagger.program.module.exif;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.metadata.exif.ExifTag;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.exif.cache.ExifCacheClearedEvent;
import org.jphototagger.exif.cache.ExifCacheFileDeletedEvent;
import org.jphototagger.lib.model.TableModelExt;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann, Tobias Stening
 */
public final class ExifTableModel extends TableModelExt {

    private static final long serialVersionUID = -5656774233855745962L;
    private File file;

    public ExifTableModel() {
        setRowHeaders();
        listen();
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString(ExifTableModel.class, "ExifTableModel.HeaderColumn.1"));
        addColumn(Bundle.getString(ExifTableModel.class, "ExifTableModel.HeaderColumn.2"));
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.file = file;
        removeAllRows();
        addExifTags();
    }

    private void addExifTags() {
        Collection<ExifTag> exifTags = ExifUtil.getExifTagsPreferCached(file);

        for (ExifTag exifTag : exifTags) {
            boolean isGpsUrl = exifTag.getDisplayName().equals(ExifTag.NAME_GOOGLE_MAPS_URL);
            if (isGpsUrl) {
                super.addRow(new Object[]{exifTag, new JButton(new GpsButtonListener(exifTag.getDisplayValue()))});
            } else {
                super.addRow(new Object[]{exifTag, exifTag});
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private class GpsButtonListener extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final String url;

        GpsButtonListener(String url) {
            super(Bundle.getString(ExifTableModel.class, "ExifTableModel.Button.GoogleMaps"));
            this.url = url;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            browse();
        }

        private void browse() {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                Logger.getLogger(GpsButtonListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @EventSubscriber(eventClass = ExifCacheClearedEvent.class)
    public void exifCacheCleared(ExifCacheClearedEvent evt) {
        int deletedCacheFileCount = evt.getDeletedCacheFileCount();
        if (file != null && deletedCacheFileCount > 0) {
            setFile(file);
        }
    }

    @EventSubscriber(eventClass = ExifCacheFileDeletedEvent.class)
    public void exifCacheCleared(ExifCacheFileDeletedEvent evt) {
        File imageFile = evt.getImageFile();
        if (imageFile.equals(file)) {
            setFile(file);
        }
    }
}
