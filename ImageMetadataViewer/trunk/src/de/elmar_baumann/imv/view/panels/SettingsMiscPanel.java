package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.model.ComboBoxModelLogfileFormatter;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;
import de.elmar_baumann.imv.view.renderer.ListCellRendererLogfileFormatter;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.io.filefilter.ExecutableFileChooserFileFilter;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-02
 */
public final class SettingsMiscPanel extends javax.swing.JPanel
        implements Persistence {

    private final ListenerProvider listenerProvider = ListenerProvider.INSTANCE;
    private String lastSelectedAutocopyDirectory = ""; // NOI18N

    /** Creates new form SettingsMiscPanel */
    public SettingsMiscPanel() {
        initComponents();
    }

    private void chooseAutocopyDirectory() {
        File file = chooseDirectory(new File(lastSelectedAutocopyDirectory));
        if (file != null) {
            String directory = file.getAbsolutePath();
            labelAutocopyDirectory.setText(directory);
            lastSelectedAutocopyDirectory = directory;
            labelAutocopyDirectory.setIcon(IconUtil.getSystemIcon(file));
            UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                    UserSettingsChangeEvent.Type.AUTOCOPY_DIRECTORY, this);
            evt.setAutoCopyDirectory(file);
            notifyChangeListener(evt);
        }
    }

    private File chooseDirectory(File startDirectory) {
        File dir = null;
        DirectoryChooser dialog = new DirectoryChooser(null, startDirectory,
                UserSettings.INSTANCE.getDefaultDirectoryChooserOptions());

        dialog.setVisible(true);

        if (dialog.accepted()) {
            dir = dialog.getSelectedDirectories().get(0);
        }
        return dir;
    }

    private File chooseFile(FileFilter filter) {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setVisible(true);
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        return file;
    }

    private void handleActionPerformedCheckBoxIsAcceptHiddenDirectories() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.IS_ACCEPT_HIDDEN_DIRECTORIES, this);
        evt.setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.
                isSelected());
        notifyChangeListener(evt);
    }

    private void handleActionPerformedCheckBoxTreeDirectoriesSelectLastDirectory() {
        UserSettingsChangeEvent evt =
                new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.TREE_DIRECTORIES_SELECT_LAST_DIRECTORY,
                this);
        evt.setTreeDirectoriesSelectLastDirectory(checkBoxTreeDirectoriesSelectLastDirectory.
                isSelected());
        notifyChangeListener(evt);
    }

    private void handleActionPerformedChooseDatabaseDirectory() {
        File file = chooseDirectory(new File(UserSettings.INSTANCE.
                getDatabaseDirectoryName()));
        if (file != null) {
            setDatabaseDirectoryName(file.getAbsolutePath());
        }
    }

    private void setDatabaseDirectoryName(String directoryName) {
        labelDatabaseDirectory.setText(directoryName);
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.DATABASE_DIRECTORY, this);
        evt.setDatabaseDirectoryName(directoryName);
        notifyChangeListener(evt);
    }

    private void handleActionPerformedSetStandardDatabaseDirectory() {
        setDatabaseDirectoryName(UserSettings.INSTANCE.
                getDefaultDatabaseDirectoryName());
    }

    private void handleActionPerformedChooseWebBrowser() {
        File programFile = chooseFile(new ExecutableFileChooserFileFilter());
        if (programFile != null) {
            String browserPath = programFile.getAbsolutePath();
            labelWebBrowser.setText(browserPath);
            labelWebBrowser.setIcon(IconUtil.getSystemIcon(programFile));
            UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                    UserSettingsChangeEvent.Type.WEB_BROWSER, this);
            evt.setWebBrowser(browserPath);
            notifyChangeListener(evt);
        }
    }

    private void handleActionPerformedComboBoxLogLevel() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.LOG_LEVEL, this);
        evt.setLogLevel(Level.parse(
                comboBoxLogLevel.getSelectedItem().toString()));
        notifyChangeListener(evt);
    }

    private void handleActionPerformedComboBoxLogfileFormatterClass() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.LOGFILE_FORMATTER_CLASS, this);
        evt.setLogfileFormatterClass(
                (Class) comboBoxLogfileFormatterClass.getSelectedItem());
        notifyChangeListener(evt);
    }

    private synchronized void notifyChangeListener(UserSettingsChangeEvent evt) {
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    private void checkLogLevel() {
        if (comboBoxLogLevel.getSelectedIndex() < 0) {
            comboBoxLogLevel.setSelectedIndex(0);
        }
    }

    @Override
    public void readProperties() {
        checkLogLevel();
        UserSettings settings = UserSettings.INSTANCE;
        readAutoCopyDirectoryProperties(settings);
        readWebBrowserProperties(settings);
        comboBoxLogLevel.setSelectedItem(
                settings.getLogLevel().getLocalizedName());
        ComboBoxModelLogfileFormatter modelLogfileFormatter =
                (ComboBoxModelLogfileFormatter) comboBoxLogfileFormatterClass.
                getModel();
        modelLogfileFormatter.setSelectedItem(
                settings.getLogfileFormatterClass());
        checkBoxIsAcceptHiddenDirectories.setSelected(
                settings.isAcceptHiddenDirectories());
        checkBoxTreeDirectoriesSelectLastDirectory.setSelected(
                settings.isTreeDirectoriesSelectLastDirectory());
        labelDatabaseDirectory.setText(UserSettings.INSTANCE.
                getDatabaseDirectoryName());
    }

    private void readAutoCopyDirectoryProperties(UserSettings settings) {
        File lastAcDirectory = settings.getAutocopyDirectory();
        if (lastAcDirectory != null && lastAcDirectory.exists()) {
            String lastAcDirectoryName = lastAcDirectory.getAbsolutePath();
            labelAutocopyDirectory.setText(lastAcDirectoryName);
            lastSelectedAutocopyDirectory = lastAcDirectoryName;
            labelAutocopyDirectory.setIcon(IconUtil.getSystemIcon(
                    lastAcDirectory));
        }
    }

    private void readWebBrowserProperties(UserSettings settings) {
        File webBrowser = new File(settings.getWebBrowser());
        labelWebBrowser.setText(webBrowser.getAbsolutePath());
        if (webBrowser.exists()) {
            labelWebBrowser.setIcon(IconUtil.getSystemIcon(webBrowser));
        }
    }

    @Override
    public void writeProperties() {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelWebBrowser = new javax.swing.JPanel();
        labelWebBrowser = new javax.swing.JLabel();
        buttonChooseWebBrowser = new javax.swing.JButton();
        panelDatabaseDirectory = new javax.swing.JPanel();
        labelDatabaseDirectory = new javax.swing.JLabel();
        buttonSetStandardDatabaseDirectoryName = new javax.swing.JButton();
        buttonChooseDatabaseDirectory = new javax.swing.JButton();
        labelInfoDatabaseDirectory = new javax.swing.JLabel();
        panelLogfile = new javax.swing.JPanel();
        labelLogLevel = new javax.swing.JLabel();
        comboBoxLogLevel = new javax.swing.JComboBox();
        labelLogLogfileFormatterClass = new javax.swing.JLabel();
        comboBoxLogfileFormatterClass = new javax.swing.JComboBox();
        labelInfoLogfile = new javax.swing.JLabel();
        panelAutoCopyDirectory = new javax.swing.JPanel();
        labelAutocopyDirectory = new javax.swing.JLabel();
        buttonChooseAutocopyDirectory = new javax.swing.JButton();
        panelMisc = new javax.swing.JPanel();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        checkBoxTreeDirectoriesSelectLastDirectory = new javax.swing.JCheckBox();

        labelWebBrowser.setForeground(new java.awt.Color(0, 0, 255));
        labelWebBrowser.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseWebBrowser.setMnemonic('u');
        buttonChooseWebBrowser.setText(Bundle.getString("SettingsMiscPanel.buttonChooseWebBrowser.text")); // NOI18N
        buttonChooseWebBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseWebBrowserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelWebBrowserLayout = new javax.swing.GroupLayout(panelWebBrowser);
        panelWebBrowser.setLayout(panelWebBrowserLayout);
        panelWebBrowserLayout.setHorizontalGroup(
            panelWebBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelWebBrowserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelWebBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelWebBrowser, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addComponent(buttonChooseWebBrowser, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelWebBrowserLayout.setVerticalGroup(
            panelWebBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelWebBrowserLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelWebBrowser, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseWebBrowser)
                .addContainerGap(139, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("SettingsMiscPanel.panelWebBrowser.TabConstraints.tabTitle"), panelWebBrowser); // NOI18N

        labelDatabaseDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonSetStandardDatabaseDirectoryName.setMnemonic('S');
        buttonSetStandardDatabaseDirectoryName.setText(Bundle.getString("SettingsMiscPanel.buttonSetStandardDatabaseDirectoryName.text")); // NOI18N
        buttonSetStandardDatabaseDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetStandardDatabaseDirectoryNameActionPerformed(evt);
            }
        });

        buttonChooseDatabaseDirectory.setMnemonic('w');
        buttonChooseDatabaseDirectory.setText(Bundle.getString("SettingsMiscPanel.buttonChooseDatabaseDirectory.text")); // NOI18N
        buttonChooseDatabaseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDatabaseDirectoryActionPerformed(evt);
            }
        });

        labelInfoDatabaseDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDatabaseDirectory.setText(Bundle.getString("SettingsMiscPanel.labelInfoDatabaseDirectory.text")); // NOI18N

        javax.swing.GroupLayout panelDatabaseDirectoryLayout = new javax.swing.GroupLayout(panelDatabaseDirectory);
        panelDatabaseDirectory.setLayout(panelDatabaseDirectoryLayout);
        panelDatabaseDirectoryLayout.setHorizontalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(buttonSetStandardDatabaseDirectoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseDatabaseDirectory))
                    .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(106, 106, 106)))
                .addContainerGap())
        );
        panelDatabaseDirectoryLayout.setVerticalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChooseDatabaseDirectory)
                    .addComponent(buttonSetStandardDatabaseDirectoryName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(118, 118, 118))
        );

        tabbedPane.addTab(Bundle.getString("SettingsMiscPanel.panelDatabaseDirectory.TabConstraints.tabTitle"), panelDatabaseDirectory); // NOI18N

        labelLogLevel.setText(Bundle.getString("SettingsMiscPanel.labelLogLevel.text")); // NOI18N

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });

        labelLogLogfileFormatterClass.setText(Bundle.getString("SettingsMiscPanel.labelLogLogfileFormatterClass.text")); // NOI18N

        comboBoxLogfileFormatterClass.setModel(new ComboBoxModelLogfileFormatter());
        comboBoxLogfileFormatterClass.setRenderer(new ListCellRendererLogfileFormatter());
        comboBoxLogfileFormatterClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogfileFormatterClassActionPerformed(evt);
            }
        });

        labelInfoLogfile.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoLogfile.setText(Bundle.getString("SettingsMiscPanel.labelInfoLogfile.text")); // NOI18N

        javax.swing.GroupLayout panelLogfileLayout = new javax.swing.GroupLayout(panelLogfile);
        panelLogfile.setLayout(panelLogfileLayout);
        panelLogfileLayout.setHorizontalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLogfileLayout.createSequentialGroup()
                        .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelLogLogfileFormatterClass, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelLogLevel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxLogfileFormatterClass, 0, 230, Short.MAX_VALUE)
                            .addComponent(comboBoxLogLevel, 0, 230, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(panelLogfileLayout.createSequentialGroup()
                        .addComponent(labelInfoLogfile, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                        .addGap(118, 118, 118))))
        );
        panelLogfileLayout.setVerticalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLogLevel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxLogfileFormatterClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLogLogfileFormatterClass, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoLogfile, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addGap(100, 100, 100))
        );

        tabbedPane.addTab(Bundle.getString("SettingsMiscPanel.panelLogfile.TabConstraints.tabTitle"), panelLogfile); // NOI18N

        labelAutocopyDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelAutocopyDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseAutocopyDirectory.setMnemonic('a');
        buttonChooseAutocopyDirectory.setText(Bundle.getString("SettingsMiscPanel.buttonChooseAutocopyDirectory.text")); // NOI18N
        buttonChooseAutocopyDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseAutocopyDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAutoCopyDirectoryLayout = new javax.swing.GroupLayout(panelAutoCopyDirectory);
        panelAutoCopyDirectory.setLayout(panelAutoCopyDirectoryLayout);
        panelAutoCopyDirectoryLayout.setHorizontalGroup(
            panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoCopyDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAutocopyDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addComponent(buttonChooseAutocopyDirectory, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelAutoCopyDirectoryLayout.setVerticalGroup(
            panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoCopyDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelAutocopyDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseAutocopyDirectory)
                .addContainerGap(139, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("SettingsMiscPanel.panelAutoCopyDirectory.TabConstraints.tabTitle"), panelAutoCopyDirectory); // NOI18N

        checkBoxIsAcceptHiddenDirectories.setMnemonic('o');
        checkBoxIsAcceptHiddenDirectories.setText(Bundle.getString("SettingsMiscPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        checkBoxTreeDirectoriesSelectLastDirectory.setMnemonic('s');
        checkBoxTreeDirectoriesSelectLastDirectory.setText(Bundle.getString("SettingsMiscPanel.checkBoxTreeDirectoriesSelectLastDirectory.text")); // NOI18N
        checkBoxTreeDirectoriesSelectLastDirectory.setPreferredSize(new java.awt.Dimension(631, 28));
        checkBoxTreeDirectoriesSelectLastDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxTreeDirectoriesSelectLastDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMiscLayout = new javax.swing.GroupLayout(panelMisc);
        panelMisc.setLayout(panelMiscLayout);
        panelMiscLayout.setHorizontalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsAcceptHiddenDirectories)
                    .addComponent(checkBoxTreeDirectoriesSelectLastDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelMiscLayout.setVerticalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxTreeDirectoriesSelectLastDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("SettingsMiscPanel.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void comboBoxLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLogLevelActionPerformed
    handleActionPerformedComboBoxLogLevel();
}//GEN-LAST:event_comboBoxLogLevelActionPerformed

private void comboBoxLogfileFormatterClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLogfileFormatterClassActionPerformed
    handleActionPerformedComboBoxLogfileFormatterClass();
}//GEN-LAST:event_comboBoxLogfileFormatterClassActionPerformed

private void buttonChooseAutocopyDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseAutocopyDirectoryActionPerformed
    chooseAutocopyDirectory();
}//GEN-LAST:event_buttonChooseAutocopyDirectoryActionPerformed

private void checkBoxIsAcceptHiddenDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed
    handleActionPerformedCheckBoxIsAcceptHiddenDirectories();
}//GEN-LAST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed

private void buttonChooseWebBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseWebBrowserActionPerformed
    handleActionPerformedChooseWebBrowser();
}//GEN-LAST:event_buttonChooseWebBrowserActionPerformed

private void checkBoxTreeDirectoriesSelectLastDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxTreeDirectoriesSelectLastDirectoryActionPerformed
    handleActionPerformedCheckBoxTreeDirectoriesSelectLastDirectory();
}//GEN-LAST:event_checkBoxTreeDirectoriesSelectLastDirectoryActionPerformed

private void buttonChooseDatabaseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDatabaseDirectoryActionPerformed
    handleActionPerformedChooseDatabaseDirectory();
}//GEN-LAST:event_buttonChooseDatabaseDirectoryActionPerformed

private void buttonSetStandardDatabaseDirectoryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed
    handleActionPerformedSetStandardDatabaseDirectory();
}//GEN-LAST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseAutocopyDirectory;
    private javax.swing.JButton buttonChooseDatabaseDirectory;
    private javax.swing.JButton buttonChooseWebBrowser;
    private javax.swing.JButton buttonSetStandardDatabaseDirectoryName;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JCheckBox checkBoxTreeDirectoriesSelectLastDirectory;
    private javax.swing.JComboBox comboBoxLogLevel;
    private javax.swing.JComboBox comboBoxLogfileFormatterClass;
    private javax.swing.JLabel labelAutocopyDirectory;
    private javax.swing.JLabel labelDatabaseDirectory;
    private javax.swing.JLabel labelInfoDatabaseDirectory;
    private javax.swing.JLabel labelInfoLogfile;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelLogLogfileFormatterClass;
    private javax.swing.JLabel labelWebBrowser;
    private javax.swing.JPanel panelAutoCopyDirectory;
    private javax.swing.JPanel panelDatabaseDirectory;
    private javax.swing.JPanel panelLogfile;
    private javax.swing.JPanel panelMisc;
    private javax.swing.JPanel panelWebBrowser;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
