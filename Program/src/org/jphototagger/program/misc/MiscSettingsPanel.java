package org.jphototagger.program.misc;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.lib.comparator.PositionComparatorAscendingOrder;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.program.app.update.UpdateCheckController;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesKeys;

/**
 * @author Elmar Baumann
 */
public final class MiscSettingsPanel extends javax.swing.JPanel implements Persistence {

    private static final long serialVersionUID = 1L;
    private static final String PREFERENCES_KEY_TABBED_PANE = "MiscSettingsPanel.TabbedPane";

    public MiscSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        lookupMiscOptionPages();
        restoreTabbedPaneSettings();
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    private File chooseDirectory(File startDirectory) {
        File dir = null;
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), startDirectory, showHiddenDirs);

        dlg.setStorageKey("MiscSettingsPanel.DirChooser");
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

    private void setAcceptHiddenDirectories() {
        setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.isSelected());
    }

    private void chooseRepositoryDirectory() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        File repositoryDirectory = provider.getFileRepositoryDirectory();

        File file = chooseDirectory(repositoryDirectory);

        if (file != null) {
            setRepositoryDirectoryName(file.getAbsolutePath());
        }
    }
    private void setAcceptHiddenDirectories(boolean accept) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES, accept);
    }

    private void setRepositoryDirectoryName(String directoryName) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        setIconRepositoryDirectory();
        labelRepositoryDirectory.setText(directoryName);
        setRepositoryDirectoryName(directoryName);
        storage.setString(FileRepositoryProvider.KEY_FILE_REPOSITORY_DIRECTORY, directoryName);
    }

    private void setIconRepositoryDirectory() {
        File dir = new File(labelRepositoryDirectory.getText());

        if (dir.isDirectory()) {
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(dir);

            labelRepositoryDirectory.setIcon(icon);
        }
    }

    private void setDefaultRepositoryDirectory() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);

        setRepositoryDirectoryName(provider.getDefaultFileRepositoryDirectory().getAbsolutePath());
    }

    private void setDisplaySearchButton() {
        setDisplaySearchButton(checkBoxDisplaySearchButton.isSelected());
    }

    private void setDisplaySearchButton(boolean display) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON, display);
    }

    private void setCopyMoveFiles() {
        boolean isConfirmOverwrite = radioButtonCopyMoveFileConfirmOverwrite.isSelected();
        boolean renameSourceFileIfTargetFileExists = radioButtonCopyMoveFileRenameIfExists.isSelected();
        CopyMoveFilesOptions options = isConfirmOverwrite
                          ? CopyMoveFilesOptions.CONFIRM_OVERWRITE
                          : renameSourceFileIfTargetFileExists
                            ? CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS
                            : CopyMoveFilesOptions.CONFIRM_OVERWRITE;

        setCopyMoveFilesOptions(options);
    }

    private void setCopyMoveFilesOptions(CopyMoveFilesOptions options) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setInt(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES, options.getInt());
    }

    private void setCheckForUpdates() {
        setCheckForUpdates(checkBoxCheckForUpdates.isSelected());
    }

    private void setCheckForUpdates(boolean auto) {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES, auto);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void applySettings(PreferencesChangedEvent evt) {
        if (AppPreferencesKeys.KEY_CHECK_FOR_UPDATES.equals(evt.getKey())) {
            checkBoxCheckForUpdates.setSelected((Boolean)evt.getNewValue());
        }
    }

    @Override
    public void restore() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);

        checkBoxCheckForUpdates.setSelected(isCheckForUpdates());
        checkBoxDisplaySearchButton.setSelected(isDisplaySearchButton());
        checkBoxIsAcceptHiddenDirectories.setSelected(isAcceptHiddenDirectories());
        labelRepositoryDirectory.setText(provider.getFileRepositoryDirectory().getAbsolutePath());
        radioButtonCopyMoveFileConfirmOverwrite.setSelected(getCopyMoveFilesOptions().equals(CopyMoveFilesOptions.CONFIRM_OVERWRITE));
        radioButtonCopyMoveFileRenameIfExists.setSelected(getCopyMoveFilesOptions().equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS));
        setIconRepositoryDirectory();
        restoreTabbedPaneSettings();
    }

    private void restoreTabbedPaneSettings() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.applyTabbedPaneSettings(PREFERENCES_KEY_TABBED_PANE, tabbedPane, null);
    }

    private boolean isCheckForUpdates() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES)
                ? storage.getBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES)
                : true;
    }

    private boolean isDisplaySearchButton() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                ? storage.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                : true;
    }

    private CopyMoveFilesOptions getCopyMoveFilesOptions() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES)
                ? CopyMoveFilesOptions.parseInteger(storage.getInt(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES))
                : CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    @Override
    public void persist() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);

        preferences.setTabbedPane(PREFERENCES_KEY_TABBED_PANE, tabbedPane, null);
    }

    private void checkDownload() {
        UpdateCheckController updateCheckController = ControllerFactory.INSTANCE.getController(UpdateCheckController.class);

        updateCheckController.actionPerformed(null);
    }

    private void lookupMiscOptionPages() {
        List<OptionPageProvider> providers =
                new ArrayList<OptionPageProvider>(Lookup.getDefault().lookupAll(OptionPageProvider.class));
        Collections.sort(providers, PositionComparatorAscendingOrder.INSTANCE);
        for (OptionPageProvider provider : providers) {
            if (provider.isMiscOptionPage()) {
                addTab(provider.getComponent(), provider.getTitle(), provider.getIcon());
            }
        }
    }

    private void addTab(Component component, String title, Icon icon) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        if (title == null) {
            throw new NullPointerException("title == null");
        }
        tabbedPane.add(title, component);
        if (icon != null) {
            tabbedPane.setIconAt(tabbedPane.indexOfComponent(component), icon);
        }
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

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        panelDefault = new javax.swing.JPanel();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        panelCheckForUpdates = new javax.swing.JPanel();
        checkBoxCheckForUpdates = new javax.swing.JCheckBox();
        buttonCheckForUpdates = new javax.swing.JButton();
        checkBoxDisplaySearchButton = new javax.swing.JCheckBox();
        panelCopyMoveFiles = new javax.swing.JPanel();
        radioButtonCopyMoveFileConfirmOverwrite = new javax.swing.JRadioButton();
        radioButtonCopyMoveFileRenameIfExists = new javax.swing.JRadioButton();
        panelRepositoryDirectory = new javax.swing.JPanel();
        labelInfoRepositoryDirectory = new javax.swing.JLabel();
        buttonSetDefaultRepositoryDirectoryName = new javax.swing.JButton();
        buttonChooseRepositoryDirectory = new javax.swing.JButton();
        labelRepositoryDirectory = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelDefault.setName("panelDefault"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/misc/Bundle"); // NOI18N
        checkBoxIsAcceptHiddenDirectories.setText(bundle.getString("MiscSettingsPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.setName("checkBoxIsAcceptHiddenDirectories"); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        panelCheckForUpdates.setName("panelCheckForUpdates"); // NOI18N
        panelCheckForUpdates.setLayout(new java.awt.GridBagLayout());

        checkBoxCheckForUpdates.setText(bundle.getString("MiscSettingsPanel.checkBoxCheckForUpdates.text")); // NOI18N
        checkBoxCheckForUpdates.setName("checkBoxCheckForUpdates"); // NOI18N
        checkBoxCheckForUpdates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCheckForUpdatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelCheckForUpdates.add(checkBoxCheckForUpdates, gridBagConstraints);

        buttonCheckForUpdates.setText(bundle.getString("MiscSettingsPanel.buttonCheckForUpdates.text")); // NOI18N
        buttonCheckForUpdates.setName("buttonCheckForUpdates"); // NOI18N
        buttonCheckForUpdates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckForUpdatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelCheckForUpdates.add(buttonCheckForUpdates, gridBagConstraints);

        checkBoxDisplaySearchButton.setText(bundle.getString("MiscSettingsPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.setName("checkBoxDisplaySearchButton"); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });

        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MiscSettingsPanel.panelCopyMoveFiles.border.title"))); // NOI18N
        panelCopyMoveFiles.setName("panelCopyMoveFiles"); // NOI18N

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(bundle.getString("MiscSettingsPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.setName("radioButtonCopyMoveFileConfirmOverwrite"); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(bundle.getString("MiscSettingsPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
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

        panelRepositoryDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MiscSettingsPanel.panelRepositoryDirectory.border.title"))); // NOI18N
        panelRepositoryDirectory.setName("panelRepositoryDirectory"); // NOI18N

        labelInfoRepositoryDirectory.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoRepositoryDirectory.setText(bundle.getString("MiscSettingsPanel.labelInfoRepositoryDirectory.text")); // NOI18N
        labelInfoRepositoryDirectory.setName("labelInfoRepositoryDirectory"); // NOI18N

        buttonSetDefaultRepositoryDirectoryName.setText(bundle.getString("MiscSettingsPanel.buttonSetDefaultRepositoryDirectoryName.text")); // NOI18N
        buttonSetDefaultRepositoryDirectoryName.setName("buttonSetDefaultRepositoryDirectoryName"); // NOI18N
        buttonSetDefaultRepositoryDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetDefaultRepositoryDirectoryNameActionPerformed(evt);
            }
        });

        buttonChooseRepositoryDirectory.setText(bundle.getString("MiscSettingsPanel.buttonChooseRepositoryDirectory.text")); // NOI18N
        buttonChooseRepositoryDirectory.setName("buttonChooseRepositoryDirectory"); // NOI18N
        buttonChooseRepositoryDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseRepositoryDirectoryActionPerformed(evt);
            }
        });

        labelRepositoryDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelRepositoryDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelRepositoryDirectory.setName("labelRepositoryDirectory"); // NOI18N

        javax.swing.GroupLayout panelRepositoryDirectoryLayout = new javax.swing.GroupLayout(panelRepositoryDirectory);
        panelRepositoryDirectory.setLayout(panelRepositoryDirectoryLayout);
        panelRepositoryDirectoryLayout.setHorizontalGroup(
            panelRepositoryDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRepositoryDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRepositoryDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelRepositoryDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                    .addGroup(panelRepositoryDirectoryLayout.createSequentialGroup()
                        .addComponent(labelInfoRepositoryDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetDefaultRepositoryDirectoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseRepositoryDirectory)))
                .addContainerGap())
        );
        panelRepositoryDirectoryLayout.setVerticalGroup(
            panelRepositoryDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRepositoryDirectoryLayout.createSequentialGroup()
                .addGroup(panelRepositoryDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoRepositoryDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSetDefaultRepositoryDirectoryName)
                    .addComponent(buttonChooseRepositoryDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelRepositoryDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDefaultLayout = new javax.swing.GroupLayout(panelDefault);
        panelDefault.setLayout(panelDefaultLayout);
        panelDefaultLayout.setHorizontalGroup(
            panelDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsAcceptHiddenDirectories)
                    .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelCheckForUpdates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelRepositoryDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDefaultLayout.setVerticalGroup(
            panelDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCheckForUpdates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRepositoryDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab(bundle.getString("MiscSettingsPanel.panelDefault.TabConstraints.tabTitle"), panelDefault); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addContainerGap())
        );
    }//GEN-END:initComponents

    private void checkBoxCheckForUpdatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxCheckForUpdatesActionPerformed
        setCheckForUpdates();
    }//GEN-LAST:event_checkBoxCheckForUpdatesActionPerformed

    private void radioButtonCopyMoveFileRenameIfExistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCopyMoveFileRenameIfExistsActionPerformed
        setCopyMoveFiles();
    }//GEN-LAST:event_radioButtonCopyMoveFileRenameIfExistsActionPerformed

    private void radioButtonCopyMoveFileConfirmOverwriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCopyMoveFileConfirmOverwriteActionPerformed
        setCopyMoveFiles();
    }//GEN-LAST:event_radioButtonCopyMoveFileConfirmOverwriteActionPerformed

    private void checkBoxIsAcceptHiddenDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed
        setAcceptHiddenDirectories();
    }//GEN-LAST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed

    private void buttonChooseRepositoryDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseRepositoryDirectoryActionPerformed
        chooseRepositoryDirectory();
    }//GEN-LAST:event_buttonChooseRepositoryDirectoryActionPerformed

    private void buttonSetDefaultRepositoryDirectoryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetDefaultRepositoryDirectoryNameActionPerformed
        setDefaultRepositoryDirectory();
    }//GEN-LAST:event_buttonSetDefaultRepositoryDirectoryNameActionPerformed

    private void checkBoxDisplaySearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplaySearchButtonActionPerformed
        setDisplaySearchButton();
    }//GEN-LAST:event_checkBoxDisplaySearchButtonActionPerformed

    private void buttonCheckForUpdatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCheckForUpdatesActionPerformed
        checkDownload();
    }//GEN-LAST:event_buttonCheckForUpdatesActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCheckForUpdates;
    private javax.swing.JButton buttonChooseRepositoryDirectory;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetDefaultRepositoryDirectoryName;
    private javax.swing.JCheckBox checkBoxCheckForUpdates;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JLabel labelInfoRepositoryDirectory;
    private javax.swing.JLabel labelRepositoryDirectory;
    private javax.swing.JPanel panelCheckForUpdates;
    private javax.swing.JPanel panelCopyMoveFiles;
    private javax.swing.JPanel panelDefault;
    private javax.swing.JPanel panelRepositoryDirectory;
    private javax.swing.JRadioButton radioButtonCopyMoveFileConfirmOverwrite;
    private javax.swing.JRadioButton radioButtonCopyMoveFileRenameIfExists;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
