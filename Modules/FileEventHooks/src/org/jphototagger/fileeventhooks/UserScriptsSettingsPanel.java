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
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class UserScriptsSettingsPanel extends javax.swing.JPanel {

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

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/fileeventhooks/Bundle"); // NOI18N
        labelGeneralInfo.setText(bundle.getString("UserScriptsSettingsPanel.labelGeneralInfo.text")); // NOI18N
        labelGeneralInfo.setLineWrap(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        add(labelGeneralInfo, gridBagConstraints);

        panelFileCopied.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("UserScriptsSettingsPanel.panelFileCopied.border.title"))); // NOI18N
        panelFileCopied.setLayout(new java.awt.GridBagLayout());

        textFieldFileCopied.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileCopied.add(textFieldFileCopied, gridBagConstraints);

        buttonChooseScriptFileCopied.setText(bundle.getString("UserScriptsSettingsPanel.buttonChooseScriptFileCopied.text")); // NOI18N
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
        buttonRemoveScriptFileCopied.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileCopied.setToolTipText(bundle.getString("UserScriptsSettingsPanel.buttonRemoveScriptFileCopied.toolTipText")); // NOI18N
        buttonRemoveScriptFileCopied.setEnabled(false);
        buttonRemoveScriptFileCopied.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileCopied.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileCopied.add(buttonRemoveScriptFileCopied, gridBagConstraints);

        labelParameterInfoFileCopied.setText(bundle.getString("UserScriptsSettingsPanel.labelParameterInfoFileCopied.text")); // NOI18N
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
        add(panelFileCopied, gridBagConstraints);

        panelFileRenamed.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("UserScriptsSettingsPanel.panelFileRenamed.border.title"))); // NOI18N
        panelFileRenamed.setLayout(new java.awt.GridBagLayout());

        textFieldFileRenamed.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileRenamed.add(textFieldFileRenamed, gridBagConstraints);

        buttonChooseScriptFileRenamed.setText(bundle.getString("UserScriptsSettingsPanel.buttonChooseScriptFileRenamed.text")); // NOI18N
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
        buttonRemoveScriptFileRenamed.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileRenamed.setToolTipText(bundle.getString("UserScriptsSettingsPanel.buttonRemoveScriptFileRenamed.toolTipText")); // NOI18N
        buttonRemoveScriptFileRenamed.setEnabled(false);
        buttonRemoveScriptFileRenamed.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileRenamed.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileRenamed.add(buttonRemoveScriptFileRenamed, gridBagConstraints);

        labelParameterInfoFileRenamed.setText(bundle.getString("UserScriptsSettingsPanel.labelParameterInfoFileRenamed.text")); // NOI18N
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
        add(panelFileRenamed, gridBagConstraints);

        panelFileMoved.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("UserScriptsSettingsPanel.panelFileMoved.border.title"))); // NOI18N
        panelFileMoved.setLayout(new java.awt.GridBagLayout());

        textFieldFileMoved.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileMoved.add(textFieldFileMoved, gridBagConstraints);

        buttonChooseScriptFileMoved.setText(bundle.getString("UserScriptsSettingsPanel.buttonChooseScriptFileMoved.text")); // NOI18N
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
        buttonRemoveScriptFileMoved.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileMoved.setToolTipText(bundle.getString("UserScriptsSettingsPanel.buttonRemoveScriptFileMoved.toolTipText")); // NOI18N
        buttonRemoveScriptFileMoved.setEnabled(false);
        buttonRemoveScriptFileMoved.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileMoved.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileMoved.add(buttonRemoveScriptFileMoved, gridBagConstraints);

        labelParameterInfoFileMoved.setText(bundle.getString("UserScriptsSettingsPanel.labelParameterInfoFileMoved.text")); // NOI18N
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
        add(panelFileMoved, gridBagConstraints);

        panelFileDeleted.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("UserScriptsSettingsPanel.panelFileDeleted.border.title"))); // NOI18N
        panelFileDeleted.setLayout(new java.awt.GridBagLayout());

        textFieldFileDeleted.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelFileDeleted.add(textFieldFileDeleted, gridBagConstraints);

        buttonChooseScriptFileDeleted.setText(bundle.getString("UserScriptsSettingsPanel.buttonChooseScriptFileDeleted.text")); // NOI18N
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
        buttonRemoveScriptFileDeleted.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png"));
        buttonRemoveScriptFileDeleted.setToolTipText(bundle.getString("UserScriptsSettingsPanel.buttonRemoveScriptFileDeleted.toolTipText")); // NOI18N
        buttonRemoveScriptFileDeleted.setEnabled(false);
        buttonRemoveScriptFileDeleted.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveScriptFileDeleted.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFileDeleted.add(buttonRemoveScriptFileDeleted, gridBagConstraints);

        labelParameterInfoFileDeleted.setText(bundle.getString("UserScriptsSettingsPanel.labelParameterInfoFileDeleted.text")); // NOI18N
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
        add(panelFileDeleted, gridBagConstraints);

        labelPathInfo.setText(bundle.getString("UserScriptsSettingsPanel.labelPathInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(labelPathInfo, gridBagConstraints);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
}
