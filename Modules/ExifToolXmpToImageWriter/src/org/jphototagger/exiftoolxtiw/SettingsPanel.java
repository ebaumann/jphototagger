package org.jphototagger.exiftoolxtiw;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.io.filefilter.AcceptExactFilenamesFileFilter;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.SystemUtil;

/**
 * @author Elmar Baumann
 */
public class SettingsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private static final String EXIF_TOOL_FILE_PATH_DOES_NOT_EXIST = Bundle.getString(SettingsPanel.class, "SettingsPanel.NoExifToolFilePath");
    private static final String EXIF_TOOL_FILE_PATH_IS_FALSE = Bundle.getString(SettingsPanel.class, "SettingsPanel.ExifToolFilePathFalse");
    private final Settings settings = new Settings();
    private final DefaultListModel<String> fileSuffixesListModel = new DefaultListModel<>();

    public SettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        restore();
        textFieldExifToolFilePath.getDocument().addDocumentListener(exifToolFilePathListener);
        fileSuffixesListModel.addListDataListener(filePathListener);
        buttonRemoveFileSuffix.setEnabled(false);
        listFileSuffixes.getSelectionModel().addListSelectionListener(filePathSelectionListener);
        setGuiEnabled();
        MnemonicUtil.setMnemonics(this);
        setErrorLabelsVisible();
        AnnotationProcessor.process(this);
    }

    private void restore() {
        restoreExifToolFilePath();
        checkBoxSelfResponsible.setSelected(settings.isSelfResponsible());
        checkBoxCreateBackupFile.setSelected(settings.isCreateBackupFile());
        checkBoxWriteOnSaveXmp.setSelected(settings.isWriteOnEveryXmpFileModification());
        checkBoxExifToolEnabled.setSelected(settings.isExifToolEnabled());
        restoreFileSuffixes();
        checkSaveInputEarly();
        setGuiEnabled();
    }

    private void restoreExifToolFilePath() {
        String filePath = settings.getExifToolFilePath();
        if (StringUtil.hasContent(filePath)) {
            File exifTool = new File(filePath);
            if (exifTool.isFile()) {
                textFieldExifToolFilePath.setForeground(UIManager.getColor("TextField.foreground"));
                textFieldExifToolFilePath.setText(filePath);
            } else {
                textFieldExifToolFilePath.setForeground(Color.RED);
                textFieldExifToolFilePath.setText(EXIF_TOOL_FILE_PATH_IS_FALSE);
            }
        } else {
            textFieldExifToolFilePath.setForeground(Color.RED);
            textFieldExifToolFilePath.setText(EXIF_TOOL_FILE_PATH_DOES_NOT_EXIST);
        }
    }

    private void restoreFileSuffixes() {
        for (String suffix : settings.getFileSuffixes()) {
            fileSuffixesListModel.addElement(suffix);
        }
    }

    private final DocumentListener exifToolFilePathListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            changed();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changed();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changed();
        }

        private void changed() {
            persistExifToolFilePath();
        }
    };

    private final ListDataListener filePathListener = new ListDataListener() {
        @Override
        public void intervalAdded(ListDataEvent e) {
            changed();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            changed();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            changed();
        }

        private void changed() {
            persistFileSuffixes();
            setErrorLabelsVisible();
        }
    };

    private final ListSelectionListener filePathSelectionListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean suffixIsSelected = listFileSuffixes.getSelectedIndex() >= 0;
                buttonRemoveFileSuffix.setEnabled(suffixIsSelected);
            }
        }
    };


    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void applySettings(PreferencesChangedEvent evt) {
        if (CommonPreferences.KEY_SAVE_INPUT_EARLY.equals(evt.getKey())) {
            Object value = evt.getNewValue();
            if (value instanceof Boolean) {
                boolean saveInputEarly = (Boolean) value;
                if (!saveInputEarly) {
                    settings.setWriteOnEveryXmpFileModification(false);
                }
                checkSaveInputEarly();
            }
        }
    }

    private void checkSaveInputEarly() {
        boolean saveInputEarly = settings.isInputSaveEarly();
        checkBoxWriteOnSaveXmp.setEnabled(checkBoxSelfResponsible.isSelected()
                && checkBoxExifToolEnabled.isSelected()
                && !saveInputEarly);
        if (saveInputEarly) {
            checkBoxWriteOnSaveXmp.setSelected(false);
        }
        setErrorLabelsVisible();
    }

    private void persistExifToolFilePath() {
        String filePath = textFieldExifToolFilePath.getText();
        if (!StringUtil.hasContent(filePath)
                || EXIF_TOOL_FILE_PATH_DOES_NOT_EXIST.equals(filePath)
                || EXIF_TOOL_FILE_PATH_IS_FALSE.equals(filePath)) {
            settings.setExifToolFilePath(null);
        } else {
            settings.setExifToolFilePath(filePath.trim());
        }
    }

    private void persistFileSuffixes() {
        Collection<String> fileSuffixes = new ArrayList<>();
        for (Enumeration<String> e = fileSuffixesListModel.elements(); e.hasMoreElements(); ) {
            fileSuffixes.add(e.nextElement().trim());
        }
        settings.setFileSuffixes(fileSuffixes);
    }

    private void setDefaultFileSuffixes() {
        fileSuffixesListModel.clear();
        for (String suffix : settings.getDefaultFileSuffixes()) {
            fileSuffixesListModel.addElement(suffix);
        }
    }

    private void removeSelectedFileSuffixes() {
        for (String selSuffix : listFileSuffixes.getSelectedValuesList()) {
            fileSuffixesListModel.removeElement(selSuffix);
        }
    }

    private void addFileSuffixes() {
        String message = Bundle.getString(SettingsPanel.class, "SettingsPanel.AddFileSuffixes.Message");
        String input = MessageDisplayer.input(message, "");
        if (StringUtil.hasContent(input)) {
            for (String suffix : input.split(",")) {
                String sfx = suffix.trim();
                if (sfx.isEmpty()) {
                    continue;
                }
                if (!suffix.startsWith(".")) {
                    sfx = "." + sfx;
                }
                if (!fileSuffixesListModel.contains(sfx)) {
                    fileSuffixesListModel.addElement(sfx);
                }
            }
        }
    }

    private void setExifToolFilePath() {
        File file = FileChooserHelper.chooseFile(createFileChooserProperties());

        if (file != null) {
            textFieldExifToolFilePath.setForeground(UIManager.getColor("TextField.foreground"));
            textFieldExifToolFilePath.setText(file.getAbsolutePath());
        }
    }

    private FileChooserProperties createFileChooserProperties() {
        String dirPath = SystemUtil.isWindows()
                ? System.getenv("ProgramFiles")
                : "/usr/bin";
        FileChooserProperties fcProps = new FileChooserProperties();

        fcProps.dialogTitle(Bundle.getString(SettingsPanel.class, "SettingsPanel.FileChooser.Title"));
        fcProps.currentDirectoryPath(dirPath);
        fcProps.multiSelectionEnabled(false);
        fcProps.fileFilter(createFileFilter());
        fcProps.fileSelectionMode(JFileChooser.FILES_ONLY);

        return fcProps;
    }

    private FileFilter createFileFilter() {
        Collection<String> filenames = SystemUtil.isWindows()
                ? Arrays.asList("exiftool(-k).exe", "exiftool.exe")
                : Arrays.asList("exiftool");
        AcceptExactFilenamesFileFilter filter = new AcceptExactFilenamesFileFilter(filenames);

        return filter.forFileChooser(Bundle.getString(SettingsPanel.class, "SettingsPanel.FileChooser.ExifToolFile"));
    }

    private void setGuiEnabled() {
        boolean selfResponsible = checkBoxSelfResponsible.isSelected();
        boolean enabled = selfResponsible && checkBoxExifToolEnabled.isSelected();

        buttonAddFileSuffix.setEnabled(enabled);
        buttonChooseExifTool.setEnabled(enabled);
        buttonRemoveFileSuffix.setEnabled(enabled && listFileSuffixes.getSelectedIndex() >= 0);
        buttonSetDefaultFileSuffixes.setEnabled(enabled);

        checkBoxExifToolEnabled.setEnabled(selfResponsible);
        checkBoxCreateBackupFile.setEnabled(enabled);
        checkSaveInputEarly();
    }

    private void setErrorLabelsVisible() {
        labelErrorCanWrite.setVisible(checkBoxExifToolEnabled.isSelected() && !settings.canWrite());
        labelErrorInputsSavedEarly.setVisible(settings.isInputSaveEarly());
    }

    private void browseExifToolWebsite() {
        DesktopUtil.browse("https://www.sno.phy.queensu.ca/~phil/exiftool/", "ExifToolXmpToImageWriter.Browser");
    }

    private void selfResponsiblePerformed() {
        boolean selfResponsible = checkBoxSelfResponsible.isSelected();
        settings.setSelfResponsible(selfResponsible);
        if (!selfResponsible) {
            settings.setExifToolEnabled(false);
            checkBoxExifToolEnabled.setSelected(false);
        }
        setGuiEnabled();
    }

    private void defaultFileSuffixesPerformed() {
        setDefaultFileSuffixes();
        setErrorLabelsVisible();
    }

    private void writeOnSaveXmpPerformed() {
        if (checkBoxWriteOnSaveXmp.isSelected()) {
            MessageDisplayer.information(this, Bundle.getString(SettingsPanel.class, "SettingsPanel.WarningWriteOnSaveXmp"));
        }
        settings.setWriteOnEveryXmpFileModification(checkBoxWriteOnSaveXmp.isSelected());
        setErrorLabelsVisible();
    }

    private void createBackupFilePerformed() {
        settings.setCreateBackupFile(checkBoxCreateBackupFile.isSelected());
        setErrorLabelsVisible();
    }

    private void removeFileSuffixPerformed() {
        removeSelectedFileSuffixes();
        setErrorLabelsVisible();
    }

    private void addFileSuffixPerformed() {
        addFileSuffixes();
        setErrorLabelsVisible();
    }

    private void chooseExifToolPerformed() {
        setExifToolFilePath();
        setErrorLabelsVisible();
    }

    private void exifToolEnabledPerformed() {
        settings.setExifToolEnabled(checkBoxExifToolEnabled.isSelected());
        setErrorLabelsVisible();
        setGuiEnabled();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        paneContents = new javax.swing.JPanel();
        checkBoxSelfResponsible = org.jphototagger.resources.UiFactory.checkBox();
        panelExifToolGeneral = new javax.swing.JPanel();
        checkBoxExifToolEnabled = org.jphototagger.resources.UiFactory.checkBox();
        buttonBrowseExifToolWebsite = org.jphototagger.resources.UiFactory.button();
        textAreaInfo = new javax.swing.JTextArea();
        labelErrorCanWrite = org.jphototagger.resources.UiFactory.label();
        panelChooseExifTool = new javax.swing.JPanel();
        textFieldExifToolFilePath = new javax.swing.JTextField();
        buttonChooseExifTool = org.jphototagger.resources.UiFactory.button();
        panelWriteOnSaveXmp = new javax.swing.JPanel();
        checkBoxWriteOnSaveXmp = org.jphototagger.resources.UiFactory.checkBox();
        labelErrorInputsSavedEarly = org.jphototagger.resources.UiFactory.label();
        checkBoxCreateBackupFile = org.jphototagger.resources.UiFactory.checkBox();
        labelFileSuffixes = org.jphototagger.resources.UiFactory.label();
        scrollPaneFileSuffixes = org.jphototagger.resources.UiFactory.scrollPane();
        listFileSuffixes = new javax.swing.JList<String>();
        panelButtonsFileSuffixes = new javax.swing.JPanel();
        buttonSetDefaultFileSuffixes = org.jphototagger.resources.UiFactory.button();
        buttonAddFileSuffix = org.jphototagger.resources.UiFactory.button();
        buttonRemoveFileSuffix = org.jphototagger.resources.UiFactory.button();

        setLayout(new java.awt.GridBagLayout());

        paneContents.setLayout(new java.awt.GridBagLayout());

        checkBoxSelfResponsible.setText(Bundle.getString(getClass(), "SettingsPanel.checkBoxSelfResponsible.text")); // NOI18N
        checkBoxSelfResponsible.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxSelfResponsibleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(checkBoxSelfResponsible, gridBagConstraints);

        panelExifToolGeneral.setLayout(new java.awt.GridBagLayout());

        checkBoxExifToolEnabled.setText(Bundle.getString(getClass(), "SettingsPanel.checkBoxExifToolEnabled.text")); // NOI18N
        checkBoxExifToolEnabled.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxExifToolEnabledActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelExifToolGeneral.add(checkBoxExifToolEnabled, gridBagConstraints);

        buttonBrowseExifToolWebsite.setText(Bundle.getString(getClass(), "SettingsPanel.buttonBrowseExifToolWebsite.text")); // NOI18N
        buttonBrowseExifToolWebsite.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseExifToolWebsiteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panelExifToolGeneral.add(buttonBrowseExifToolWebsite, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(panelExifToolGeneral, gridBagConstraints);

        textAreaInfo.setEditable(false);
        textAreaInfo.setColumns(20);
        textAreaInfo.setLineWrap(true);
        textAreaInfo.setRows(1);
        textAreaInfo.setText(Bundle.getString(getClass(), "SettingsPanel.textAreaInfo.text")); // NOI18N
        textAreaInfo.setWrapStyleWord(true);
        textAreaInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        textAreaInfo.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(textAreaInfo, gridBagConstraints);

        labelErrorCanWrite.setForeground(java.awt.Color.RED);
        labelErrorCanWrite.setText(Bundle.getString(getClass(), "SettingsPanel.labelErrorCanWrite.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        paneContents.add(labelErrorCanWrite, gridBagConstraints);

        panelChooseExifTool.setLayout(new java.awt.GridBagLayout());

        textFieldExifToolFilePath.setEditable(false);
        textFieldExifToolFilePath.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelChooseExifTool.add(textFieldExifToolFilePath, gridBagConstraints);

        buttonChooseExifTool.setText(Bundle.getString(getClass(), "SettingsPanel.buttonChooseExifTool.text")); // NOI18N
        buttonChooseExifTool.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseExifToolActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelChooseExifTool.add(buttonChooseExifTool, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(panelChooseExifTool, gridBagConstraints);

        panelWriteOnSaveXmp.setLayout(new java.awt.GridBagLayout());

        checkBoxWriteOnSaveXmp.setText(Bundle.getString(getClass(), "SettingsPanel.checkBoxWriteOnSaveXmp.text")); // NOI18N
        checkBoxWriteOnSaveXmp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxWriteOnSaveXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelWriteOnSaveXmp.add(checkBoxWriteOnSaveXmp, gridBagConstraints);

        labelErrorInputsSavedEarly.setForeground(java.awt.Color.RED);
        labelErrorInputsSavedEarly.setText(Bundle.getString(getClass(), "SettingsPanel.labelErrorInputsSavedEarly.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 0);
        panelWriteOnSaveXmp.add(labelErrorInputsSavedEarly, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(panelWriteOnSaveXmp, gridBagConstraints);

        checkBoxCreateBackupFile.setText(Bundle.getString(getClass(), "SettingsPanel.checkBoxCreateBackupFile.text")); // NOI18N
        checkBoxCreateBackupFile.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCreateBackupFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paneContents.add(checkBoxCreateBackupFile, gridBagConstraints);

        labelFileSuffixes.setText(Bundle.getString(getClass(), "SettingsPanel.labelFileSuffixes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(labelFileSuffixes, gridBagConstraints);

        scrollPaneFileSuffixes.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(200, 100));

        listFileSuffixes.setModel(fileSuffixesListModel);
        listFileSuffixes.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        scrollPaneFileSuffixes.setViewportView(listFileSuffixes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(scrollPaneFileSuffixes, gridBagConstraints);

        panelButtonsFileSuffixes.setLayout(new java.awt.GridBagLayout());

        buttonSetDefaultFileSuffixes.setText(Bundle.getString(getClass(), "SettingsPanel.buttonSetDefaultFileSuffixes.text")); // NOI18N
        buttonSetDefaultFileSuffixes.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetDefaultFileSuffixesActionPerformed(evt);
            }
        });
        panelButtonsFileSuffixes.add(buttonSetDefaultFileSuffixes, new java.awt.GridBagConstraints());

        buttonAddFileSuffix.setText(Bundle.getString(getClass(), "SettingsPanel.buttonAddFileSuffix.text")); // NOI18N
        buttonAddFileSuffix.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddFileSuffixActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtonsFileSuffixes.add(buttonAddFileSuffix, gridBagConstraints);

        buttonRemoveFileSuffix.setText(Bundle.getString(getClass(), "SettingsPanel.buttonRemoveFileSuffix.text")); // NOI18N
        buttonRemoveFileSuffix.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveFileSuffixActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtonsFileSuffixes.add(buttonRemoveFileSuffix, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        paneContents.add(panelButtonsFileSuffixes, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        add(paneContents, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonSetDefaultFileSuffixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetDefaultFileSuffixesActionPerformed
        defaultFileSuffixesPerformed();
    }//GEN-LAST:event_buttonSetDefaultFileSuffixesActionPerformed

    private void checkBoxWriteOnSaveXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxWriteOnSaveXmpActionPerformed
        writeOnSaveXmpPerformed();
    }//GEN-LAST:event_checkBoxWriteOnSaveXmpActionPerformed

    private void buttonRemoveFileSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveFileSuffixActionPerformed
        removeFileSuffixPerformed();
    }//GEN-LAST:event_buttonRemoveFileSuffixActionPerformed

    private void buttonAddFileSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddFileSuffixActionPerformed
        addFileSuffixPerformed();
    }//GEN-LAST:event_buttonAddFileSuffixActionPerformed

    private void buttonChooseExifToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseExifToolActionPerformed
        chooseExifToolPerformed();
    }//GEN-LAST:event_buttonChooseExifToolActionPerformed

    private void checkBoxExifToolEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxExifToolEnabledActionPerformed
        exifToolEnabledPerformed();
    }//GEN-LAST:event_checkBoxExifToolEnabledActionPerformed

    private void buttonBrowseExifToolWebsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseExifToolWebsiteActionPerformed
        browseExifToolWebsite();
    }//GEN-LAST:event_buttonBrowseExifToolWebsiteActionPerformed

    private void checkBoxSelfResponsibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxSelfResponsibleActionPerformed
        selfResponsiblePerformed();
    }//GEN-LAST:event_checkBoxSelfResponsibleActionPerformed

    private void checkBoxCreateBackupFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxCreateBackupFileActionPerformed
        createBackupFilePerformed();
    }//GEN-LAST:event_checkBoxCreateBackupFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddFileSuffix;
    private javax.swing.JButton buttonBrowseExifToolWebsite;
    private javax.swing.JButton buttonChooseExifTool;
    private javax.swing.JButton buttonRemoveFileSuffix;
    private javax.swing.JButton buttonSetDefaultFileSuffixes;
    private javax.swing.JCheckBox checkBoxCreateBackupFile;
    private javax.swing.JCheckBox checkBoxExifToolEnabled;
    private javax.swing.JCheckBox checkBoxSelfResponsible;
    private javax.swing.JCheckBox checkBoxWriteOnSaveXmp;
    private javax.swing.JLabel labelErrorCanWrite;
    private javax.swing.JLabel labelErrorInputsSavedEarly;
    private javax.swing.JLabel labelFileSuffixes;
    private javax.swing.JList<String> listFileSuffixes;
    private javax.swing.JPanel paneContents;
    private javax.swing.JPanel panelButtonsFileSuffixes;
    private javax.swing.JPanel panelChooseExifTool;
    private javax.swing.JPanel panelExifToolGeneral;
    private javax.swing.JPanel panelWriteOnSaveXmp;
    private javax.swing.JScrollPane scrollPaneFileSuffixes;
    private javax.swing.JTextArea textAreaInfo;
    private javax.swing.JTextField textFieldExifToolFilePath;
    // End of variables declaration//GEN-END:variables
}
