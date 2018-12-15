package org.jphototagger.exif.kmlexport;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.exif.ExifPreferencesKeys;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ExportGPSToKMLSettingsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public ExportGPSToKMLSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        readAddFilenameToGpsLocationExport();
    }

    private void readAddFilenameToGpsLocationExport() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        boolean add = preferences.getBoolean(ExifPreferencesKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT);

        checkBoxAddFilenameToGpsLocationExport.setSelected(add);
    }

    private void storeAddFilenameToGpsLocationExport() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        boolean add = checkBoxAddFilenameToGpsLocationExport.isSelected();

        preferences.setBoolean(ExifPreferencesKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT, add);
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        checkBoxAddFilenameToGpsLocationExport = UiFactory.checkBox();
        panelFill = UiFactory.panel();
        labelVersion = UiFactory.label();

        setName("Form"); // NOI18N
        setLayout(new GridBagLayout());

        checkBoxAddFilenameToGpsLocationExport.setText(Bundle.getString(getClass(), "ExportGPSToKMLSettingsPanel.checkBoxAddFilenameToGpsLocationExport.text")); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.setName("checkBoxAddFilenameToGpsLocationExport"); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                checkBoxAddFilenameToGpsLocationExportActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(checkBoxAddFilenameToGpsLocationExport, gridBagConstraints);

        panelFill.setName("panelFill"); // NOI18N
        panelFill.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panelFill, gridBagConstraints);

        labelVersion.setText(Bundle.getString(getClass(), "ExportGPSToKMLSettingsPanel.panelVersion.text")); // NOI18N
        labelVersion.setName("panelVersion"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        add(labelVersion, gridBagConstraints);
    }

    private void checkBoxAddFilenameToGpsLocationExportActionPerformed(ActionEvent evt) {
        storeAddFilenameToGpsLocationExport();
    }

    private JCheckBox checkBoxAddFilenameToGpsLocationExport;
    private JLabel labelVersion;
    private JPanel panelFill;
}
