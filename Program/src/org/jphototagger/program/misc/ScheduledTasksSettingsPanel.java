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
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.tasks.ScheduledTasks;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ScheduledTasksSettingsPanel extends PanelExt implements Persistence, HelpPageProvider {

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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelTasksAutoscan = UiFactory.panel();
        labelAutoscanDirectoriesInfo = UiFactory.label();
        labelAutoscanDirectoriesPrompt = UiFactory.label();
        scrollPaneListAutoscanDirectories = UiFactory.scrollPane();
        listAutoscanDirectories = UiFactory.jxList();
        checkBoxIsAutoscanIncludeSubdirectories = UiFactory.checkBox();
        buttonRemoveAutoscanDirectories = UiFactory.button();
        buttonAddAutoscanDirectories = UiFactory.button();
        panelTime = UiFactory.panel();
        labelTasksMinutesToStartScheduledTasks = UiFactory.label();
        spinnerMinutesToStartScheduledTasks = UiFactory.spinner();
        buttonScheduledTasks = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.panelTasksAutoscan.border.title"))); // NOI18N
        panelTasksAutoscan.setName("panelTasksAutoscan"); // NOI18N
        panelTasksAutoscan.setLayout(new java.awt.GridBagLayout());

        labelAutoscanDirectoriesInfo.setText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.labelAutoscanDirectoriesInfo.text")); // NOI18N
        labelAutoscanDirectoriesInfo.setName("labelAutoscanDirectoriesInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelTasksAutoscan.add(labelAutoscanDirectoriesInfo, gridBagConstraints);

        labelAutoscanDirectoriesPrompt.setLabelFor(listAutoscanDirectories);
        labelAutoscanDirectoriesPrompt.setText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.labelAutoscanDirectoriesPrompt.text")); // NOI18N
        labelAutoscanDirectoriesPrompt.setName("labelAutoscanDirectoriesPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelTasksAutoscan.add(labelAutoscanDirectoriesPrompt, gridBagConstraints);

        scrollPaneListAutoscanDirectories.setName("scrollPaneListAutoscanDirectories"); // NOI18N

        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        listAutoscanDirectories.setCellRenderer(new org.jphototagger.lib.swing.FileSystemListCellRenderer(true));
        listAutoscanDirectories.setName("listAutoscanDirectories"); // NOI18N
        listAutoscanDirectories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listAutoscanDirectoriesValueChanged(evt);
            }
        });
        listAutoscanDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
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
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelTasksAutoscan.add(scrollPaneListAutoscanDirectories, gridBagConstraints);

        checkBoxIsAutoscanIncludeSubdirectories.setText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.checkBoxIsAutoscanIncludeSubdirectories.text")); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.setName("checkBoxIsAutoscanIncludeSubdirectories"); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.addActionListener(new java.awt.event.ActionListener() {
            @Override
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
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 0);
        panelTasksAutoscan.add(checkBoxIsAutoscanIncludeSubdirectories, gridBagConstraints);

        buttonRemoveAutoscanDirectories.setText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.buttonRemoveAutoscanDirectories.text")); // NOI18N
        buttonRemoveAutoscanDirectories.setEnabled(false);
        buttonRemoveAutoscanDirectories.setName("buttonRemoveAutoscanDirectories"); // NOI18N
        buttonRemoveAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 0);
        panelTasksAutoscan.add(buttonRemoveAutoscanDirectories, gridBagConstraints);

        buttonAddAutoscanDirectories.setText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.buttonAddAutoscanDirectories.text")); // NOI18N
        buttonAddAutoscanDirectories.setName("buttonAddAutoscanDirectories"); // NOI18N
        buttonAddAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelTasksAutoscan.add(buttonAddAutoscanDirectories, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panelTasksAutoscan, gridBagConstraints);

        panelTime.setName("panelTime"); // NOI18N
        panelTime.setLayout(new java.awt.GridBagLayout());

        labelTasksMinutesToStartScheduledTasks.setLabelFor(spinnerMinutesToStartScheduledTasks);
        labelTasksMinutesToStartScheduledTasks.setText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.labelTasksMinutesToStartScheduledTasks.text")); // NOI18N
        labelTasksMinutesToStartScheduledTasks.setName("labelTasksMinutesToStartScheduledTasks"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        panelTime.add(labelTasksMinutesToStartScheduledTasks, gridBagConstraints);

        spinnerMinutesToStartScheduledTasks.setModel(new SpinnerNumberModel(5, 1, 6000, 1));
        spinnerMinutesToStartScheduledTasks.setName("spinnerMinutesToStartScheduledTasks"); // NOI18N
        spinnerMinutesToStartScheduledTasks.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMinutesToStartScheduledTasksStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTime.add(spinnerMinutesToStartScheduledTasks, gridBagConstraints);

        buttonScheduledTasks.setIcon(org.jphototagger.resources.Icons.getIcon("icon_start.png"));
        buttonScheduledTasks.setToolTipText(Bundle.getString(getClass(), "ScheduledTasksSettingsPanel.buttonScheduledTasks.toolTipText")); // NOI18N
        buttonScheduledTasks.setAlignmentY(0.0F);
        buttonScheduledTasks.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonScheduledTasks.setMargin(UiFactory.insets(0, 0, 0, 0));
        buttonScheduledTasks.setName("buttonScheduledTasks"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTime.add(buttonScheduledTasks, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        add(panelTime, gridBagConstraints);
    }

    private void listAutoscanDirectoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {
        setEnabledButtonRemoveAutoscanDirectory();
    }

    private void listAutoscanDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {
        handleKeyEventListTasksAutoscanDirectories(evt);
    }

    private void checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(java.awt.event.ActionEvent evt) {
        handleActionCheckBoxIsAutoscanIncludeSubdirectories();
    }

    private void buttonRemoveAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {
        removeSelectedAutoscanDirectories();
    }

    private void buttonAddAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {
        addAutoscanDirectories();
    }

    private void spinnerMinutesToStartScheduledTasksStateChanged(javax.swing.event.ChangeEvent evt) {
        handleStateChangedSpinnerMinutesToStartScheduledTasks();
    }

    private javax.swing.JButton buttonAddAutoscanDirectories;
    private javax.swing.JButton buttonRemoveAutoscanDirectories;
    private javax.swing.JButton buttonScheduledTasks;
    private javax.swing.JCheckBox checkBoxIsAutoscanIncludeSubdirectories;
    private javax.swing.JLabel labelAutoscanDirectoriesInfo;
    private javax.swing.JLabel labelAutoscanDirectoriesPrompt;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private org.jdesktop.swingx.JXList listAutoscanDirectories;
    private javax.swing.JPanel panelTasksAutoscan;
    private javax.swing.JPanel panelTime;
    private javax.swing.JScrollPane scrollPaneListAutoscanDirectories;
    private javax.swing.JSpinner spinnerMinutesToStartScheduledTasks;
}
