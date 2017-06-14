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
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.preferences.PreferencesKeys;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.lib.api.LayerUtil;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.wordsets.WordsetPreferences;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesDefaults;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.xmp.XmpPreferences;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class MiscSettingsPanel extends javax.swing.JPanel implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private static final String PREFERENCES_KEY_TABBED_PANE = "MiscSettingsPanel.TabbedPane";
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private boolean listenToUseLongXmpSidecarFileNames;

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
        dlg.setPreferencesKey("MiscSettingsPanel.DirChooser");
        dlg.setVisible(true);
        ComponentUtil.parentWindowToFront(this);
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
        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void setAcceptHiddenDirectories() {
        setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.isSelected());
    }

    private void setAcceptHiddenDirectories(boolean accept) {
        prefs.setBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES, accept);
    }

    private boolean isDeleteDirectoriesEnabled() {
        return prefs.containsKey(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                : true;
    }

    private void setDeleteDirectoriesEnabled() {
        prefs.setBoolean(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES, checkBoxEnableDeleteDirectories.isSelected());
    }

    private void chooseRepositoryDirectory() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        File repositoryDirectory = provider.getFileRepositoryDirectory();
        File file = chooseDirectory(repositoryDirectory);
        if (file != null) {
            setRepositoryDirectoryName(file.getAbsolutePath());
            displayRepositoryDirectoryInfo();
        }
    }

    private void setRepositoryDirectoryName(String directoryName) {
        setIconRepositoryDirectory();
        labelRepositoryDirectory.setText(directoryName);
        prefs.setString(FileRepositoryProvider.KEY_FILE_REPOSITORY_DIRECTORY, directoryName);
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

    private void displayRepositoryDirectoryInfo() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        String repositoryFileName = provider.getFileRepositoryFileName(FilenameTokens.FULL_PATH);
        String repositoryPattern = FileUtil.getAbsolutePathnamePrefix(repositoryFileName);
        String message = Bundle.getString(MiscSettingsPanel.class, "MiscSettingsPanel.RepositoryDirectoryInfo.Text", repositoryPattern);
        MessageDisplayer.information(this, message);
    }

    private void setDisplaySearchButton() {
        setDisplaySearchButton(checkBoxDisplaySearchButton.isSelected());
    }

    private void setDisplaySearchButton(boolean display) {
        prefs.setBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON, display);
    }

    private void setDisplayWordsetsEditPanel() {
        boolean display = checkBoxDisplayWordsetsEditPanel.isSelected();
        prefs.setBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_WORD_SETS_EDIT_PANEL, display);
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
        prefs.setInt(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES, options.getInt());
    }

    private void setCheckForUpdates() {
        setCheckForUpdates(checkBoxCheckForUpdates.isSelected());
    }

    private void setCheckForUpdates(boolean auto) {
        prefs.setBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES, auto);
    }

    private void setUseLongXmpSidecarFileNames() {
        if (!listenToUseLongXmpSidecarFileNames) {
            return;
        }
        boolean useLongSidecarFileNames = checkBoxUseLongXmpSidecarFileNames.isSelected();
        String message = useLongSidecarFileNames
                ? Bundle.getString(MiscSettingsPanel.class, "MiscSettingsPanel.Confirm.UseLongXmpSidecarFileNames")
                : Bundle.getString(MiscSettingsPanel.class, "MiscSettingsPanel.Confirm.UseDefaultXmpSidecarFileNames");
        if (MessageDisplayer.confirmYesNo(this, message)) {
            prefs.setBoolean(XmpPreferences.KEY_USE_LONG_SIDECAR_FILENAMES, useLongSidecarFileNames);
            SerialTaskExecutor executor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
            RenameLongXmpSidecarFilenames task = new RenameLongXmpSidecarFilenames(useLongSidecarFileNames);
            executor.addTask(task);
        } else {
            listenToUseLongXmpSidecarFileNames = false;
            checkBoxUseLongXmpSidecarFileNames.setSelected(!useLongSidecarFileNames);
            listenToUseLongXmpSidecarFileNames = true;
        }
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void applySettings(PreferencesChangedEvent evt) {
        if (AppPreferencesKeys.KEY_CHECK_FOR_UPDATES.equals(evt.getKey())) {
            checkBoxCheckForUpdates.setSelected((Boolean) evt.getNewValue());
        }
    }

    @Override
    public void restore() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        checkBoxCheckForUpdates.setSelected(isCheckForUpdates());
        checkBoxDisplaySearchButton.setSelected(isDisplaySearchButton());
        checkBoxIsAcceptHiddenDirectories.setSelected(isAcceptHiddenDirectories());
        checkBoxEnableDeleteDirectories.setSelected(isDeleteDirectoriesEnabled());
        checkBoxDisplayWordsetsEditPanel.setSelected(WordsetPreferences.isDisplayWordsetsEditPanel());
        labelRepositoryDirectory.setText(provider.getFileRepositoryDirectory().getAbsolutePath());
        radioButtonCopyMoveFileConfirmOverwrite.setSelected(getCopyMoveFilesOptions().equals(CopyMoveFilesOptions.CONFIRM_OVERWRITE));
        radioButtonCopyMoveFileRenameIfExists.setSelected(getCopyMoveFilesOptions().equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS));
        setIconRepositoryDirectory();
        restoreUseLongXmpSidecarFileNames();
        restoreTabbedPaneSettings();
        restoreMetaDataTextAreasColumns();
    }

    private void restoreTabbedPaneSettings() {
        if (prefs != null) {
            prefs.applyTabbedPaneSettings(PREFERENCES_KEY_TABBED_PANE, tabbedPane, null);
        }
    }

    private void persistMdTextAreasColumns() {
        int value = (Integer) spinnerMdTextAreasColumns.getModel().getValue();
        if (value >= AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MINIMUM && value <= AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MAXIMUM) {
            prefs.setInt(AppPreferencesKeys.KEY_UI_COLUMNS_MD_TEXT_AREAS, value);
        }
    }

    private void restoreMetaDataTextAreasColumns() {
        if (prefs.containsKey(AppPreferencesKeys.KEY_UI_COLUMNS_MD_TEXT_AREAS)) {
            int value = prefs.getInt(AppPreferencesKeys.KEY_UI_COLUMNS_MD_TEXT_AREAS);
            if (value >= AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MINIMUM && value <= AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MAXIMUM) {
                spinnerMdTextAreasColumns.getModel().setValue(value);
            }
        }
    }

    private void restoreUseLongXmpSidecarFileNames() {
        listenToUseLongXmpSidecarFileNames = false;
        if (prefs != null && prefs.containsKey(XmpPreferences.KEY_USE_LONG_SIDECAR_FILENAMES)) {
            checkBoxUseLongXmpSidecarFileNames.setSelected(
                    prefs.getBoolean(XmpPreferences.KEY_USE_LONG_SIDECAR_FILENAMES));
        }
        listenToUseLongXmpSidecarFileNames = true;
    }

    private boolean isCheckForUpdates() {
        return prefs.containsKey(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_CHECK_FOR_UPDATES)
                : true;
    }

    private boolean isDisplaySearchButton() {
        return prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_SEARCH_BUTTON)
                : true;
    }

    private CopyMoveFilesOptions getCopyMoveFilesOptions() {
        return prefs.containsKey(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES)
                ? CopyMoveFilesOptions.parseInteger(prefs.getInt(AppPreferencesKeys.KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES))
                : CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    @Override
    public void persist() {
        prefs.setTabbedPane(PREFERENCES_KEY_TABBED_PANE, tabbedPane, null);
    }

    private void lookupMiscOptionPages() {
        List<OptionPageProvider> providers =
                new ArrayList<OptionPageProvider>(Lookup.getDefault().lookupAll(OptionPageProvider.class));
        Collections.sort(providers, PositionProviderAscendingComparator.INSTANCE);
        LayerUtil.logWarningIfNotUniquePositions(providers);
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

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(MiscSettingsPanel.class, "MiscSettingsPanel.HelpPage");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        panelDefault = new javax.swing.JPanel();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        checkBoxEnableDeleteDirectories = new javax.swing.JCheckBox();
        panelCheckForUpdates = new javax.swing.JPanel();
        checkBoxCheckForUpdates = new javax.swing.JCheckBox();
        checkBoxDisplaySearchButton = new javax.swing.JCheckBox();
        checkBoxUseLongXmpSidecarFileNames = new javax.swing.JCheckBox();
        panelEditMetadata = new javax.swing.JPanel();
        panelMdTextAreasColumns = new javax.swing.JPanel();
        labelMdTextAreasColumnsPropmpt = new javax.swing.JLabel();
        spinnerMdTextAreasColumns = new javax.swing.JSpinner();
        labelMdTextAreasColumnsInfo = new javax.swing.JLabel();
        checkBoxDisplayWordsetsEditPanel = new javax.swing.JCheckBox();
        panelCopyMoveFiles = new javax.swing.JPanel();
        radioButtonCopyMoveFileConfirmOverwrite = new javax.swing.JRadioButton();
        radioButtonCopyMoveFileRenameIfExists = new javax.swing.JRadioButton();
        panelRepositoryDirectory = new javax.swing.JPanel();
        labelInfoRepositoryDirectory = new javax.swing.JLabel();
        panelButtonsRepositoryDirectory = new javax.swing.JPanel();
        buttonChooseRepositoryDirectory = new javax.swing.JButton();
        buttonSetDefaultRepositoryDirectoryName = new javax.swing.JButton();
        labelRepositoryDirectory = new javax.swing.JLabel();
        panelFill = new javax.swing.JPanel();

        panelDefault.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/misc/Bundle"); // NOI18N
        checkBoxIsAcceptHiddenDirectories.setText(bundle.getString("MiscSettingsPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelDefault.add(checkBoxIsAcceptHiddenDirectories, gridBagConstraints);

        checkBoxEnableDeleteDirectories.setText(bundle.getString("MiscSettingsPanel.checkBoxEnableDeleteDirectories.text")); // NOI18N
        checkBoxEnableDeleteDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxEnableDeleteDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelDefault.add(checkBoxEnableDeleteDirectories, gridBagConstraints);

        panelCheckForUpdates.setLayout(new java.awt.GridBagLayout());

        checkBoxCheckForUpdates.setText(bundle.getString("MiscSettingsPanel.checkBoxCheckForUpdates.text")); // NOI18N
        checkBoxCheckForUpdates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCheckForUpdatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelCheckForUpdates.add(checkBoxCheckForUpdates, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelDefault.add(panelCheckForUpdates, gridBagConstraints);

        checkBoxDisplaySearchButton.setText(bundle.getString("MiscSettingsPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelDefault.add(checkBoxDisplaySearchButton, gridBagConstraints);

        checkBoxUseLongXmpSidecarFileNames.setText(bundle.getString("MiscSettingsPanel.checkBoxUseLongXmpSidecarFileNames.text")); // NOI18N
        checkBoxUseLongXmpSidecarFileNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUseLongXmpSidecarFileNamesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelDefault.add(checkBoxUseLongXmpSidecarFileNames, gridBagConstraints);

        panelEditMetadata.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MiscSettingsPanel.panelEditMetadata.border.title"))); // NOI18N
        panelEditMetadata.setLayout(new java.awt.GridBagLayout());

        panelMdTextAreasColumns.setLayout(new java.awt.GridBagLayout());

        labelMdTextAreasColumnsPropmpt.setText(bundle.getString("MiscSettingsPanel.labelMdTextAreasColumnsPropmpt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelMdTextAreasColumns.add(labelMdTextAreasColumnsPropmpt, gridBagConstraints);

        spinnerMdTextAreasColumns.setModel(new javax.swing.SpinnerNumberModel(AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_DEFAULT, AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MINIMUM, AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MAXIMUM, 5));
        spinnerMdTextAreasColumns.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMdTextAreasColumnsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelMdTextAreasColumns.add(spinnerMdTextAreasColumns, gridBagConstraints);

        labelMdTextAreasColumnsInfo.setText(bundle.getString("MiscSettingsPanel.labelMdTextAreasColumnsInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelMdTextAreasColumns.add(labelMdTextAreasColumnsInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelEditMetadata.add(panelMdTextAreasColumns, gridBagConstraints);

        checkBoxDisplayWordsetsEditPanel.setText(bundle.getString("MiscSettingsPanel.checkBoxDisplayWordsetsEditPanel.text")); // NOI18N
        checkBoxDisplayWordsetsEditPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplayWordsetsEditPanelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelEditMetadata.add(checkBoxDisplayWordsetsEditPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        panelDefault.add(panelEditMetadata, gridBagConstraints);

        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MiscSettingsPanel.panelCopyMoveFiles.border.title"))); // NOI18N
        panelCopyMoveFiles.setLayout(new java.awt.GridBagLayout());

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(bundle.getString("MiscSettingsPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite, gridBagConstraints);

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(bundle.getString("MiscSettingsPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
        radioButtonCopyMoveFileRenameIfExists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileRenameIfExistsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        panelCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        panelDefault.add(panelCopyMoveFiles, gridBagConstraints);

        panelRepositoryDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MiscSettingsPanel.panelRepositoryDirectory.border.title"))); // NOI18N
        panelRepositoryDirectory.setLayout(new java.awt.GridBagLayout());

        labelInfoRepositoryDirectory.setText(bundle.getString("MiscSettingsPanel.labelInfoRepositoryDirectory.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelRepositoryDirectory.add(labelInfoRepositoryDirectory, gridBagConstraints);

        panelButtonsRepositoryDirectory.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        buttonChooseRepositoryDirectory.setText(bundle.getString("MiscSettingsPanel.buttonChooseRepositoryDirectory.text")); // NOI18N
        buttonChooseRepositoryDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseRepositoryDirectoryActionPerformed(evt);
            }
        });
        panelButtonsRepositoryDirectory.add(buttonChooseRepositoryDirectory);

        buttonSetDefaultRepositoryDirectoryName.setText(bundle.getString("MiscSettingsPanel.buttonSetDefaultRepositoryDirectoryName.text")); // NOI18N
        buttonSetDefaultRepositoryDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetDefaultRepositoryDirectoryNameActionPerformed(evt);
            }
        });
        panelButtonsRepositoryDirectory.add(buttonSetDefaultRepositoryDirectoryName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelRepositoryDirectory.add(panelButtonsRepositoryDirectory, gridBagConstraints);

        labelRepositoryDirectory.setText(" "); // NOI18N
        labelRepositoryDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelRepositoryDirectory.add(labelRepositoryDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        panelDefault.add(panelRepositoryDirectory, gridBagConstraints);

        javax.swing.GroupLayout panelFillLayout = new javax.swing.GroupLayout(panelFill);
        panelFill.setLayout(panelFillLayout);
        panelFillLayout.setHorizontalGroup(
            panelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFillLayout.setVerticalGroup(
            panelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panelDefault.add(panelFill, gridBagConstraints);

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
                .addComponent(tabbedPane)
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
        displayRepositoryDirectoryInfo();
    }//GEN-LAST:event_buttonSetDefaultRepositoryDirectoryNameActionPerformed

    private void checkBoxDisplaySearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplaySearchButtonActionPerformed
        setDisplaySearchButton();
    }//GEN-LAST:event_checkBoxDisplaySearchButtonActionPerformed

    private void checkBoxDisplayWordsetsEditPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplayWordsetsEditPanelActionPerformed
        setDisplayWordsetsEditPanel();
    }//GEN-LAST:event_checkBoxDisplayWordsetsEditPanelActionPerformed

    private void checkBoxUseLongXmpSidecarFileNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxUseLongXmpSidecarFileNamesActionPerformed
        setUseLongXmpSidecarFileNames();
    }//GEN-LAST:event_checkBoxUseLongXmpSidecarFileNamesActionPerformed

    private void spinnerMdTextAreasColumnsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMdTextAreasColumnsStateChanged
        persistMdTextAreasColumns();
    }//GEN-LAST:event_spinnerMdTextAreasColumnsStateChanged

    private void checkBoxEnableDeleteDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxEnableDeleteDirectoriesActionPerformed
        setDeleteDirectoriesEnabled();
    }//GEN-LAST:event_checkBoxEnableDeleteDirectoriesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseRepositoryDirectory;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetDefaultRepositoryDirectoryName;
    private javax.swing.JCheckBox checkBoxCheckForUpdates;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxDisplayWordsetsEditPanel;
    private javax.swing.JCheckBox checkBoxEnableDeleteDirectories;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JCheckBox checkBoxUseLongXmpSidecarFileNames;
    private javax.swing.JLabel labelInfoRepositoryDirectory;
    private javax.swing.JLabel labelMdTextAreasColumnsInfo;
    private javax.swing.JLabel labelMdTextAreasColumnsPropmpt;
    private javax.swing.JLabel labelRepositoryDirectory;
    private javax.swing.JPanel panelButtonsRepositoryDirectory;
    private javax.swing.JPanel panelCheckForUpdates;
    private javax.swing.JPanel panelCopyMoveFiles;
    private javax.swing.JPanel panelDefault;
    private javax.swing.JPanel panelEditMetadata;
    private javax.swing.JPanel panelFill;
    private javax.swing.JPanel panelMdTextAreasColumns;
    private javax.swing.JPanel panelRepositoryDirectory;
    private javax.swing.JRadioButton radioButtonCopyMoveFileConfirmOverwrite;
    private javax.swing.JRadioButton radioButtonCopyMoveFileRenameIfExists;
    private javax.swing.JSpinner spinnerMdTextAreasColumns;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
