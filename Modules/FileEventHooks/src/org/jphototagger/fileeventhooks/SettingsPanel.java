package org.jphototagger.fileeventhooks;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public class SettingsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color TEXTFIELD_FOREGROUND = getTextFieldForeground();
    private static final String LAST_CHOOSEN_DIR_KEY = "Module.FileEventHooks.LastChoosenDir";
    private final Map<JTextField, String> keyOfTextField = new HashMap<JTextField, String>(4);
    private final Map<JButton, JTextField> textFieldOfRemoveButton = new HashMap<JButton, JTextField>(4);
    private final FilenameSuffixesListModel filenameSuffixesListModel = new FilenameSuffixesListModel();
    private String selectedFilenameSuffix;
    private String lastChoosenDirectory;

    public SettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initKeyOfTextFieldMap();
        initTextFieldOfRemoveButtonMap();
        initLastChoosenDirectory();
        setScriptsToTextFields();
        MnemonicUtil.setMnemonics(this);
    }

    private static Color getTextFieldForeground() {
        Color foreground = UIManager.getColor("TextField.foreground");
        return foreground == null ? Color.BLACK : foreground;
    }

    private void initKeyOfTextFieldMap() {
        keyOfTextField.put(textFieldFileCopied, FileEventHooksPreferencesKeys.FILE_COPIED_KEY);
        keyOfTextField.put(textFieldFileDeleted, FileEventHooksPreferencesKeys.FILE_DELETED_KEY);
        keyOfTextField.put(textFieldFileMoved, FileEventHooksPreferencesKeys.FILE_MOVED_KEY);
        keyOfTextField.put(textFieldFileRenamed, FileEventHooksPreferencesKeys.FILE_RENAMED_KEY);
    }

    private void initTextFieldOfRemoveButtonMap() {
        textFieldOfRemoveButton.put(buttonRemoveScriptFileCopied, textFieldFileCopied);
        textFieldOfRemoveButton.put(buttonRemoveScriptFileDeleted, textFieldFileDeleted);
        textFieldOfRemoveButton.put(buttonRemoveScriptFileMoved, textFieldFileMoved);
        textFieldOfRemoveButton.put(buttonRemoveScriptFileRenamed, textFieldFileRenamed);
    }

    private void initLastChoosenDirectory() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        String prefString = preferences.getString(LAST_CHOOSEN_DIR_KEY);

        lastChoosenDirectory = StringUtil.hasContent(prefString) ? prefString : "";
    }

    private void setScriptsToTextFields() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);

        setScriptToTextField(preferences.getString(FileEventHooksPreferencesKeys.FILE_COPIED_KEY), textFieldFileCopied);
        setScriptToTextField(preferences.getString(FileEventHooksPreferencesKeys.FILE_DELETED_KEY), textFieldFileDeleted);
        setScriptToTextField(preferences.getString(FileEventHooksPreferencesKeys.FILE_MOVED_KEY), textFieldFileMoved);
        setScriptToTextField(preferences.getString(FileEventHooksPreferencesKeys.FILE_RENAMED_KEY), textFieldFileRenamed);
        setRemoveButtonsEnabled();
    }

    private void setScriptToTextField(String script, JTextField textField) {
        if (!StringUtil.hasContent(script)) {
            return;
        }

        boolean fileExists = FileUtil.existsFile(new File(script));

        textField.setText(script);
        textField.setForeground(fileExists ? TEXTFIELD_FOREGROUND : Color.RED);
    }

    private void chooseScript(JTextField textField) {
        File scriptFile = chooseScriptFile();

        if (scriptFile == null) {
            return;
        }

        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        String key = keyOfTextField.get(textField);
        String scriptFilePath = scriptFile.getAbsolutePath();

        preferences.setString(key, scriptFilePath);
        textField.setText(scriptFilePath);
        setRemoveButtonsEnabled();
    }

    private File chooseScriptFile() {
        FileChooserProperties props = new FileChooserProperties();

        props.currentDirectoryPath(lastChoosenDirectory);
        props.dialogTitle(Bundle.getString(SettingsPanel.class, "SettingsPanel.FileChooser.Title"));
        props.multiSelectionEnabled(false);
        props.fileSelectionMode(JFileChooser.FILES_ONLY);
        props.propertyKeyPrefix("Module.FileEventHooks.FileChooser");

        File scriptFile = FileChooserHelper.chooseFile(props);

        if (scriptFile != null) {
            String dirOfScriptFile = scriptFile.getParent();

            if (dirOfScriptFile != null) {
                lastChoosenDirectory = dirOfScriptFile;

                Preferences preferences = Lookup.getDefault().lookup(Preferences.class);

                preferences.setString(LAST_CHOOSEN_DIR_KEY, lastChoosenDirectory);
            }
        }

        return scriptFile;
    }

    private void setRemoveButtonsEnabled() {
        for (JButton button : textFieldOfRemoveButton.keySet()) {
            JTextField textField = textFieldOfRemoveButton.get(button);
            String script = textField.getText();
            button.setEnabled(StringUtil.hasContent(script));
        }
    }
    private final Action removeScriptFileAction = new AbstractAction() {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source instanceof JButton) {
                JButton button = (JButton) source;
                JTextField textField = textFieldOfRemoveButton.get(button);
                if (textField != null) {
                    textField.setText("");
                    String key = keyOfTextField.get(textField);
                    if (key != null) {
                        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
                        preferences.removeKey(key);
                    }
                    setRemoveButtonsEnabled();
                }
            }
        }
    };
    private final ListCellRenderer suffixesListCellRenderer = new DefaultListCellRenderer() {

        private static final long serialVersionUID = 1L;
        private Icon ICON_FILE = IconUtil.getImageIcon(SettingsPanel.class, "file.png");

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(ICON_FILE);
            return label;
        }
    };

    private void filenameSuffixTyped(KeyEvent evt) {
        String filenameSuffix = textFieldFilenameSuffix.getText();
        buttonAddFilenameSuffix.setEnabled(StringUtil.hasContent(filenameSuffix));
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addFilenameSuffix();
        }
    }

    private void addFilenameSuffix() {
        String filenameSuffix = textFieldFilenameSuffix.getText().trim();
        if (!filenameSuffix.isEmpty()) {
            filenameSuffixesListModel.addSuffix(filenameSuffix);
            ComponentUtil.parentWindowToFront(this);
        }
    }

    public String getSelectedFilenameSuffix() {
        return selectedFilenameSuffix;
    }

    public void setSelectedFilenameSuffix(String selectedFilenameSuffix) {
        this.selectedFilenameSuffix = selectedFilenameSuffix;
        boolean suffixSelected = StringUtil.hasContent(selectedFilenameSuffix);
        buttonRenameSelectedFilenameSuffix.setEnabled(suffixSelected);
        buttonRemoveSelectedFilenameSuffix.setEnabled(suffixSelected);
    }

    private void removeSelectedFilenameSuffix() {
        if (selectedFilenameSuffix != null
                && MessageDisplayer.confirmYesNo(this, Bundle.getString(SettingsPanel.class, "SettingsPanel.Confirm.RemoveSelectedFilenameSuffix", selectedFilenameSuffix))) {
            filenameSuffixesListModel.removeSuffix(selectedFilenameSuffix);
            ComponentUtil.parentWindowToFront(this);
            textFieldFilenameSuffix.requestFocusInWindow();
        }
    }

    private void renameSelectedFilenameSuffix() {
        if (selectedFilenameSuffix != null) {
            String message = Bundle.getString(SettingsPanel.class, "SettingsPanel.Input.RenameSelectedFilenameSuffix");
            String input = MessageDisplayer.input(message, selectedFilenameSuffix);
            if (StringUtil.hasContent(input)) {
                if (filenameSuffixesListModel.renameSuffix(selectedFilenameSuffix, input)) {
                    selectedFilenameSuffix = input;
                }
                ComponentUtil.parentWindowToFront(this);
                textFieldFilenameSuffix.requestFocusInWindow();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        tabbedPane = new javax.swing.JTabbedPane();
        panelJPhotoTaggerActions = new javax.swing.JPanel();
        labelInfoJPhotoTaggerFilenameSuffixes = new org.jdesktop.swingx.JXLabel();
        panelSuffixes = new javax.swing.JPanel();
        panelListFilenameSuffixes = new javax.swing.JPanel();
        scrollPaneListFilenameSuffixes = new javax.swing.JScrollPane();
        listFilenameSuffixes = new org.jdesktop.swingx.JXList();
        panelEditFilenameSuffix = new javax.swing.JPanel();
        labelFilenameSuffix = new javax.swing.JLabel();
        textFieldFilenameSuffix = new javax.swing.JTextField();
        buttonAddFilenameSuffix = new javax.swing.JButton();
        labelFilenameSuffixExample = new javax.swing.JLabel();
        panelButtonsFilenameSuffixes = new javax.swing.JPanel();
        buttonRenameSelectedFilenameSuffix = new javax.swing.JButton();
        buttonRemoveSelectedFilenameSuffix = new javax.swing.JButton();
        labelAttentionJptActions = new javax.swing.JLabel();
        panelUserScripts = new javax.swing.JPanel();
        labelGeneralInfo = new org.jdesktop.swingx.JXLabel();
        panelFileCopied = new javax.swing.JPanel();
        textFieldFileCopied = new javax.swing.JTextField();
        buttonChooseScriptFileCopied = new javax.swing.JButton();
        buttonRemoveScriptFileCopied = new javax.swing.JButton();
        labelParameterInfoFileCopied = new org.jdesktop.swingx.JXLabel();
        panelFileRenamed = new javax.swing.JPanel();
        textFieldFileRenamed = new javax.swing.JTextField();
        buttonChooseScriptFileRenamed = new javax.swing.JButton();
        buttonRemoveScriptFileRenamed = new javax.swing.JButton();
        labelParameterInfoFileRenamed = new org.jdesktop.swingx.JXLabel();
        panelFileMoved = new javax.swing.JPanel();
        textFieldFileMoved = new javax.swing.JTextField();
        buttonChooseScriptFileMoved = new javax.swing.JButton();
        buttonRemoveScriptFileMoved = new javax.swing.JButton();
        labelParameterInfoFileMoved = new org.jdesktop.swingx.JXLabel();
        panelFileDeleted = new javax.swing.JPanel();
        textFieldFileDeleted = new javax.swing.JTextField();
        buttonChooseScriptFileDeleted = new javax.swing.JButton();
        buttonRemoveScriptFileDeleted = new javax.swing.JButton();
        labelParameterInfoFileDeleted = new org.jdesktop.swingx.JXLabel();
        labelPathInfo = new org.jdesktop.swingx.JXLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        panelJPhotoTaggerActions.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/fileeventhooks/Bundle"); // NOI18N
        labelInfoJPhotoTaggerFilenameSuffixes.setText(bundle.getString("SettingsPanel.labelInfoJPhotoTaggerFilenameSuffixes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelJPhotoTaggerActions.add(labelInfoJPhotoTaggerFilenameSuffixes, gridBagConstraints);

        panelSuffixes.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsPanel.panelSuffixes.border.title"))); // NOI18N
        panelSuffixes.setLayout(new java.awt.GridBagLayout());

        panelListFilenameSuffixes.setLayout(new java.awt.GridBagLayout());

        listFilenameSuffixes.setModel(filenameSuffixesListModel);
        listFilenameSuffixes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFilenameSuffixes.setCellRenderer(suffixesListCellRenderer);
        listFilenameSuffixes.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedFilenameSuffix}"), listFilenameSuffixes, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        listFilenameSuffixes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listFilenameSuffixesKeyPressed(evt);
            }
        });
        scrollPaneListFilenameSuffixes.setViewportView(listFilenameSuffixes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelListFilenameSuffixes.add(scrollPaneListFilenameSuffixes, gridBagConstraints);

        panelEditFilenameSuffix.setLayout(new java.awt.GridBagLayout());

        labelFilenameSuffix.setLabelFor(textFieldFilenameSuffix);
        labelFilenameSuffix.setText(bundle.getString("SettingsPanel.labelFilenameSuffix.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelEditFilenameSuffix.add(labelFilenameSuffix, gridBagConstraints);

        textFieldFilenameSuffix.setColumns(5);
        textFieldFilenameSuffix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldFilenameSuffixKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelEditFilenameSuffix.add(textFieldFilenameSuffix, gridBagConstraints);

        buttonAddFilenameSuffix.setText(bundle.getString("SettingsPanel.buttonAddFilenameSuffix.text")); // NOI18N
        buttonAddFilenameSuffix.setEnabled(false);
        buttonAddFilenameSuffix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddFilenameSuffixActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelEditFilenameSuffix.add(buttonAddFilenameSuffix, gridBagConstraints);

        labelFilenameSuffixExample.setText(bundle.getString("SettingsPanel.labelFilenameSuffixExample.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelEditFilenameSuffix.add(labelFilenameSuffixExample, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelListFilenameSuffixes.add(panelEditFilenameSuffix, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelSuffixes.add(panelListFilenameSuffixes, gridBagConstraints);

        panelButtonsFilenameSuffixes.setLayout(new java.awt.GridLayout(2, 0, 0, 5));

        buttonRenameSelectedFilenameSuffix.setText(bundle.getString("SettingsPanel.buttonRenameSelectedFilenameSuffix.text")); // NOI18N
        buttonRenameSelectedFilenameSuffix.setEnabled(false);
        buttonRenameSelectedFilenameSuffix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameSelectedFilenameSuffixActionPerformed(evt);
            }
        });
        panelButtonsFilenameSuffixes.add(buttonRenameSelectedFilenameSuffix);

        buttonRemoveSelectedFilenameSuffix.setText(bundle.getString("SettingsPanel.buttonRemoveSelectedFilenameSuffix.text")); // NOI18N
        buttonRemoveSelectedFilenameSuffix.setEnabled(false);
        buttonRemoveSelectedFilenameSuffix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSelectedFilenameSuffixActionPerformed(evt);
            }
        });
        panelButtonsFilenameSuffixes.add(buttonRemoveSelectedFilenameSuffix);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelSuffixes.add(panelButtonsFilenameSuffixes, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelJPhotoTaggerActions.add(panelSuffixes, gridBagConstraints);

        labelAttentionJptActions.setText(bundle.getString("SettingsPanel.labelAttentionJptActions.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelJPhotoTaggerActions.add(labelAttentionJptActions, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("SettingsPanel.panelJPhotoTaggerActions.TabConstraints.tabTitle"), panelJPhotoTaggerActions); // NOI18N

        panelUserScripts.setLayout(new java.awt.GridBagLayout());

        labelGeneralInfo.setText(bundle.getString("SettingsPanel.labelGeneralInfo.text")); // NOI18N
        labelGeneralInfo.setLineWrap(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelUserScripts.add(labelGeneralInfo, gridBagConstraints);

        panelFileCopied.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsPanel.panelFileCopied.border.title"))); // NOI18N
        panelFileCopied.setLayout(new java.awt.GridBagLayout());

        textFieldFileCopied.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileCopied.add(textFieldFileCopied, gridBagConstraints);

        buttonChooseScriptFileCopied.setText(bundle.getString("SettingsPanel.buttonChooseScriptFileCopied.text")); // NOI18N
        buttonChooseScriptFileCopied.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileCopiedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileCopied.add(buttonChooseScriptFileCopied, gridBagConstraints);

        buttonRemoveScriptFileCopied.setAction(removeScriptFileAction);
        buttonRemoveScriptFileCopied.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/fileeventhooks/delete.png"))); // NOI18N
        buttonRemoveScriptFileCopied.setToolTipText(bundle.getString("SettingsPanel.buttonRemoveScriptFileCopied.toolTipText")); // NOI18N
        buttonRemoveScriptFileCopied.setEnabled(false);
        buttonRemoveScriptFileCopied.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileCopied.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileCopied.add(buttonRemoveScriptFileCopied, gridBagConstraints);

        labelParameterInfoFileCopied.setText(bundle.getString("SettingsPanel.labelParameterInfoFileCopied.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFileCopied.add(labelParameterInfoFileCopied, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelUserScripts.add(panelFileCopied, gridBagConstraints);

        panelFileRenamed.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsPanel.panelFileRenamed.border.title"))); // NOI18N
        panelFileRenamed.setLayout(new java.awt.GridBagLayout());

        textFieldFileRenamed.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileRenamed.add(textFieldFileRenamed, gridBagConstraints);

        buttonChooseScriptFileRenamed.setText(bundle.getString("SettingsPanel.buttonChooseScriptFileRenamed.text")); // NOI18N
        buttonChooseScriptFileRenamed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileRenamedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileRenamed.add(buttonChooseScriptFileRenamed, gridBagConstraints);

        buttonRemoveScriptFileRenamed.setAction(removeScriptFileAction);
        buttonRemoveScriptFileRenamed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/fileeventhooks/delete.png"))); // NOI18N
        buttonRemoveScriptFileRenamed.setToolTipText(bundle.getString("SettingsPanel.buttonRemoveScriptFileRenamed.toolTipText")); // NOI18N
        buttonRemoveScriptFileRenamed.setEnabled(false);
        buttonRemoveScriptFileRenamed.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileRenamed.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileRenamed.add(buttonRemoveScriptFileRenamed, gridBagConstraints);

        labelParameterInfoFileRenamed.setText(bundle.getString("SettingsPanel.labelParameterInfoFileRenamed.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFileRenamed.add(labelParameterInfoFileRenamed, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelUserScripts.add(panelFileRenamed, gridBagConstraints);

        panelFileMoved.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsPanel.panelFileMoved.border.title"))); // NOI18N
        panelFileMoved.setLayout(new java.awt.GridBagLayout());

        textFieldFileMoved.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileMoved.add(textFieldFileMoved, gridBagConstraints);

        buttonChooseScriptFileMoved.setText(bundle.getString("SettingsPanel.buttonChooseScriptFileMoved.text")); // NOI18N
        buttonChooseScriptFileMoved.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileMovedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileMoved.add(buttonChooseScriptFileMoved, gridBagConstraints);

        buttonRemoveScriptFileMoved.setAction(removeScriptFileAction);
        buttonRemoveScriptFileMoved.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/fileeventhooks/delete.png"))); // NOI18N
        buttonRemoveScriptFileMoved.setToolTipText(bundle.getString("SettingsPanel.buttonRemoveScriptFileMoved.toolTipText")); // NOI18N
        buttonRemoveScriptFileMoved.setEnabled(false);
        buttonRemoveScriptFileMoved.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileMoved.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileMoved.add(buttonRemoveScriptFileMoved, gridBagConstraints);

        labelParameterInfoFileMoved.setText(bundle.getString("SettingsPanel.labelParameterInfoFileMoved.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFileMoved.add(labelParameterInfoFileMoved, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelUserScripts.add(panelFileMoved, gridBagConstraints);

        panelFileDeleted.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsPanel.panelFileDeleted.border.title"))); // NOI18N
        panelFileDeleted.setLayout(new java.awt.GridBagLayout());

        textFieldFileDeleted.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileDeleted.add(textFieldFileDeleted, gridBagConstraints);

        buttonChooseScriptFileDeleted.setText(bundle.getString("SettingsPanel.buttonChooseScriptFileDeleted.text")); // NOI18N
        buttonChooseScriptFileDeleted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileDeletedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileDeleted.add(buttonChooseScriptFileDeleted, gridBagConstraints);

        buttonRemoveScriptFileDeleted.setAction(removeScriptFileAction);
        buttonRemoveScriptFileDeleted.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/fileeventhooks/delete.png"))); // NOI18N
        buttonRemoveScriptFileDeleted.setToolTipText(bundle.getString("SettingsPanel.buttonRemoveScriptFileDeleted.toolTipText")); // NOI18N
        buttonRemoveScriptFileDeleted.setEnabled(false);
        buttonRemoveScriptFileDeleted.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileDeleted.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileDeleted.add(buttonRemoveScriptFileDeleted, gridBagConstraints);

        labelParameterInfoFileDeleted.setText(bundle.getString("SettingsPanel.labelParameterInfoFileDeleted.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFileDeleted.add(labelParameterInfoFileDeleted, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelUserScripts.add(panelFileDeleted, gridBagConstraints);

        labelPathInfo.setText(bundle.getString("SettingsPanel.labelPathInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelUserScripts.add(labelPathInfo, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("SettingsPanel.panelUserScripts.TabConstraints.tabTitle"), panelUserScripts); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);

        bindingGroup.bind();
    }//GEN-END:initComponents

    private void buttonChooseScriptFileCopiedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseScriptFileCopiedActionPerformed
        chooseScript(textFieldFileCopied);
    }//GEN-LAST:event_buttonChooseScriptFileCopiedActionPerformed

    private void buttonChooseScriptFileRenamedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseScriptFileRenamedActionPerformed
        chooseScript(textFieldFileRenamed);
    }//GEN-LAST:event_buttonChooseScriptFileRenamedActionPerformed

    private void buttonChooseScriptFileMovedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseScriptFileMovedActionPerformed
        chooseScript(textFieldFileMoved);
    }//GEN-LAST:event_buttonChooseScriptFileMovedActionPerformed

    private void buttonChooseScriptFileDeletedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseScriptFileDeletedActionPerformed
        chooseScript(textFieldFileDeleted);
    }//GEN-LAST:event_buttonChooseScriptFileDeletedActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        setScriptsToTextFields();
    }//GEN-LAST:event_formComponentShown

    private void textFieldFilenameSuffixKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFilenameSuffixKeyPressed
        filenameSuffixTyped(evt);
    }//GEN-LAST:event_textFieldFilenameSuffixKeyPressed

    private void buttonAddFilenameSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddFilenameSuffixActionPerformed
        addFilenameSuffix();
        textFieldFilenameSuffix.requestFocusInWindow();
    }//GEN-LAST:event_buttonAddFilenameSuffixActionPerformed

    private void buttonRemoveSelectedFilenameSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSelectedFilenameSuffixActionPerformed
        removeSelectedFilenameSuffix();
        textFieldFilenameSuffix.requestFocusInWindow();
    }//GEN-LAST:event_buttonRemoveSelectedFilenameSuffixActionPerformed

    private void buttonRenameSelectedFilenameSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameSelectedFilenameSuffixActionPerformed
        renameSelectedFilenameSuffix();
    }//GEN-LAST:event_buttonRenameSelectedFilenameSuffixActionPerformed

    private void listFilenameSuffixesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listFilenameSuffixesKeyPressed
        int keyCode = evt.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_DELETE:
                removeSelectedFilenameSuffix();
                listFilenameSuffixes.requestFocusInWindow();
                break;
            case KeyEvent.VK_F2:
                renameSelectedFilenameSuffix();
                listFilenameSuffixes.requestFocusInWindow();
                break;
            default: // Do nothing
        }
    }//GEN-LAST:event_listFilenameSuffixesKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddFilenameSuffix;
    private javax.swing.JButton buttonChooseScriptFileCopied;
    private javax.swing.JButton buttonChooseScriptFileDeleted;
    private javax.swing.JButton buttonChooseScriptFileMoved;
    private javax.swing.JButton buttonChooseScriptFileRenamed;
    private javax.swing.JButton buttonRemoveScriptFileCopied;
    private javax.swing.JButton buttonRemoveScriptFileDeleted;
    private javax.swing.JButton buttonRemoveScriptFileMoved;
    private javax.swing.JButton buttonRemoveScriptFileRenamed;
    private javax.swing.JButton buttonRemoveSelectedFilenameSuffix;
    private javax.swing.JButton buttonRenameSelectedFilenameSuffix;
    private javax.swing.JLabel labelAttentionJptActions;
    private javax.swing.JLabel labelFilenameSuffix;
    private javax.swing.JLabel labelFilenameSuffixExample;
    private org.jdesktop.swingx.JXLabel labelGeneralInfo;
    private org.jdesktop.swingx.JXLabel labelInfoJPhotoTaggerFilenameSuffixes;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileCopied;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileDeleted;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileMoved;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileRenamed;
    private org.jdesktop.swingx.JXLabel labelPathInfo;
    private org.jdesktop.swingx.JXList listFilenameSuffixes;
    private javax.swing.JPanel panelButtonsFilenameSuffixes;
    private javax.swing.JPanel panelEditFilenameSuffix;
    private javax.swing.JPanel panelFileCopied;
    private javax.swing.JPanel panelFileDeleted;
    private javax.swing.JPanel panelFileMoved;
    private javax.swing.JPanel panelFileRenamed;
    private javax.swing.JPanel panelJPhotoTaggerActions;
    private javax.swing.JPanel panelListFilenameSuffixes;
    private javax.swing.JPanel panelSuffixes;
    private javax.swing.JPanel panelUserScripts;
    private javax.swing.JScrollPane scrollPaneListFilenameSuffixes;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField textFieldFileCopied;
    private javax.swing.JTextField textFieldFileDeleted;
    private javax.swing.JTextField textFieldFileMoved;
    private javax.swing.JTextField textFieldFileRenamed;
    private javax.swing.JTextField textFieldFilenameSuffix;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
