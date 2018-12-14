package org.jphototagger.exifmodule;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import javax.swing.AbstractAction;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.exif.ExifTag;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.domain.metadata.exif.event.ExifCacheClearedEvent;
import org.jphototagger.domain.metadata.exif.event.ExifCacheFileDeletedEvent;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.swing.TableModelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann, Tobias Stening
 */
public final class ExifTableModel extends TableModelExt {

    private static final long serialVersionUID = 1L;
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
            boolean isGoogleMapsUrl = exifTag.getDisplayName().equals(ExifTag.NAME_GOOGLE_MAPS_URL);
            boolean isOpenStreetMapUrl = exifTag.getDisplayName().equals(ExifTag.NAME_OPEN_STREET_MAP_URL);
            if (isGoogleMapsUrl) {
                String name = Bundle.getString(ExifTableModel.class, "ExifTableModel.Button.GoogleMaps");
                GpsButtonListener action = new GpsButtonListener(name, exifTag.getDisplayValue());
                super.addRow(new Object[]{exifTag, UiFactory.button(action)});
            } else if (isOpenStreetMapUrl) {
                String name = Bundle.getString(ExifTableModel.class, "ExifTableModel.Button.OpenStreetMap");
                GpsButtonListener action = new GpsButtonListener(name, exifTag.getDisplayValue());
                super.addRow(new Object[]{exifTag, UiFactory.button(action)});
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

        GpsButtonListener(String name, String url) {
            super(name);
            this.url = url;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            browse();
        }

        private void browse() {
            DesktopUtil.browse(url, "JPhotoTagger");
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
