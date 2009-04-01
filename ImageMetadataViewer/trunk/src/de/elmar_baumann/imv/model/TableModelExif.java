package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.metadata.exif.ExifGpsMetadata;
import de.elmar_baumann.imv.image.metadata.exif.ExifGpsUtil;
import de.elmar_baumann.imv.image.metadata.exif.ExifIfdEntryDisplayComparator;
import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Translation;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.lib.runtime.External;
import de.elmar_baumann.lib.template.Pair;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
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
    private static final Translation translation = new Translation("ExifTagIdTagNameTranslations"); // NOI18N

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
            AppLog.logWarning(getClass(), ex);
        }
    }

    /**
     * Entfernt alle EXIF-Daten.
     */
    public void removeAllElements() {
        getDataVector().removeAllElements();

    }

    private void setExifData() {
        allEntries = ExifMetadata.getMetadata(file);
        if (allEntries != null) {
            List<IdfEntryProxy> entries = ExifMetadata.getDisplayableMetadata(allEntries);
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
        gps = ExifMetadata.getGpsMetadata(allEntries);
        if (gps.getLongitude() != null) {
            String prompt = translation.translate(Integer.toString(ExifTag.GPS_LONGITUDE.getId()));
            super.addRow(new Object[]{prompt, gps.getLongitude().localizedString()});
        }
        if (gps.getLatitude() != null) {
            String prompt = translation.translate(Integer.toString(ExifTag.GPS_LATITUDE.getId()));
            super.addRow(new Object[]{prompt, gps.getLatitude().localizedString()});
        }
        if (gps.getAltitude() != null) {
            String prompt = translation.translate(Integer.toString(ExifTag.GPS_ALTITUDE.getId()));
            super.addRow(new Object[]{prompt, gps.getAltitude().localizedString()});
        }
        if (gps.getLongitude() != null && gps.getLatitude() != null) {
            JButton button = new JButton("Google Maps...");
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
                errorMessageWebBrowser();
                setWebBrowser();
                return false;
            }
            return true;
        }

        private void errorMessage(Pair<byte[], byte[]> output) {
            byte[] stderr = output.getSecond();
            String message = (stderr == null ? "" : new String(stderr).trim());
            if (!message.isEmpty()) {
                message = Bundle.getString("ThumbnailUtil.ErrorMessage.ExternalProgram") + message;
                AppLog.logWarning(GpsListener.class, message);
            }
        }

        private void errorMessageWebBrowser() {
            JOptionPane.showMessageDialog(
                    null,
                    "Bitte wählen Sie einen Webbrowser aus zum Anzeigen von Google Maps und versuchen es anschließend erneut!",
                    "Fehler", JOptionPane.ERROR_MESSAGE,
                    AppIcons.getMediumAppIcon());
        }

        private void setWebBrowser() {
            UserSettingsDialog settingsDialog = UserSettingsDialog.INSTANCE;
            if (settingsDialog.isVisible()) {
                settingsDialog.toFront();
            } else {
                settingsDialog.setVisible(true);
            }
            settingsDialog.selectTab(UserSettingsDialog.Tab.MISC);
        }

        private void startWebBrowser(String webBrowser) {
            if (gps != null) {
                String url = ExifGpsUtil.getGoogleMapsUrl(gps.getLongitude(), gps.getLatitude());
                String cmd = webBrowser + " " + url;
                logExternalAppCommand(cmd);
                Pair<byte[], byte[]> output = External.executeGetOutput(cmd);
                if (output.getSecond() != null) {
                    errorMessage(output);
                }
            }
        }

        private void logExternalAppCommand(String cmd) {
            MessageFormat msg = new MessageFormat("Rufe Webbrowser auf für Google Maps: ");
            Object[] params = {cmd};
            AppLog.logFinest(GpsListener.class, msg.format(params));
        }
    }
}
