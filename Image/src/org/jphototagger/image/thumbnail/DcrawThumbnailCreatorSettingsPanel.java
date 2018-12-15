package org.jphototagger.image.thumbnail;

import java.io.File;
import javax.swing.JFileChooser;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.image.ImagePreferencesKeys;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class DcrawThumbnailCreatorSettingsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public DcrawThumbnailCreatorSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
        readPreferences();
        buttonRemoveFile.setEnabled(StringUtil.hasContent(labelFile.getText()));
    }

    private void chooseFile() {
        FileChooserProperties props = createFileChooserProperties();
        File dcraw = FileChooserHelper.chooseFile(props);
        if (dcraw != null) {
            labelFile.setText(dcraw.getAbsolutePath());
            labelFile.setIcon(IconUtil.getSystemIcon(dcraw));
            buttonRemoveFile.setEnabled(true);
            persistFile();
        }
    }

    private FileChooserProperties createFileChooserProperties() {
        FileChooserProperties props = new FileChooserProperties();
        props.currentDirectoryPath(getDirectoryPath());
        props.dialogTitle(Bundle.getString(DcrawThumbnailCreatorSettingsPanel.class, "DcrawThumbnailCreatorSettingsPanel.FileChooser.Title"));
        props.multiSelectionEnabled(false);
        props.fileSelectionMode(JFileChooser.FILES_ONLY);
        props.propertyKeyPrefix("DcrawThumbnailCreatorPanel.FileChooser");
        return props;
    }

    private String getDirectoryPath() {
        File file = new File(labelFile.getText());
        if (file.isFile()) {
            String parent = file.getParent();
            return parent == null ? "" : parent;
        }
        return "";
    }

    private void removeFile() {
        labelFile.setText("");
        buttonRemoveFile.setEnabled(false);
        persistFile();
    }

    private void persistFile() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String filepath = labelFile.getText();
        if (StringUtil.hasContent(filepath)) {
            prefs.setString(ImagePreferencesKeys.KEY_DCRAW_FILEPATH, filepath);
        } else {
            prefs.removeKey(ImagePreferencesKeys.KEY_DCRAW_FILEPATH);
        }
    }

    private void readPreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(ImagePreferencesKeys.KEY_DCRAW_FILEPATH)) {
            File file = new File(prefs.getString(ImagePreferencesKeys.KEY_DCRAW_FILEPATH));
            labelFile.setText(file.getAbsolutePath());
            labelFile.setIcon(file.isFile()
                    ? IconUtil.getSystemIcon(file)
                    : org.jphototagger.resources.Icons.getIcon("icon_error.png"));
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelInfo = UiFactory.label();
        panelChooseFile = UiFactory.panel();
        labelFile = UiFactory.label();
        buttonChooseFile = UiFactory.button();
        buttonRemoveFile = UiFactory.button();

        setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "DcrawThumbnailCreatorSettingsPanel.border.title"))); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "DcrawThumbnailCreatorSettingsPanel.labelInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        add(labelInfo, gridBagConstraints);

        panelChooseFile.setLayout(new java.awt.GridBagLayout());

        labelFile.setText(" "); // NOI18N
        labelFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelChooseFile.add(labelFile, gridBagConstraints);

        buttonChooseFile.setText(Bundle.getString(getClass(), "DcrawThumbnailCreatorSettingsPanel.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelChooseFile.add(buttonChooseFile, gridBagConstraints);

        buttonRemoveFile.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveFile.setEnabled(false);
        buttonRemoveFile.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonRemoveFile.setPreferredSize(UiFactory.dimension(18, 18));
        buttonRemoveFile.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelChooseFile.add(buttonRemoveFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 5, 5, 5);
        add(panelChooseFile, gridBagConstraints);
    }

    private void buttonChooseFileActionPerformed(java.awt.event.ActionEvent evt) {
        chooseFile();
    }

    private void buttonRemoveFileActionPerformed(java.awt.event.ActionEvent evt) {
        removeFile();
    }

    private javax.swing.JButton buttonChooseFile;
    private javax.swing.JButton buttonRemoveFile;
    private javax.swing.JLabel labelFile;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JPanel panelChooseFile;
}
