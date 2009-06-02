package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.model.ComboBoxModelLogfileFormatter;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.renderer.ListCellRendererLogfileFormatter;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.io.ExecutableFileChooserFileFilter;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/02
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
        DirectoryChooser dialog = new DirectoryChooser(null, startDirectory, UserSettings.INSTANCE.getDefaultDirectoryChooserOptions());
        ViewUtil.setDirectoryTreeModel(dialog);

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
        evt.setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.isSelected());
        notifyChangeListener(evt);
    }

    private void handleActionPerformedCheckBoxTreeDirectoriesSelectLastDirectory() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.TREE_DIRECTORIES_SELECT_LAST_DIRECTORY, this);
        evt.setTreeDirectoriesSelectLastDirectory(checkBoxTreeDirectoriesSelectLastDirectory.isSelected());
        notifyChangeListener(evt);
    }

    private void handleActionPerformedChooseDatabaseDirectory() {
        File file = chooseDirectory(new File(UserSettings.INSTANCE.getDatabaseDirectoryName()));
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
        setDatabaseDirectoryName(UserSettings.INSTANCE.getDefaultDatabaseDirectoryName());
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
        evt.setLogLevel(Level.parse(comboBoxLogLevel.getSelectedItem().toString()));
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
        comboBoxLogLevel.setSelectedItem(settings.getLogLevel().getLocalizedName());
        ComboBoxModelLogfileFormatter modelLogfileFormatter =
                (ComboBoxModelLogfileFormatter) comboBoxLogfileFormatterClass.getModel();
        modelLogfileFormatter.setSelectedItem(settings.getLogfileFormatterClass());
        checkBoxIsAcceptHiddenDirectories.setSelected(
                settings.isAcceptHiddenDirectories());
        checkBoxTreeDirectoriesSelectLastDirectory.setSelected(
                settings.isTreeDirectoriesSelectLastDirectory());
        labelDatabaseDirectory.setText(UserSettings.INSTANCE.getDatabaseDirectoryName());
    }

    private void readAutoCopyDirectoryProperties(UserSettings settings) {
        File lastAcDirectory = settings.getAutocopyDirectory();
        if (lastAcDirectory != null && lastAcDirectory.exists()) {
            String lastAcDirectoryName = lastAcDirectory.getAbsolutePath();
            labelAutocopyDirectory.setText(lastAcDirectoryName);
            lastSelectedAutocopyDirectory = lastAcDirectoryName;
            labelAutocopyDirectory.setIcon(IconUtil.getSystemIcon(lastAcDirectory));
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

        panelLogfile = new javax.swing.JPanel();
        labelLogLevel = new javax.swing.JLabel();
        comboBoxLogLevel = new javax.swing.JComboBox();
        labelLogLogfileFormatterClass = new javax.swing.JLabel();
        comboBoxLogfileFormatterClass = new javax.swing.JComboBox();
        panelAutoCopyDirectory = new javax.swing.JPanel();
        labelAutocopyDirectory = new javax.swing.JLabel();
        buttonChooseAutocopyDirectory = new javax.swing.JButton();
        panelWebBrowser = new javax.swing.JPanel();
        labelWebBrowser = new javax.swing.JLabel();
        buttonChooseWebBrowser = new javax.swing.JButton();
        panelDatabaseDirectory = new javax.swing.JPanel();
        labelDatabaseDirectory = new javax.swing.JLabel();
        buttonChooseDatabaseDirectory = new javax.swing.JButton();
        buttonSetStandardDatabaseDirectoryName = new javax.swing.JButton();
        panelFolderView = new javax.swing.JPanel();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        checkBoxTreeDirectoriesSelectLastDirectory = new javax.swing.JCheckBox();

        panelLogfile.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("SettingsMiscPanel.panelLogfile.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelLogLevel.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLogLevel.setText(Bundle.getString("SettingsMiscPanel.labelLogLevel.text")); // NOI18N

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });

        labelLogLogfileFormatterClass.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLogLogfileFormatterClass.setText(Bundle.getString("SettingsMiscPanel.labelLogLogfileFormatterClass.text")); // NOI18N

        comboBoxLogfileFormatterClass.setModel(new ComboBoxModelLogfileFormatter());
        comboBoxLogfileFormatterClass.setRenderer(new ListCellRendererLogfileFormatter());
        comboBoxLogfileFormatterClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogfileFormatterClassActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLogfileLayout = new javax.swing.GroupLayout(panelLogfile);
        panelLogfile.setLayout(panelLogfileLayout);
        panelLogfileLayout.setHorizontalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLogLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelLogLogfileFormatterClass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxLogfileFormatterClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(172, Short.MAX_VALUE))
        );
        panelLogfileLayout.setVerticalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLogLevel)
                    .addComponent(labelLogLogfileFormatterClass)
                    .addComponent(comboBoxLogfileFormatterClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelAutoCopyDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("SettingsMiscPanel.panelAutoCopyDirectory.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelAutocopyDirectory.setFont(new java.awt.Font("Dialog", 0, 10));
        labelAutocopyDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelAutocopyDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseAutocopyDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
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
                .addComponent(labelAutocopyDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseAutocopyDirectory)
                .addContainerGap())
        );
        panelAutoCopyDirectoryLayout.setVerticalGroup(
            panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoCopyDirectoryLayout.createSequentialGroup()
                .addGroup(panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonChooseAutocopyDirectory)
                    .addComponent(labelAutocopyDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        panelWebBrowser.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("SettingsMiscPanel.panelWebBrowser.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelWebBrowser.setFont(new java.awt.Font("Dialog", 0, 10));
        labelWebBrowser.setForeground(new java.awt.Color(0, 0, 255));
        labelWebBrowser.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseWebBrowser.setFont(new java.awt.Font("Dialog", 0, 12));
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
                .addComponent(labelWebBrowser, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseWebBrowser)
                .addContainerGap())
        );
        panelWebBrowserLayout.setVerticalGroup(
            panelWebBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelWebBrowserLayout.createSequentialGroup()
                .addGroup(panelWebBrowserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonChooseWebBrowser)
                    .addComponent(labelWebBrowser, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("SettingsMiscPanel.panelDatabaseDirectory.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelDatabaseDirectory.setFont(new java.awt.Font("Dialog", 0, 10));
        labelDatabaseDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseDatabaseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDatabaseDirectory.setMnemonic('w');
        buttonChooseDatabaseDirectory.setText(Bundle.getString("SettingsMiscPanel.buttonChooseDatabaseDirectory.text")); // NOI18N
        buttonChooseDatabaseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDatabaseDirectoryActionPerformed(evt);
            }
        });

        buttonSetStandardDatabaseDirectoryName.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonSetStandardDatabaseDirectoryName.setMnemonic('S');
        buttonSetStandardDatabaseDirectoryName.setText(Bundle.getString("SettingsMiscPanel.buttonSetStandardDatabaseDirectoryName.text")); // NOI18N
        buttonSetStandardDatabaseDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetStandardDatabaseDirectoryNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatabaseDirectoryLayout = new javax.swing.GroupLayout(panelDatabaseDirectory);
        panelDatabaseDirectory.setLayout(panelDatabaseDirectoryLayout);
        panelDatabaseDirectoryLayout.setHorizontalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSetStandardDatabaseDirectoryName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseDatabaseDirectory)
                .addContainerGap())
        );
        panelDatabaseDirectoryLayout.setVerticalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonChooseDatabaseDirectory)
                        .addComponent(buttonSetStandardDatabaseDirectoryName)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFolderView.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("SettingsMiscPanel.panelFolderView.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxIsAcceptHiddenDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsAcceptHiddenDirectories.setMnemonic('o');
        checkBoxIsAcceptHiddenDirectories.setText(Bundle.getString("SettingsMiscPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        checkBoxTreeDirectoriesSelectLastDirectory.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        checkBoxTreeDirectoriesSelectLastDirectory.setMnemonic('s');
        checkBoxTreeDirectoriesSelectLastDirectory.setText(Bundle.getString("SettingsMiscPanel.checkBoxTreeDirectoriesSelectLastDirectory.text")); // NOI18N
        checkBoxTreeDirectoriesSelectLastDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxTreeDirectoriesSelectLastDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFolderViewLayout = new javax.swing.GroupLayout(panelFolderView);
        panelFolderView.setLayout(panelFolderViewLayout);
        panelFolderViewLayout.setHorizontalGroup(
            panelFolderViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFolderViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFolderViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsAcceptHiddenDirectories)
                    .addComponent(checkBoxTreeDirectoriesSelectLastDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelFolderViewLayout.setVerticalGroup(
            panelFolderViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFolderViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxTreeDirectoriesSelectLastDirectory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelFolderView, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelLogfile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelAutoCopyDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelWebBrowser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelLogfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelAutoCopyDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelWebBrowser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFolderView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelLogLogfileFormatterClass;
    private javax.swing.JLabel labelWebBrowser;
    private javax.swing.JPanel panelAutoCopyDirectory;
    private javax.swing.JPanel panelDatabaseDirectory;
    private javax.swing.JPanel panelFolderView;
    private javax.swing.JPanel panelLogfile;
    private javax.swing.JPanel panelWebBrowser;
    // End of variables declaration//GEN-END:variables
}
