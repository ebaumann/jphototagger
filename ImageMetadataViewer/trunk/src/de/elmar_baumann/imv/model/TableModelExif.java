package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.image.metadata.exif.entry.ExifGpsMetadata;
import de.elmar_baumann.imv.image.metadata.exif.entry.ExifGpsUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifIfdEntryDisplayComparator;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadataToDisplay;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.SettingsMiscPanel;
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
    private List<IdfEntryProxy> allEntries;
    private static final Translation TRANSLATION = new Translation(
            "ExifTagIdTagNameTranslations"); // NOI18N

    public TableModelExif() {
        setRowHeaders();
    }

    private void setRowHeaders() {
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.1")); // NOI18N
        addColumn(Bundle.getString("TableModelExif.HeaderColumn.2")); // NOI18N
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
            List<IdfEntryProxy> entries = ExifMetadataToDisplay.get(allEntries);
            if (entries != null) {
                Collections.sort(entries, ExifIfdEntryDisplayComparator.INSTANCE);
                for (IdfEntryProxy entry : entries) {
                    String value = entry.toString();
                    if (value.length() > 0) {
                        addRow(entry);
                    }
                }
            }
            addGps();
        }
    }

    private void addGps() {
        gps = ExifGpsUtil.getGpsMetadata(allEntries);
        if (gps.getLatitude() != null) {
            String prompt = TRANSLATION.translate(Integer.toString(
                    ExifTag.GPS_LATITUDE.getId()));
            super.addRow(new Object[]{prompt,
                        gps.getLatitude().localizedString()});
        }
        if (gps.getLongitude() != null) {
            String prompt = TRANSLATION.translate(Integer.toString(
                    ExifTag.GPS_LONGITUDE.getId()));
            super.addRow(new Object[]{prompt,
                        gps.getLongitude().localizedString()});
        }
        if (gps.getAltitude() != null) {
            String prompt = TRANSLATION.translate(Integer.toString(
                    ExifTag.GPS_ALTITUDE.getId()));
            super.addRow(new Object[]{prompt,
                        gps.getAltitude().localizedString()});
        }
        if (gps.getLongitude() != null && gps.getLatitude() != null) {
            JButton button = new JButton(
                    Bundle.getString("TableModelExif.Button.GoogleMaps")); // NOI18N
            button.addActionListener(new GpsListener());
            super.addRow(new Object[]{gps, button});
        }
    }

    private void addRow(IdfEntryProxy entry) {
        List<IdfEntryProxy> row = new ArrayList<IdfEntryProxy>();
        row.add(entry);
        row.add(entry);
        super.addRow(row.toArray(new IdfEntryProxy[row.size()]));
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
                MessageDisplayer.error(null, "TableModelExif.Error.WebBrowser"); // NOI18N
                setWebBrowser();
                return false;
            }
            return true;
        }

        private void setWebBrowser() {
            UserSettingsDialog settingsDialog = UserSettingsDialog.INSTANCE;
            if (settingsDialog.isVisible()) {
                settingsDialog.toFront();
            } else {
                settingsDialog.setVisible(true);
            }
            settingsDialog.selectTab(SettingsMiscPanel.Tab.EXTERNAL_APPLICATIONS);
        }

        private void startWebBrowser(String webBrowser) {
            if (gps != null) {
                String url = ExifGpsUtil.getGoogleMapsUrl(gps.getLongitude(),
                        gps.getLatitude());
                String cmd = webBrowser + " " + url; // NOI18N
                logExternalAppCommand(cmd);
                External.execute(cmd);
            }
        }

        private void logExternalAppCommand(String cmd) {
            AppLog.logFinest(GpsListener.class, Bundle.getString(
                    "TableModelExif.ExternalAppCommand", cmd)); // NOI18N
        }
    }
}
