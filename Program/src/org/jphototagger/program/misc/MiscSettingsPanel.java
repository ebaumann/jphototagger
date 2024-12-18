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
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.PreferencesUtil;
import org.jphototagger.program.module.wordsets.WordsetPreferences;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.AppPreferencesDefaults;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.resources.UiFactory;
import org.jphototagger.xmp.XmpPreferences;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class MiscSettingsPanel extends PanelExt implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private static final String PREFERENCES_KEY_TABBED_PANE = "MiscSettingsPanel.TabbedPane";
    private transient final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
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

    private void setForceCreateXmpSidecarFiles() {
        prefs.setBoolean(XmpPreferences.KEY_FORCE_CREATE_XMP_SIDECARFILES, checkBoxForceCreateXmpSidecarFiles.isSelected());
    }

    private void setLockFileWhenWritingXmp() {
        prefs.setBoolean(XmpPreferences.KEY_LOCK_FILE_WHEN_WRITING_XMP, checkBoxLockFileWhenWritingXmp.isSelected());
    }

    private void setWriteExifDateToXmpDateCreated() {
        prefs.setBoolean(PreferencesKeys.KEY_DISABLE_SAVE_EXIF_TO_XMP_DATE_CREATED, !checkBoxWriteExifDateToXmpDateCreated.isSelected());
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
        restoreForceCreateXmpSidecarFiles();
        restoreLockFileWhenWritingXmp();
        restoreWriteExifDateToXmpDateCreated();
        restoreInputHelperDialogAlwaysOnTop();
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

    private void restoreForceCreateXmpSidecarFiles() {
        boolean restore = prefs != null && prefs.containsKey(XmpPreferences.KEY_FORCE_CREATE_XMP_SIDECARFILES)
                ? prefs.getBoolean(XmpPreferences.KEY_FORCE_CREATE_XMP_SIDECARFILES)
                :false;
        checkBoxForceCreateXmpSidecarFiles.setSelected(restore);
    }

    private void restoreLockFileWhenWritingXmp() {
        checkBoxLockFileWhenWritingXmp.setSelected(PreferencesUtil.getBoolean(XmpPreferences.KEY_LOCK_FILE_WHEN_WRITING_XMP, true));
    }

    private void restoreWriteExifDateToXmpDateCreated() {
        boolean restore = prefs != null && prefs.containsKey(PreferencesKeys.KEY_DISABLE_SAVE_EXIF_TO_XMP_DATE_CREATED)
                ? !prefs.getBoolean(PreferencesKeys.KEY_DISABLE_SAVE_EXIF_TO_XMP_DATE_CREATED)
                :false;
        checkBoxWriteExifDateToXmpDateCreated.setSelected(restore);
    }

    private void restoreInputHelperDialogAlwaysOnTop() {
        boolean alwaysOnTop = prefs == null
                ? true
                : prefs.getBoolean(AppPreferencesKeys.KEY_UI_INPUT_HELPER_DIALOG_ALWAYS_ON_TOP, true);
        checkBoxInputHelperDialogAlwaysOnTop.setSelected(alwaysOnTop);
    }

    private void persistInputHelperDialogAlwaysOnTop() {
        prefs.setBoolean(AppPreferencesKeys.KEY_UI_INPUT_HELPER_DIALOG_ALWAYS_ON_TOP, checkBoxInputHelperDialogAlwaysOnTop.isSelected());
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
                new ArrayList<>(Lookup.getDefault().lookupAll(OptionPageProvider.class));
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        tabbedPane = UiFactory.tabbedPane();
        panelDefault = UiFactory.panel();
        checkBoxIsAcceptHiddenDirectories = UiFactory.checkBox();
        checkBoxLockFileWhenWritingXmp = UiFactory.checkBox();
        checkBoxEnableDeleteDirectories = UiFactory.checkBox();
        panelCheckForUpdates = UiFactory.panel();
        checkBoxCheckForUpdates = UiFactory.checkBox();
        checkBoxDisplaySearchButton = UiFactory.checkBox();
        checkBoxUseLongXmpSidecarFileNames = UiFactory.checkBox();
        checkBoxForceCreateXmpSidecarFiles = UiFactory.checkBox();
        checkBoxInputHelperDialogAlwaysOnTop = UiFactory.checkBox();
        checkBoxWriteExifDateToXmpDateCreated = UiFactory.checkBox();
        panelEditMetadata = UiFactory.panel();
        panelMdTextAreasColumns = UiFactory.panel();
        labelMdTextAreasColumnsPropmpt = UiFactory.label();
        spinnerMdTextAreasColumns = UiFactory.spinner();
        labelMdTextAreasColumnsInfo = UiFactory.label();
        checkBoxDisplayWordsetsEditPanel = UiFactory.checkBox();
        panelCopyMoveFiles = UiFactory.panel();
        radioButtonCopyMoveFileConfirmOverwrite = UiFactory.radioButton();
        radioButtonCopyMoveFileRenameIfExists = UiFactory.radioButton();
        panelRepositoryDirectory = UiFactory.panel();
        labelInfoRepositoryDirectory = UiFactory.label();
        panelButtonsRepositoryDirectory = UiFactory.panel();
        buttonChooseRepositoryDirectory = UiFactory.button();
        buttonSetDefaultRepositoryDirectoryName = UiFactory.button();
        labelRepositoryDirectory = UiFactory.label();
        panelFill = UiFactory.panel();

        setLayout(new java.awt.GridBagLayout());

        panelDefault.setLayout(new java.awt.GridBagLayout());

        checkBoxIsAcceptHiddenDirectories.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        panelDefault.add(checkBoxIsAcceptHiddenDirectories, gridBagConstraints);

        checkBoxEnableDeleteDirectories.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxEnableDeleteDirectories.text")); // NOI18N
        checkBoxEnableDeleteDirectories.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxEnableDeleteDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxEnableDeleteDirectories, gridBagConstraints);

        panelCheckForUpdates.setLayout(new java.awt.GridBagLayout());

        checkBoxCheckForUpdates.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxCheckForUpdates.text")); // NOI18N
        checkBoxCheckForUpdates.addActionListener(new java.awt.event.ActionListener() {
            @Override
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
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(panelCheckForUpdates, gridBagConstraints);

        checkBoxDisplaySearchButton.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxDisplaySearchButton, gridBagConstraints);

        checkBoxUseLongXmpSidecarFileNames.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxUseLongXmpSidecarFileNames.text")); // NOI18N
        checkBoxUseLongXmpSidecarFileNames.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUseLongXmpSidecarFileNamesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxUseLongXmpSidecarFileNames, gridBagConstraints);

        checkBoxForceCreateXmpSidecarFiles.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxForceCreateXmpSidecarFiles.text")); // NOI18N
        checkBoxForceCreateXmpSidecarFiles.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxForceCreateXmpSidecarFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxForceCreateXmpSidecarFiles, gridBagConstraints);

        checkBoxLockFileWhenWritingXmp.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxLockFileWhenWritingXmp.text")); // NOI18N
        checkBoxLockFileWhenWritingXmp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxLockFileWhenWritingXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxLockFileWhenWritingXmp, gridBagConstraints);

        checkBoxWriteExifDateToXmpDateCreated.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxWriteExifDateToXmpDateCreated.text")); // NOI18N
        checkBoxWriteExifDateToXmpDateCreated.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxWriteExifDateToXmpDateCreatedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxWriteExifDateToXmpDateCreated, gridBagConstraints);

        checkBoxInputHelperDialogAlwaysOnTop.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxInputHelperDialogAlwaysOnTop.text")); // NOI18N
        checkBoxInputHelperDialogAlwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                persistInputHelperDialogAlwaysOnTop();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 10);
        panelDefault.add(checkBoxInputHelperDialogAlwaysOnTop, gridBagConstraints);

        panelEditMetadata.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "MiscSettingsPanel.panelEditMetadata.border.title"))); // NOI18N
        panelEditMetadata.setLayout(new java.awt.GridBagLayout());

        panelMdTextAreasColumns.setLayout(new java.awt.GridBagLayout());

        labelMdTextAreasColumnsPropmpt.setText(Bundle.getString(getClass(), "MiscSettingsPanel.labelMdTextAreasColumnsPropmpt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelMdTextAreasColumns.add(labelMdTextAreasColumnsPropmpt, gridBagConstraints);

        spinnerMdTextAreasColumns.setModel(new javax.swing.SpinnerNumberModel(AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_DEFAULT, AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MINIMUM, AppPreferencesDefaults.UI_COLUMNS_MD_TEXT_AREAS_MAXIMUM, 5));
        spinnerMdTextAreasColumns.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMdTextAreasColumnsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelMdTextAreasColumns.add(spinnerMdTextAreasColumns, gridBagConstraints);

        labelMdTextAreasColumnsInfo.setText(Bundle.getString(getClass(), "MiscSettingsPanel.labelMdTextAreasColumnsInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelMdTextAreasColumns.add(labelMdTextAreasColumnsInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelEditMetadata.add(panelMdTextAreasColumns, gridBagConstraints);

        checkBoxDisplayWordsetsEditPanel.setText(Bundle.getString(getClass(), "MiscSettingsPanel.checkBoxDisplayWordsetsEditPanel.text")); // NOI18N
        checkBoxDisplayWordsetsEditPanel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplayWordsetsEditPanelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelEditMetadata.add(checkBoxDisplayWordsetsEditPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 10, 0, 10);
        panelDefault.add(panelEditMetadata, gridBagConstraints);

        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "MiscSettingsPanel.panelCopyMoveFiles.border.title"))); // NOI18N
        panelCopyMoveFiles.setLayout(new java.awt.GridBagLayout());

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(Bundle.getString(getClass(), "MiscSettingsPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite, gridBagConstraints);

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(Bundle.getString(getClass(), "MiscSettingsPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
        radioButtonCopyMoveFileRenameIfExists.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileRenameIfExistsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 5, 5);
        panelCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 10, 0, 10);
        panelDefault.add(panelCopyMoveFiles, gridBagConstraints);

        panelRepositoryDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "MiscSettingsPanel.panelRepositoryDirectory.border.title"))); // NOI18N
        panelRepositoryDirectory.setLayout(new java.awt.GridBagLayout());

        labelInfoRepositoryDirectory.setText(Bundle.getString(getClass(), "MiscSettingsPanel.labelInfoRepositoryDirectory.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelRepositoryDirectory.add(labelInfoRepositoryDirectory, gridBagConstraints);

        panelButtonsRepositoryDirectory.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(5), 0));

        buttonChooseRepositoryDirectory.setText(Bundle.getString(getClass(), "MiscSettingsPanel.buttonChooseRepositoryDirectory.text")); // NOI18N
        buttonChooseRepositoryDirectory.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseRepositoryDirectoryActionPerformed(evt);
            }
        });
        panelButtonsRepositoryDirectory.add(buttonChooseRepositoryDirectory);

        buttonSetDefaultRepositoryDirectoryName.setText(Bundle.getString(getClass(), "MiscSettingsPanel.buttonSetDefaultRepositoryDirectoryName.text")); // NOI18N
        buttonSetDefaultRepositoryDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetDefaultRepositoryDirectoryNameActionPerformed(evt);
            }
        });
        panelButtonsRepositoryDirectory.add(buttonSetDefaultRepositoryDirectoryName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelRepositoryDirectory.add(panelButtonsRepositoryDirectory, gridBagConstraints);

        labelRepositoryDirectory.setText(" "); // NOI18N
        labelRepositoryDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelRepositoryDirectory.add(labelRepositoryDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 10, 0, 10);
        panelDefault.add(panelRepositoryDirectory, gridBagConstraints);

        panelFill.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panelDefault.add(panelFill, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "MiscSettingsPanel.panelDefault.TabConstraints.tabTitle"), panelDefault); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }

    private void checkBoxCheckForUpdatesActionPerformed(java.awt.event.ActionEvent evt) {
        setCheckForUpdates();
    }

    private void radioButtonCopyMoveFileRenameIfExistsActionPerformed(java.awt.event.ActionEvent evt) {
        setCopyMoveFiles();
    }

    private void radioButtonCopyMoveFileConfirmOverwriteActionPerformed(java.awt.event.ActionEvent evt) {
        setCopyMoveFiles();
    }

    private void checkBoxIsAcceptHiddenDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {
        setAcceptHiddenDirectories();
    }

    private void buttonChooseRepositoryDirectoryActionPerformed(java.awt.event.ActionEvent evt) {
        chooseRepositoryDirectory();
    }

    private void buttonSetDefaultRepositoryDirectoryNameActionPerformed(java.awt.event.ActionEvent evt) {
        setDefaultRepositoryDirectory();
        displayRepositoryDirectoryInfo();
    }

    private void checkBoxDisplaySearchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setDisplaySearchButton();
    }

    private void checkBoxDisplayWordsetsEditPanelActionPerformed(java.awt.event.ActionEvent evt) {
        setDisplayWordsetsEditPanel();
    }

    private void checkBoxUseLongXmpSidecarFileNamesActionPerformed(java.awt.event.ActionEvent evt) {
        setUseLongXmpSidecarFileNames();
    }

    private void checkBoxForceCreateXmpSidecarFilesActionPerformed(java.awt.event.ActionEvent evt) {
        setForceCreateXmpSidecarFiles();
    }

    private void checkBoxLockFileWhenWritingXmpActionPerformed(java.awt.event.ActionEvent evt) {
        setLockFileWhenWritingXmp();
    }

    private void checkBoxWriteExifDateToXmpDateCreatedActionPerformed(java.awt.event.ActionEvent evt) {
        setWriteExifDateToXmpDateCreated();
    }

    private void spinnerMdTextAreasColumnsStateChanged(javax.swing.event.ChangeEvent evt) {
        persistMdTextAreasColumns();
    }

    private void checkBoxEnableDeleteDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {
        setDeleteDirectoriesEnabled();
    }

    private javax.swing.JButton buttonChooseRepositoryDirectory;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetDefaultRepositoryDirectoryName;
    private javax.swing.JCheckBox checkBoxCheckForUpdates;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxDisplayWordsetsEditPanel;
    private javax.swing.JCheckBox checkBoxEnableDeleteDirectories;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JCheckBox checkBoxLockFileWhenWritingXmp;
    private javax.swing.JCheckBox checkBoxUseLongXmpSidecarFileNames;
    private javax.swing.JCheckBox checkBoxForceCreateXmpSidecarFiles;
    private javax.swing.JCheckBox checkBoxInputHelperDialogAlwaysOnTop;
    private javax.swing.JCheckBox checkBoxWriteExifDateToXmpDateCreated;
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
}
