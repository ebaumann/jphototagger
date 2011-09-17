package org.jphototagger.program.view.panels;

import java.awt.Container;
import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Preferences;
import org.jphototagger.domain.event.UserPropertyChangedEvent;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.iptc.IptcStorageKeys;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.dialog.DirectoryChooser.Option;
import org.jphototagger.program.app.AppStorageKeys;
import org.jphototagger.program.controller.misc.UpdateCheckController;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.helper.CopyFiles;
import org.jphototagger.program.helper.CopyFiles.Options;
import org.jphototagger.program.model.IptcCharsetComboBoxModel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Persistence;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsMiscPanel extends javax.swing.JPanel implements Persistence {

    private static final long serialVersionUID = 479354601163285718L;

    public SettingsMiscPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    private File chooseDirectory(File startDirectory) {
        File dir = null;
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), startDirectory, showHiddenDirs);

        dlg.setStorageKey("SettingsMiscPanel.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            dir = dlg.getSelectedDirectories().get(0);
        }

        return dir;
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void handleActionPerformedCheckBoxIsAcceptHiddenDirectories() {
        setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.isSelected());
    }

    private void handleActionPerformedChooseDatabaseDirectory() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        File databaseDirectory = provider.getFileRepositoryDirectory();

        File file = chooseDirectory(databaseDirectory);

        if (file != null) {
            setDatabaseDirectoryName(file.getAbsolutePath());
        }
    }
    private void setAcceptHiddenDirectories(boolean accept) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES, accept);
    }

    private void setDatabaseDirectoryName(String directoryName) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        setIconDatabaseDirectory();
        labelDatabaseDirectory.setText(directoryName);
        setDatabaseDirectoryName(directoryName);
        storage.setString(FileRepositoryProvider.KEY_FILE_REPOSITORY_DIRECTORY, directoryName);
    }

    private void setIconDatabaseDirectory() {
        File dir = new File(labelDatabaseDirectory.getText());

        if (dir.isDirectory()) {
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(dir);

            labelDatabaseDirectory.setIcon(icon);
        }
    }

    private void handleActionPerformedSetStandardDatabaseDirectory() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);

        setDatabaseDirectoryName(provider.getDefaultFileRepositoryDirectory().getAbsolutePath());
    }

    private void handleActionPerformedCheckBoxDisplaySearchButton() {
        setDisplaySearchButton(checkBoxDisplaySearchButton.isSelected());
    }

    private void setDisplaySearchButton(boolean display) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppStorageKeys.KEY_UI_DISPLAY_SEARCH_BUTTON, display);
    }

    private void handleActionPerformedCopyMoveFiles() {
        boolean isConfirmOverwrite = radioButtonCopyMoveFileConfirmOverwrite.isSelected();
        boolean renameSourceFileIfTargetFileExists = radioButtonCopyMoveFileRenameIfExists.isSelected();
        Options options = isConfirmOverwrite
                          ? CopyFiles.Options.CONFIRM_OVERWRITE
                          : renameSourceFileIfTargetFileExists
                            ? CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS
                            : CopyFiles.Options.CONFIRM_OVERWRITE;

        setCopyMoveFilesOptions(options);
    }

    private void setCopyMoveFilesOptions(Options options) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setInt(AppStorageKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES, options.getInt());
    }

    private void handleActionPerformedAutoDownload() {
        setCheckForUpdates(checkBoxAutoDownloadCheck.isSelected());
    }

    private void setCheckForUpdates(boolean auto) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppStorageKeys.KEY_CHECK_FOR_UPDATES, auto);
    }

    private void handleActionComboBoxIptcCharset() {
        setIptcCharset(comboBoxIptcCharset.getSelectedItem().toString());
    }

    private void setIptcCharset(String charset) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setString(IptcStorageKeys.KEY_IPTC_CHARSET, charset);
    }

    private void handleActionPerformedCheckBoxAddFilenameToGpsLocationExport() {
        setAddFilenameToGpsLocationExport(checkBoxAddFilenameToGpsLocationExport.isSelected());
    }

    private void setAddFilenameToGpsLocationExport(boolean add) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppStorageKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT, add);
    }

    @EventSubscriber(eventClass = UserPropertyChangedEvent.class)
    public void applySettings(UserPropertyChangedEvent evt) {
        if (AppStorageKeys.KEY_CHECK_FOR_UPDATES.equals(evt.getPropertyKey())) {
            checkBoxAutoDownloadCheck.setSelected((Boolean)evt.getNewValue());
        } else if (IptcStorageKeys.KEY_IPTC_CHARSET.equals(evt.getPropertyKey())) {
            setIptcCharsetFromUserSettings();
        }
    }

    private void setIptcCharsetFromUserSettings() {
        comboBoxIptcCharset.getModel().setSelectedItem(getIptcCharset());
    }

    private String getIptcCharset() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);
        String charset = storage.getString(IptcStorageKeys.KEY_IPTC_CHARSET);

        return charset.isEmpty()
                ? "ISO-8859-1"
                : charset;
    }

    @Override
    public void readProperties() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);

        checkBoxAutoDownloadCheck.setSelected(isCheckForUpdates());
        checkBoxDisplaySearchButton.setSelected(isDisplaySearchButton());
        checkBoxIsAcceptHiddenDirectories.setSelected(isAcceptHiddenDirectories());
        checkBoxAddFilenameToGpsLocationExport.setSelected(isAddFilenameToGpsLocationExport());
        setIptcCharsetFromUserSettings();
        labelDatabaseDirectory.setText(provider.getFileRepositoryDirectory().getAbsolutePath());
        radioButtonCopyMoveFileConfirmOverwrite.setSelected(getCopyMoveFilesOptions().equals(CopyFiles.Options.CONFIRM_OVERWRITE));
        radioButtonCopyMoveFileRenameIfExists.setSelected(getCopyMoveFilesOptions().equals(CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
        setIconDatabaseDirectory();
    }

    private static boolean isAddFilenameToGpsLocationExport() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppStorageKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT)
                ? storage.getBoolean(AppStorageKeys.KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT)
                : false;
    }

    private boolean isCheckForUpdates() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppStorageKeys.KEY_CHECK_FOR_UPDATES)
                ? storage.getBoolean(AppStorageKeys.KEY_CHECK_FOR_UPDATES)
                : true;
    }

    private boolean isDisplaySearchButton() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppStorageKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                ? storage.getBoolean(AppStorageKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                : true;
    }

    private CopyFiles.Options getCopyMoveFilesOptions() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppStorageKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES)
                ? CopyFiles.Options.fromInt(storage.getInt(AppStorageKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES))
                : CopyFiles.Options.CONFIRM_OVERWRITE;
    }

    @Override
    public void writeProperties() {
    }

    private void checkDownload() {
        ControllerFactory.INSTANCE.getController(
                UpdateCheckController.class).actionPerformed(null);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        checkBoxAutoDownloadCheck = new javax.swing.JCheckBox();
        buttonCheckDownload = new javax.swing.JButton();
        checkBoxDisplaySearchButton = new javax.swing.JCheckBox();
        checkBoxAddFilenameToGpsLocationExport = new javax.swing.JCheckBox();
        panelCopyMoveFiles = new javax.swing.JPanel();
        radioButtonCopyMoveFileConfirmOverwrite = new javax.swing.JRadioButton();
        radioButtonCopyMoveFileRenameIfExists = new javax.swing.JRadioButton();
        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();
        panelDatabaseDirectory = new javax.swing.JPanel();
        labelInfoDatabaseDirectory = new javax.swing.JLabel();
        buttonSetStandardDatabaseDirectoryName = new javax.swing.JButton();
        buttonChooseDatabaseDirectory = new javax.swing.JButton();
        labelDatabaseDirectory = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        checkBoxIsAcceptHiddenDirectories.setText(bundle.getString("SettingsMiscPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.setName("checkBoxIsAcceptHiddenDirectories"); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        checkBoxAutoDownloadCheck.setText(bundle.getString("SettingsMiscPanel.checkBoxAutoDownloadCheck.text")); // NOI18N
        checkBoxAutoDownloadCheck.setName("checkBoxAutoDownloadCheck"); // NOI18N
        checkBoxAutoDownloadCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoDownloadCheckActionPerformed(evt);
            }
        });

        buttonCheckDownload.setText(bundle.getString("SettingsMiscPanel.buttonCheckDownload.text")); // NOI18N
        buttonCheckDownload.setName("buttonCheckDownload"); // NOI18N
        buttonCheckDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckDownloadActionPerformed(evt);
            }
        });

        checkBoxDisplaySearchButton.setText(bundle.getString("SettingsMiscPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.setName("checkBoxDisplaySearchButton"); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });

        checkBoxAddFilenameToGpsLocationExport.setText(bundle.getString("SettingsMiscPanel.checkBoxAddFilenameToGpsLocationExport.text")); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.setName("checkBoxAddFilenameToGpsLocationExport"); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAddFilenameToGpsLocationExportActionPerformed(evt);
            }
        });

        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsMiscPanel.panelCopyMoveFiles.border.title"))); // NOI18N
        panelCopyMoveFiles.setName("panelCopyMoveFiles"); // NOI18N

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(bundle.getString("SettingsMiscPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.setName("radioButtonCopyMoveFileConfirmOverwrite"); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(bundle.getString("SettingsMiscPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
        radioButtonCopyMoveFileRenameIfExists.setName("radioButtonCopyMoveFileRenameIfExists"); // NOI18N
        radioButtonCopyMoveFileRenameIfExists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileRenameIfExistsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCopyMoveFilesLayout = new javax.swing.GroupLayout(panelCopyMoveFiles);
        panelCopyMoveFiles.setLayout(panelCopyMoveFilesLayout);
        panelCopyMoveFilesLayout.setHorizontalGroup(
            panelCopyMoveFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCopyMoveFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCopyMoveFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(radioButtonCopyMoveFileRenameIfExists, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addComponent(radioButtonCopyMoveFileConfirmOverwrite, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelCopyMoveFilesLayout.setVerticalGroup(
            panelCopyMoveFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCopyMoveFilesLayout.createSequentialGroup()
                .addComponent(radioButtonCopyMoveFileConfirmOverwrite)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCopyMoveFileRenameIfExists)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        labelIptcCharset.setLabelFor(comboBoxIptcCharset);
        labelIptcCharset.setText(bundle.getString("SettingsMiscPanel.labelIptcCharset.text")); // NOI18N
        labelIptcCharset.setName("labelIptcCharset"); // NOI18N

        comboBoxIptcCharset.setModel(new IptcCharsetComboBoxModel());
        comboBoxIptcCharset.setName("comboBoxIptcCharset"); // NOI18N
        comboBoxIptcCharset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });

        panelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsMiscPanel.panelDatabaseDirectory.border.title"))); // NOI18N
        panelDatabaseDirectory.setName("panelDatabaseDirectory"); // NOI18N

        labelInfoDatabaseDirectory.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoDatabaseDirectory.setText(bundle.getString("SettingsMiscPanel.labelInfoDatabaseDirectory.text")); // NOI18N
        labelInfoDatabaseDirectory.setName("labelInfoDatabaseDirectory"); // NOI18N

        buttonSetStandardDatabaseDirectoryName.setText(bundle.getString("SettingsMiscPanel.buttonSetStandardDatabaseDirectoryName.text")); // NOI18N
        buttonSetStandardDatabaseDirectoryName.setName("buttonSetStandardDatabaseDirectoryName"); // NOI18N
        buttonSetStandardDatabaseDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetStandardDatabaseDirectoryNameActionPerformed(evt);
            }
        });

        buttonChooseDatabaseDirectory.setText(bundle.getString("SettingsMiscPanel.buttonChooseDatabaseDirectory.text")); // NOI18N
        buttonChooseDatabaseDirectory.setName("buttonChooseDatabaseDirectory"); // NOI18N
        buttonChooseDatabaseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDatabaseDirectoryActionPerformed(evt);
            }
        });

        labelDatabaseDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDatabaseDirectory.setName("labelDatabaseDirectory"); // NOI18N

        javax.swing.GroupLayout panelDatabaseDirectoryLayout = new javax.swing.GroupLayout(panelDatabaseDirectory);
        panelDatabaseDirectory.setLayout(panelDatabaseDirectoryLayout);
        panelDatabaseDirectoryLayout.setHorizontalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetStandardDatabaseDirectoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseDatabaseDirectory)))
                .addContainerGap())
        );
        panelDatabaseDirectoryLayout.setVerticalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSetStandardDatabaseDirectoryName)
                    .addComponent(buttonChooseDatabaseDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxAutoDownloadCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonCheckDownload))
                    .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxAddFilenameToGpsLocationExport)
                    .addComponent(checkBoxIsAcceptHiddenDirectories)
                    .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelIptcCharset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxAutoDownloadCheck)
                    .addComponent(buttonCheckDownload))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAddFilenameToGpsLocationExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIptcCharset)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents

    private void checkBoxAutoDownloadCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAutoDownloadCheckActionPerformed
        handleActionPerformedAutoDownload();
    }//GEN-LAST:event_checkBoxAutoDownloadCheckActionPerformed

    private void radioButtonCopyMoveFileRenameIfExistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCopyMoveFileRenameIfExistsActionPerformed
        handleActionPerformedCopyMoveFiles();
    }//GEN-LAST:event_radioButtonCopyMoveFileRenameIfExistsActionPerformed

    private void radioButtonCopyMoveFileConfirmOverwriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCopyMoveFileConfirmOverwriteActionPerformed
        handleActionPerformedCopyMoveFiles();
    }//GEN-LAST:event_radioButtonCopyMoveFileConfirmOverwriteActionPerformed

    private void checkBoxIsAcceptHiddenDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed
        handleActionPerformedCheckBoxIsAcceptHiddenDirectories();
    }//GEN-LAST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed

    private void buttonChooseDatabaseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDatabaseDirectoryActionPerformed
        handleActionPerformedChooseDatabaseDirectory();
    }//GEN-LAST:event_buttonChooseDatabaseDirectoryActionPerformed

    private void buttonSetStandardDatabaseDirectoryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed
        handleActionPerformedSetStandardDatabaseDirectory();
    }//GEN-LAST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed

    private void checkBoxDisplaySearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplaySearchButtonActionPerformed
        handleActionPerformedCheckBoxDisplaySearchButton();
    }//GEN-LAST:event_checkBoxDisplaySearchButtonActionPerformed

    private void comboBoxIptcCharsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxIptcCharsetActionPerformed
        handleActionComboBoxIptcCharset();
    }//GEN-LAST:event_comboBoxIptcCharsetActionPerformed

    private void buttonCheckDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCheckDownloadActionPerformed
        checkDownload();
    }//GEN-LAST:event_buttonCheckDownloadActionPerformed

    private void checkBoxAddFilenameToGpsLocationExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAddFilenameToGpsLocationExportActionPerformed
        handleActionPerformedCheckBoxAddFilenameToGpsLocationExport();
    }//GEN-LAST:event_checkBoxAddFilenameToGpsLocationExportActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCheckDownload;
    private javax.swing.JButton buttonChooseDatabaseDirectory;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetStandardDatabaseDirectoryName;
    private javax.swing.JCheckBox checkBoxAddFilenameToGpsLocationExport;
    private javax.swing.JCheckBox checkBoxAutoDownloadCheck;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JComboBox comboBoxIptcCharset;
    private javax.swing.JLabel labelDatabaseDirectory;
    private javax.swing.JLabel labelInfoDatabaseDirectory;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JPanel panelCopyMoveFiles;
    private javax.swing.JPanel panelDatabaseDirectory;
    private javax.swing.JRadioButton radioButtonCopyMoveFileConfirmOverwrite;
    private javax.swing.JRadioButton radioButtonCopyMoveFileRenameIfExists;
    // End of variables declaration//GEN-END:variables
}
