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
import de.elmar_baumann.jpt.image.metadata.exif.entry.ExifGpsMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.entry.ExifGpsUtil;
import de.elmar_baumann.jpt.image.metadata.exif.ExifIfdEntryDisplayComparator;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadataToDisplay;
import de.elmar_baumann.jpt.image.metadata.exif.IfdEntryProxy;
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

    private File file;
    private ExifGpsMetadata gps;
    private List<IfdEntryProxy> allEntries;
    private static final Translation TRANSLATION = new Translation(
            "ExifTagIdTagNameTranslations");

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
            setExifData();
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

    private void setExifData() {
        allEntries = ExifMetadata.getExifEntries(file);
        if (allEntries != null) {
            List<IfdEntryProxy> entries = ExifMetadataToDisplay.get(allEntries);
            if (entries != null) {
                Collections.sort(entries, ExifIfdEntryDisplayComparator.INSTANCE);
                for (IfdEntryProxy entry : entries) {
                    String value = entry.stringValue();
                    if (value.length() > 0) {
                        addRow(entry);
                    }
                }
            }
            addGps();
        }
    }

    private void addGps() {
        gps = ExifGpsUtil.gpsMetadata(allEntries);
        if (gps.latitude() != null) {
            String prompt = TRANSLATION.translate(Integer.toString(
                    ExifTag.GPS_LATITUDE.tagId()));
            super.addRow(new Object[]{prompt,
                        gps.latitude().localizedString()});
        }
        if (gps.longitude() != null) {
            String prompt = TRANSLATION.translate(Integer.toString(
                    ExifTag.GPS_LONGITUDE.tagId()));
            super.addRow(new Object[]{prompt,
                        gps.longitude().localizedString()});
        }
        if (gps.altitude() != null) {
            String prompt = TRANSLATION.translate(Integer.toString(
                    ExifTag.GPS_ALTITUDE.tagId()));
            super.addRow(new Object[]{prompt,
                        gps.altitude().localizedString()});
        }
        if (gps.longitude() != null && gps.latitude() != null) {
            JButton button = new JButton(Bundle.getString("TableModelExif.Button.GoogleMaps"));
            button.addActionListener(new GpsListener());
            super.addRow(new Object[]{gps, button});
        }
    }

    private void addRow(IfdEntryProxy entry) {
        List<IfdEntryProxy> row = new ArrayList<IfdEntryProxy>();
        row.add(entry);
        row.add(entry);
        super.addRow(row.toArray(new IfdEntryProxy[row.size()]));
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private class GpsListener implements ActionListener {

        public GpsListener() {
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
            if (gps != null) {
                String url = ExifGpsUtil.googleMapsUrl(gps.longitude(),
                        gps.latitude());
                String cmd = "\"" + webBrowser + "\" \"" + url + "\"";
                logExternalAppCommand(cmd);
                External.execute(cmd);
            }
        }

        private void logExternalAppCommand(String cmd) {
            AppLog.logFinest(GpsListener.class,
                    "TableModelExif.ExternalAppCommand", cmd);
        }
    }
}
