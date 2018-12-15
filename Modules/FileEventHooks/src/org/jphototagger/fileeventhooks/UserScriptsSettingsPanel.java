package org.jphototagger.fileeventhooks;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class UserScriptsSettingsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private static final Color TEXTFIELD_FOREGROUND = getTextFieldForeground();
    private static final String LAST_CHOOSEN_DIR_KEY = "Module.FileEventHooks.LastChoosenDir";
    private final Map<JTextField, String> keyOfTextField = new HashMap<>(4);
    private final Map<JButton, JTextField> textFieldOfRemoveButton = new HashMap<>(4);
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private String lastChoosenDirectory;

    public UserScriptsSettingsPanel() {
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
        if (prefs != null) {
            keyOfTextField.put(textFieldFileCopied, PreferencesKeys.FILE_COPIED_KEY);
            keyOfTextField.put(textFieldFileDeleted, PreferencesKeys.FILE_DELETED_KEY);
            keyOfTextField.put(textFieldFileMoved, PreferencesKeys.FILE_MOVED_KEY);
            keyOfTextField.put(textFieldFileRenamed, PreferencesKeys.FILE_RENAMED_KEY);
        }
    }

    private void initTextFieldOfRemoveButtonMap() {
        textFieldOfRemoveButton.put(buttonRemoveScriptFileCopied, textFieldFileCopied);
        textFieldOfRemoveButton.put(buttonRemoveScriptFileDeleted, textFieldFileDeleted);
        textFieldOfRemoveButton.put(buttonRemoveScriptFileMoved, textFieldFileMoved);
        textFieldOfRemoveButton.put(buttonRemoveScriptFileRenamed, textFieldFileRenamed);
    }

    private void initLastChoosenDirectory() {
        if (prefs != null) {
            String prefString = prefs.getString(LAST_CHOOSEN_DIR_KEY);
            lastChoosenDirectory = StringUtil.hasContent(prefString) ? prefString : "";
        }
    }

    private void setScriptsToTextFields() {
        if (prefs != null) {
            setScriptToTextField(prefs.getString(PreferencesKeys.FILE_COPIED_KEY), textFieldFileCopied);
            setScriptToTextField(prefs.getString(PreferencesKeys.FILE_DELETED_KEY), textFieldFileDeleted);
            setScriptToTextField(prefs.getString(PreferencesKeys.FILE_MOVED_KEY), textFieldFileMoved);
            setScriptToTextField(prefs.getString(PreferencesKeys.FILE_RENAMED_KEY), textFieldFileRenamed);
        }
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
        String key = keyOfTextField.get(textField);
        String scriptFilePath = scriptFile.getAbsolutePath();
        prefs.setString(key, scriptFilePath);
        textField.setText(scriptFilePath);
        setRemoveButtonsEnabled();
    }

    private File chooseScriptFile() {
        FileChooserProperties props = createFileChooserProperties();
        File scriptFile = FileChooserHelper.chooseFile(props);
        if (scriptFile != null) {
            String dirOfScriptFile = scriptFile.getParent();
            if (dirOfScriptFile != null) {
                lastChoosenDirectory = dirOfScriptFile;
                prefs.setString(LAST_CHOOSEN_DIR_KEY, lastChoosenDirectory);
            }
        }

        return scriptFile;
    }

    private FileChooserProperties createFileChooserProperties() {
        FileChooserProperties props = new FileChooserProperties();
        props.currentDirectoryPath(lastChoosenDirectory);
        props.dialogTitle(Bundle.getString(UserScriptsSettingsPanel.class, "SettingsPanel.FileChooser.Title"));
        props.multiSelectionEnabled(false);
        props.fileSelectionMode(JFileChooser.FILES_ONLY);
        props.propertyKeyPrefix("Module.FileEventHooks.FileChooser");
        return props;
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelGeneralInfo = UiFactory.jxLabel();
        panelFileCopied = UiFactory.panel();
        textFieldFileCopied = UiFactory.textField();
        buttonChooseScriptFileCopied = UiFactory.button();
        buttonRemoveScriptFileCopied = UiFactory.button();
        labelParameterInfoFileCopied = UiFactory.jxLabel();
        panelFileRenamed = UiFactory.panel();
        textFieldFileRenamed = UiFactory.textField();
        buttonChooseScriptFileRenamed = UiFactory.button();
        buttonRemoveScriptFileRenamed = UiFactory.button();
        labelParameterInfoFileRenamed = UiFactory.jxLabel();
        panelFileMoved = UiFactory.panel();
        textFieldFileMoved = UiFactory.textField();
        buttonChooseScriptFileMoved = UiFactory.button();
        buttonRemoveScriptFileMoved = UiFactory.button();
        labelParameterInfoFileMoved = UiFactory.jxLabel();
        panelFileDeleted = UiFactory.panel();
        textFieldFileDeleted = UiFactory.textField();
        buttonChooseScriptFileDeleted = UiFactory.button();
        buttonRemoveScriptFileDeleted = UiFactory.button();
        labelParameterInfoFileDeleted = UiFactory.jxLabel();
        labelPathInfo = UiFactory.jxLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        labelGeneralInfo.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.labelGeneralInfo.text")); // NOI18N
        labelGeneralInfo.setLineWrap(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(labelGeneralInfo, gridBagConstraints);

        panelFileCopied.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "UserScriptsSettingsPanel.panelFileCopied.border.title"))); // NOI18N
        panelFileCopied.setLayout(new java.awt.GridBagLayout());

        textFieldFileCopied.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileCopied.add(textFieldFileCopied, gridBagConstraints);

        buttonChooseScriptFileCopied.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonChooseScriptFileCopied.text")); // NOI18N
        buttonChooseScriptFileCopied.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileCopiedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileCopied.add(buttonChooseScriptFileCopied, gridBagConstraints);

        buttonRemoveScriptFileCopied.setAction(removeScriptFileAction);
        buttonRemoveScriptFileCopied.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileCopied.setToolTipText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonRemoveScriptFileCopied.toolTipText")); // NOI18N
        buttonRemoveScriptFileCopied.setEnabled(false);
        buttonRemoveScriptFileCopied.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonRemoveScriptFileCopied.setPreferredSize(UiFactory.dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFileCopied.add(buttonRemoveScriptFileCopied, gridBagConstraints);

        labelParameterInfoFileCopied.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.labelParameterInfoFileCopied.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelFileCopied.add(labelParameterInfoFileCopied, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(panelFileCopied, gridBagConstraints);

        panelFileRenamed.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "UserScriptsSettingsPanel.panelFileRenamed.border.title"))); // NOI18N
        panelFileRenamed.setLayout(new java.awt.GridBagLayout());

        textFieldFileRenamed.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileRenamed.add(textFieldFileRenamed, gridBagConstraints);

        buttonChooseScriptFileRenamed.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonChooseScriptFileRenamed.text")); // NOI18N
        buttonChooseScriptFileRenamed.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileRenamedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileRenamed.add(buttonChooseScriptFileRenamed, gridBagConstraints);

        buttonRemoveScriptFileRenamed.setAction(removeScriptFileAction);
        buttonRemoveScriptFileRenamed.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileRenamed.setToolTipText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonRemoveScriptFileRenamed.toolTipText")); // NOI18N
        buttonRemoveScriptFileRenamed.setEnabled(false);
        buttonRemoveScriptFileRenamed.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonRemoveScriptFileRenamed.setPreferredSize(UiFactory.dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFileRenamed.add(buttonRemoveScriptFileRenamed, gridBagConstraints);

        labelParameterInfoFileRenamed.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.labelParameterInfoFileRenamed.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelFileRenamed.add(labelParameterInfoFileRenamed, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(panelFileRenamed, gridBagConstraints);

        panelFileMoved.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "UserScriptsSettingsPanel.panelFileMoved.border.title"))); // NOI18N
        panelFileMoved.setLayout(new java.awt.GridBagLayout());

        textFieldFileMoved.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileMoved.add(textFieldFileMoved, gridBagConstraints);

        buttonChooseScriptFileMoved.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonChooseScriptFileMoved.text")); // NOI18N
        buttonChooseScriptFileMoved.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileMovedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileMoved.add(buttonChooseScriptFileMoved, gridBagConstraints);

        buttonRemoveScriptFileMoved.setAction(removeScriptFileAction);
        buttonRemoveScriptFileMoved.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileMoved.setToolTipText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonRemoveScriptFileMoved.toolTipText")); // NOI18N
        buttonRemoveScriptFileMoved.setEnabled(false);
        buttonRemoveScriptFileMoved.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonRemoveScriptFileMoved.setPreferredSize(UiFactory.dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFileMoved.add(buttonRemoveScriptFileMoved, gridBagConstraints);

        labelParameterInfoFileMoved.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.labelParameterInfoFileMoved.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelFileMoved.add(labelParameterInfoFileMoved, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(panelFileMoved, gridBagConstraints);

        panelFileDeleted.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "UserScriptsSettingsPanel.panelFileDeleted.border.title"))); // NOI18N
        panelFileDeleted.setLayout(new java.awt.GridBagLayout());

        textFieldFileDeleted.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileDeleted.add(textFieldFileDeleted, gridBagConstraints);

        buttonChooseScriptFileDeleted.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonChooseScriptFileDeleted.text")); // NOI18N
        buttonChooseScriptFileDeleted.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileDeletedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelFileDeleted.add(buttonChooseScriptFileDeleted, gridBagConstraints);

        buttonRemoveScriptFileDeleted.setAction(removeScriptFileAction);
        buttonRemoveScriptFileDeleted.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileDeleted.setToolTipText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.buttonRemoveScriptFileDeleted.toolTipText")); // NOI18N
        buttonRemoveScriptFileDeleted.setEnabled(false);
        buttonRemoveScriptFileDeleted.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonRemoveScriptFileDeleted.setPreferredSize(UiFactory.dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelFileDeleted.add(buttonRemoveScriptFileDeleted, gridBagConstraints);

        labelParameterInfoFileDeleted.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.labelParameterInfoFileDeleted.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelFileDeleted.add(labelParameterInfoFileDeleted, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(panelFileDeleted, gridBagConstraints);

        labelPathInfo.setText(Bundle.getString(getClass(), "UserScriptsSettingsPanel.labelPathInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        add(labelPathInfo, gridBagConstraints);
    }

    private void buttonChooseScriptFileCopiedActionPerformed(java.awt.event.ActionEvent evt) {
        chooseScript(textFieldFileCopied);
    }

    private void buttonChooseScriptFileRenamedActionPerformed(java.awt.event.ActionEvent evt) {
        chooseScript(textFieldFileRenamed);
    }

    private void buttonChooseScriptFileMovedActionPerformed(java.awt.event.ActionEvent evt) {
        chooseScript(textFieldFileMoved);
    }

    private void buttonChooseScriptFileDeletedActionPerformed(java.awt.event.ActionEvent evt) {
        chooseScript(textFieldFileDeleted);
    }

    private void formComponentShown(java.awt.event.ComponentEvent evt) {
        setScriptsToTextFields();
    }

    private javax.swing.JButton buttonChooseScriptFileCopied;
    private javax.swing.JButton buttonChooseScriptFileDeleted;
    private javax.swing.JButton buttonChooseScriptFileMoved;
    private javax.swing.JButton buttonChooseScriptFileRenamed;
    private javax.swing.JButton buttonRemoveScriptFileCopied;
    private javax.swing.JButton buttonRemoveScriptFileDeleted;
    private javax.swing.JButton buttonRemoveScriptFileMoved;
    private javax.swing.JButton buttonRemoveScriptFileRenamed;
    private org.jdesktop.swingx.JXLabel labelGeneralInfo;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileCopied;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileDeleted;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileMoved;
    private org.jdesktop.swingx.JXLabel labelParameterInfoFileRenamed;
    private org.jdesktop.swingx.JXLabel labelPathInfo;
    private javax.swing.JPanel panelFileCopied;
    private javax.swing.JPanel panelFileDeleted;
    private javax.swing.JPanel panelFileMoved;
    private javax.swing.JPanel panelFileRenamed;
    private javax.swing.JTextField textFieldFileCopied;
    private javax.swing.JTextField textFieldFileDeleted;
    private javax.swing.JTextField textFieldFileMoved;
    private javax.swing.JTextField textFieldFileRenamed;
}
