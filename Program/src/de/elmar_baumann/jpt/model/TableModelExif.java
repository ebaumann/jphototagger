/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.image.metadata.exif.tag.ExifGpsMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.tag.ExifGpsUtil;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTagDisplayComparator;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTagsToDisplay;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.Translation;
import de.elmar_baumann.jpt.view.dialogs.SettingsDialog;
import de.elmar_baumann.jpt.view.panels.SettingsMiscPanel;
import de.elmar_baumann.lib.runtime.External;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

/**
 * EXIF-Daten eines Bilds.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class TableModelExif extends DefaultTableModel {

    private              File            file;
    private              ExifGpsMetadata exifGpsMetadata;
    private              List<ExifTag>   allExifTags;
    private static final Translation     TRANSLATION = new Translation("ExifTagIdTagNameTranslations");

    public TableModelExif() {
        setRowHeaders();
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.1"));
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.2"));
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
        this.file = file;
        removeAllElements();
        try {
            setExifTags();
        } catch (Exception ex) {
            AppLog.logSevere(TableModelExif.class, ex);
        }
    }

    /**
     * Entfernt alle EXIF-Daten.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();

    }

    private void setExifTags() {

        allExifTags = ExifMetadata.getExifTags(file);

        if (allExifTags != null) {

            List<ExifTag> exifTagsToDisplay = ExifTagsToDisplay.get(allExifTags);

            if (exifTagsToDisplay != null) {

                Collections.sort(exifTagsToDisplay, ExifTagDisplayComparator.INSTANCE);

                for (ExifTag exifTagToDisplay : exifTagsToDisplay) {

                    String value = exifTagToDisplay.stringValue();

                    if (value.length() > 0) {

                        addTableRow(exifTagToDisplay);
                    }
                }
            }
            addGpsTags();
        }
    }

    private void addGpsTags() {
        exifGpsMetadata = ExifGpsUtil.gpsMetadata(allExifTags);
        if (exifGpsMetadata.latitude() != null) {

            final String tagId   = Integer.toString(ExifTag.Id.GPS_LATITUDE.value());
            final String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[]{tagName, exifGpsMetadata.latitude().localizedString()});
        }
        if (exifGpsMetadata.longitude() != null) {

            final String tagId   = Integer.toString(ExifTag.Id.GPS_LONGITUDE.value());
            final String tagName = TRANSLATION.translate(tagId, tagId);
            super.addRow(new Object[]{tagName, exifGpsMetadata.longitude().localizedString()});
        }
        if (exifGpsMetadata.altitude() != null) {

            final String tagId   = Integer.toString(ExifTag.Id.GPS_ALTITUDE.value());
            final String tagName = TRANSLATION.translate(tagId, tagId);

            super.addRow(new Object[]{tagName, exifGpsMetadata.altitude().localizedString()});
        }
        if (exifGpsMetadata.longitude() != null && exifGpsMetadata.latitude() != null) {

            final JButton button = new JButton(Bundle.getString("TableModelExif.Button.GoogleMaps"));

            button.addActionListener(new GpsButtonListener());
            super.addRow(new Object[]{exifGpsMetadata, button});
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

        public GpsButtonListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String webBrowser = UserSettings.INSTANCE.getWebBrowser();
            if (checkWebBrowser(webBrowser)) {
                startWebBrowser(webBrowser);
            }
        }

        private boolean checkWebBrowser(String webBrowser) {
            if (webBrowser.length() <= 0) {
                MessageDisplayer.error(null, "TableModelExif.Error.WebBrowser");
                setWebBrowser();
                return false;
            }
            return true;
        }

        private void setWebBrowser() {
            SettingsDialog settingsDialog = SettingsDialog.INSTANCE;
            if (settingsDialog.isVisible()) {
                settingsDialog.toFront();
            } else {
                settingsDialog.setVisible(true);
            }
            settingsDialog.selectTab(SettingsMiscPanel.Tab.EXTERNAL_APPLICATIONS);
        }

        private void startWebBrowser(String webBrowser) {
            if (exifGpsMetadata != null) {
                String url = ExifGpsUtil.googleMapsUrl(exifGpsMetadata.longitude(), exifGpsMetadata.latitude());
                String cmd = "\"" + webBrowser + "\" \"" + url + "\"";
                logExternalAppCommand(cmd);
                External.execute(cmd);
            }
        }

        private void logExternalAppCommand(String cmd) {
            AppLog.logFinest(GpsButtonListener.class, "TableModelExif.ExternalAppCommand", cmd);
        }
    }
}
