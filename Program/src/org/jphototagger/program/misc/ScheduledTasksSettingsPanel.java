package org.jphototagger.program.misc;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.repository.AutoscanDirectoriesRepository;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.tasks.ScheduledTasks;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ScheduledTasksSettingsPanel extends javax.swing.JPanel implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private static final String KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY = "UserSettingsDialog.keyLastSelectedAutoscanDirectory";
    private AutoscanDirectoriesListModel modelAutoscanDirectories = new AutoscanDirectoriesListModel();
    private String lastSelectedAutoscanDirectory = "";
    private final AutoscanDirectoriesRepository repo = Lookup.getDefault().lookup(AutoscanDirectoriesRepository.class);

    public ScheduledTasksSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        modelAutoscanDirectories = new AutoscanDirectoriesListModel();
        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        MnemonicUtil.setMnemonics((Container) this);
        setEnabled();
    }

    public void setEnabled() {
        buttonRemoveAutoscanDirectories.setEnabled(listAutoscanDirectories.getSelectedIndex() >= 0);
        setEnabledCheckBoxSubdirs();
    }

    @Override
    public void restore() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        spinnerMinutesToStartScheduledTasks.setValue(ScheduledTasks.getMinutesToStartScheduledTasks());
        checkBoxIsAutoscanIncludeSubdirectories.setSelected(isAutoscanIncludeSubdirectories());
        lastSelectedAutoscanDirectory = prefs.getString(KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY);
        setEnabledCheckBoxSubdirs();

    }

    private boolean isAutoscanIncludeSubdirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(AppPreferencesKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
                : true;
    }

    private void setEnabledCheckBoxSubdirs() {
        boolean hasDirs = listAutoscanDirectories.getModel().getSize() > 0;
        checkBoxIsAutoscanIncludeSubdirectories.setEnabled(hasDirs);
    }

    @Override
    public void persist() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setString(KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY, lastSelectedAutoscanDirectory);
    }

    private void addAutoscanDirectories() {
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(DomainPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), new File(lastSelectedAutoscanDirectory), hideRootFiles, showHiddenDirs);

        dlg.setPreferencesKey("ScheduledTasksSettingsPanel.DirChooser");
        dlg.setVisible(true);
        ComponentUtil.parentWindowToFront(this);

        if (dlg.isAccepted()) {
            List<File> directories = dlg.getSelectedDirectories();

            for (File directory : directories) {
                if (!modelAutoscanDirectories.contains(directory)) {
                    lastSelectedAutoscanDirectory = directory.getAbsolutePath();

                    if (!repo.existsAutoscanDirectory(directory)) {
                        if (!repo.saveAutoscanDirectory(directory)) {
                            errorMessageInsertAutoscanDirectory(directory);
                        }
                    }
                }
            }
        }

        setEnabled();
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void removeSelectedAutoscanDirectories() {
        List<?> values = listAutoscanDirectories.getSelectedValuesList();
        for (int i = 0; i < values.size(); i++) {
            File   directory     = (File) values.get(i);
            if (repo.existsAutoscanDirectory(directory)) {
                if (!repo.deleteAutoscanDirectory(directory)) {
                    errorMessageDeleteAutoscanDirectory(directory);
                }
            }
        }
        setEnabled();
    }

    private void errorMessageInsertAutoscanDirectory(File directory) {
        String message = Bundle.getString(ScheduledTasksSettingsPanel.class, "ScheduledTasksSettingsPanel.Error.InsertAutoscanDirectory", directory);
        MessageDisplayer.error(this, message);
    }

    private void errorMessageDeleteAutoscanDirectory(File directory) {
        String message = Bundle.getString(ScheduledTasksSettingsPanel.class, "ScheduledTasksSettingsPanel.Error.DeleteAutoscanDirectory", directory);
        MessageDisplayer.error(this, message);
    }

    private void handleKeyEventListTasksAutoscanDirectories(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedAutoscanDirectories();
        }
    }

    private void handleStateChangedSpinnerMinutesToStartScheduledTasks() {
        setMinutesToStartScheduledTasks((Integer) spinnerMinutesToStartScheduledTasks.getValue());
    }

    private void setMinutesToStartScheduledTasks(int minutes) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setString(AppPreferencesKeys.KEY_SCHEDULED_TASKS_MINUTES_TO_START_SCHEDULED_TASKS, Integer.toString(minutes));
    }

    private void handleActionCheckBoxIsAutoscanIncludeSubdirectories() {
        setAutoscanIncludeSubdirectories(checkBoxIsAutoscanIncludeSubdirectories.isSelected());
    }

    private void setAutoscanIncludeSubdirectories(boolean include) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(AppPreferencesKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES, include);
    }

    private void setEnabledButtonRemoveAutoscanDirectory() {
        buttonRemoveAutoscanDirectories.setEnabled(listAutoscanDirectories.getSelectedIndices().length > 0);
    }

    public JButton getButtonScheduledTasks() {
        return buttonScheduledTasks;
    }

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(ScheduledTasksSettingsPanel.class, "ScheduledTasksSettingsPanel.HelpPage");
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelTasksAutoscan = new javax.swing.JPanel();
        labelAutoscanDirectoriesInfo = new javax.swing.JLabel();
        labelAutoscanDirectoriesPrompt = new javax.swing.JLabel();
        scrollPaneListAutoscanDirectories = new javax.swing.JScrollPane();
        listAutoscanDirectories = new org.jdesktop.swingx.JXList();
        checkBoxIsAutoscanIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonRemoveAutoscanDirectories = new javax.swing.JButton();
        buttonAddAutoscanDirectories = new javax.swing.JButton();
        buttonScheduledTasks = new javax.swing.JButton();
        labelTasksMinutesToStartScheduledTasks = new javax.swing.JLabel();
        spinnerMinutesToStartScheduledTasks = new javax.swing.JSpinner();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/misc/Bundle"); // NOI18N
        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ScheduledTasksSettingsPanel.panelTasksAutoscan.border.title"))); // NOI18N
        panelTasksAutoscan.setName("panelTasksAutoscan"); // NOI18N
        panelTasksAutoscan.setLayout(new java.awt.GridBagLayout());

        labelAutoscanDirectoriesInfo.setText(bundle.getString("ScheduledTasksSettingsPanel.labelAutoscanDirectoriesInfo.text")); // NOI18N
        labelAutoscanDirectoriesInfo.setName("labelAutoscanDirectoriesInfo"); // NOI18N
        labelAutoscanDirectoriesInfo.setPreferredSize(new java.awt.Dimension(978, 48));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelTasksAutoscan.add(labelAutoscanDirectoriesInfo, gridBagConstraints);

        labelAutoscanDirectoriesPrompt.setLabelFor(listAutoscanDirectories);
        labelAutoscanDirectoriesPrompt.setText(bundle.getString("ScheduledTasksSettingsPanel.labelAutoscanDirectoriesPrompt.text")); // NOI18N
        labelAutoscanDirectoriesPrompt.setName("labelAutoscanDirectoriesPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelTasksAutoscan.add(labelAutoscanDirectoriesPrompt, gridBagConstraints);

        scrollPaneListAutoscanDirectories.setName("scrollPaneListAutoscanDirectories"); // NOI18N

        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        listAutoscanDirectories.setCellRenderer(new org.jphototagger.lib.swing.FileSystemListCellRenderer(true));
        listAutoscanDirectories.setName("listAutoscanDirectories"); // NOI18N
        listAutoscanDirectories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listAutoscanDirectoriesValueChanged(evt);
            }
        });
        listAutoscanDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listAutoscanDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneListAutoscanDirectories.setViewportView(listAutoscanDirectories);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelTasksAutoscan.add(scrollPaneListAutoscanDirectories, gridBagConstraints);

        checkBoxIsAutoscanIncludeSubdirectories.setText(bundle.getString("ScheduledTasksSettingsPanel.checkBoxIsAutoscanIncludeSubdirectories.text")); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.setName("checkBoxIsAutoscanIncludeSubdirectories"); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelTasksAutoscan.add(checkBoxIsAutoscanIncludeSubdirectories, gridBagConstraints);

        buttonRemoveAutoscanDirectories.setText(bundle.getString("ScheduledTasksSettingsPanel.buttonRemoveAutoscanDirectories.text")); // NOI18N
        buttonRemoveAutoscanDirectories.setEnabled(false);
        buttonRemoveAutoscanDirectories.setName("buttonRemoveAutoscanDirectories"); // NOI18N
        buttonRemoveAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelTasksAutoscan.add(buttonRemoveAutoscanDirectories, gridBagConstraints);

        buttonAddAutoscanDirectories.setText(bundle.getString("ScheduledTasksSettingsPanel.buttonAddAutoscanDirectories.text")); // NOI18N
        buttonAddAutoscanDirectories.setName("buttonAddAutoscanDirectories"); // NOI18N
        buttonAddAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelTasksAutoscan.add(buttonAddAutoscanDirectories, gridBagConstraints);

        buttonScheduledTasks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_start.png"))); // NOI18N
        buttonScheduledTasks.setToolTipText(bundle.getString("ScheduledTasksSettingsPanel.buttonScheduledTasks.toolTipText")); // NOI18N
        buttonScheduledTasks.setAlignmentY(0.0F);
        buttonScheduledTasks.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonScheduledTasks.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonScheduledTasks.setName("buttonScheduledTasks"); // NOI18N

        labelTasksMinutesToStartScheduledTasks.setLabelFor(spinnerMinutesToStartScheduledTasks);
        labelTasksMinutesToStartScheduledTasks.setText(bundle.getString("ScheduledTasksSettingsPanel.labelTasksMinutesToStartScheduledTasks.text")); // NOI18N
        labelTasksMinutesToStartScheduledTasks.setName("labelTasksMinutesToStartScheduledTasks"); // NOI18N

        spinnerMinutesToStartScheduledTasks.setModel(new SpinnerNumberModel(5, 1, 6000, 1));
        spinnerMinutesToStartScheduledTasks.setName("spinnerMinutesToStartScheduledTasks"); // NOI18N
        spinnerMinutesToStartScheduledTasks.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMinutesToStartScheduledTasksStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelTasksMinutesToStartScheduledTasks)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonScheduledTasks)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTasksMinutesToStartScheduledTasks)
                    .addComponent(buttonScheduledTasks))
                .addContainerGap())
        );
    }//GEN-END:initComponents

    private void listAutoscanDirectoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listAutoscanDirectoriesValueChanged
        setEnabledButtonRemoveAutoscanDirectory();
    }//GEN-LAST:event_listAutoscanDirectoriesValueChanged

    private void listAutoscanDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listAutoscanDirectoriesKeyReleased
        handleKeyEventListTasksAutoscanDirectories(evt);
    }//GEN-LAST:event_listAutoscanDirectoriesKeyReleased

    private void checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed
        handleActionCheckBoxIsAutoscanIncludeSubdirectories();
    }//GEN-LAST:event_checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed

    private void buttonRemoveAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveAutoscanDirectoriesActionPerformed
        removeSelectedAutoscanDirectories();
    }//GEN-LAST:event_buttonRemoveAutoscanDirectoriesActionPerformed

    private void buttonAddAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddAutoscanDirectoriesActionPerformed
        addAutoscanDirectories();
    }//GEN-LAST:event_buttonAddAutoscanDirectoriesActionPerformed

    private void spinnerMinutesToStartScheduledTasksStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMinutesToStartScheduledTasksStateChanged
        handleStateChangedSpinnerMinutesToStartScheduledTasks();
    }//GEN-LAST:event_spinnerMinutesToStartScheduledTasksStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAutoscanDirectories;
    private javax.swing.JButton buttonRemoveAutoscanDirectories;
    private javax.swing.JButton buttonScheduledTasks;
    private javax.swing.JCheckBox checkBoxIsAutoscanIncludeSubdirectories;
    private javax.swing.JLabel labelAutoscanDirectoriesInfo;
    private javax.swing.JLabel labelAutoscanDirectoriesPrompt;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private org.jdesktop.swingx.JXList listAutoscanDirectories;
    private javax.swing.JPanel panelTasksAutoscan;
    private javax.swing.JScrollPane scrollPaneListAutoscanDirectories;
    private javax.swing.JSpinner spinnerMinutesToStartScheduledTasks;
    // End of variables declaration//GEN-END:variables
}
