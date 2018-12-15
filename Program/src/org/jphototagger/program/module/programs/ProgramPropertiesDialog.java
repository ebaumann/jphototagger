package org.jphototagger.program.module.programs;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.TabOrEnterLeavingTextArea;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.LookAndFeelUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Modal Dialog to change or define the properties of a program which can
 * be started within the application.
 *
 * @author Elmar Baumann
 */
public final class ProgramPropertiesDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private transient Program program = new Program();
    private static final String KEY_LAST_DIR = "ProgramPropertiesDialog.LastDirectory";
    private static final String KEY_EXPERT_SETTINGS = "ProgramPropertiesDialog.ExpertSettings";
    private static final String BUTTON_TEXT_TOGGLE_TO_EXPERT_SETTINGS = Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.ButtonText.ExpertSettings");
    private static final String BUTTON_TEXT_TOGGLE_TO_SIMPLE_SETTINGS = Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.ButtonText.SimpleSettings");
    private static final Preferences PREFS = Lookup.getDefault().lookup(Preferences.class);
    private File lastDir = initCreateLastDir();
    private File file;
    private boolean accecpted = false;
    private final boolean action;
    private static final Color FG_COLOR_LABEL_FILE_EXISTS = Color.BLUE;
    private static final Color BG_COLOR_LABEL_FILE_EXISTS = LookAndFeelUtil.getUiColor("Label.background");
    private static final Color FG_COLOR_LABEL_FILE_NOT_EXISTS = Color.WHITE;
    private static final Color BG_COLOR_LABEL_FILE_NOT_EXISTS = Color.RED;

    public ProgramPropertiesDialog(boolean action) {
        super(GUI.getAppFrame(), true);
        this.action = action;
        program.setAction(action);
        initComponents();
        postInitComponents();
    }

    private File initCreateLastDir() {
        String lastDirPath = PREFS.getString(KEY_LAST_DIR);
        if (!StringUtil.hasContent(lastDirPath)) {
            return new File(SystemUtil.getDefaultProgramDirPath());
        }
        return new File(lastDirPath);
    }

    private void postInitComponents() {
        if (action) {
            setActionTexts();
        } else {
            getContentPane().remove(checkBoxInputBeforeExecute);
        }
        setIgnorePersistedSizeAndLocation(true);
        MnemonicUtil.setMnemonics((Container) this);
        MnemonicUtil.setMnemonics(panelExpertSettings);
        listen();
    }

    private void listen() {
        textFieldAlias.getDocument().addDocumentListener(
            new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evt) {
                setEnabledButtonOk();
            }
            @Override
            public void removeUpdate(DocumentEvent evt) {
                setEnabledButtonOk();
            }
            @Override
            public void changedUpdate(DocumentEvent evt) {
                setEnabledButtonOk();
            }
        });
    }

    private void setEnabledButtonOk() {
        buttonOk.setEnabled(inputsValid());
    }

    private void showFileExists(boolean exists) {
        if (exists) {
            labelErrorFileDoesNotExist.setText("<html> </html>");
        } else {
            labelErrorFileDoesNotExist.setText(Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.LabelErrorFileDoesNotExist.ErrorText"));
        }
        labelFile.setForeground(exists
                                ? FG_COLOR_LABEL_FILE_EXISTS
                                : FG_COLOR_LABEL_FILE_NOT_EXISTS);
        labelFile.setBackground(exists
                                ? BG_COLOR_LABEL_FILE_EXISTS
                                : BG_COLOR_LABEL_FILE_NOT_EXISTS);
    }

    public void setProgram(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }
        this.program = program;
        file = program.getFile();
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        String parametersAfterFilename = program.getParametersAfterFilename();
        labelFile.setText(file.getAbsolutePath());
        textFieldAlias.setText(program.getAlias());
        textAreaParametersBeforeFilename.setText((parametersBeforeFilename
                == null)
                ? ""
                : parametersBeforeFilename);
        textAreaParametersAfterFilename.setText((parametersAfterFilename
                == null)
                ? ""
                : parametersAfterFilename);
        checkBoxInputBeforeExecute.setSelected(program.isInputBeforeExecute());
        checkBoxInputBeforeExecutePerFile.setSelected(program.isInputBeforeExecutePerFile());
        checkBoxInputBeforeExecutePerFile.setEnabled(program.isInputBeforeExecute());
        radioButtonSingleFileProcessingYes.setSelected(program.isSingleFileProcessing());
        radioButtonSingleFileProcessingNo.setSelected(!program.isSingleFileProcessing());
        checkBoxChangeFile.setSelected(program.isChangeFile());
        checkBoxUsePattern.setSelected(program.isUsePattern());
        String pattern = program.getPattern();
        textAreaUsePattern.setText((pattern == null)
                                   ? ""
                                   : pattern);
        setPatternStatus();
        setProgramIcon();
        showFileExists((file != null) && file.exists());
        setEnabledButtonOk();
    }

    private void setActionTexts() {
        setTitle(Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.title.Action"));
        labelFilePrompt.setText(Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.labelFilePrompt.text.Action"));
        labelAlias.setText(Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.labelAlias.text.Action"));
    }

    private void setProgramIcon() {
        if ((file != null) && file.exists()) {
            labelFile.setIcon(IconUtil.getSystemIcon(file));
        }
    }

    public Program getProgram() {
        return program;
    }

    public boolean isAccepted() {
        return accecpted;
    }

    private void accept() {
        if (inputsValid()) {
            String parametersBeforeFilename = textAreaParametersBeforeFilename.getText().trim();
            String parametersAfterFilename = textAreaParametersAfterFilename.getText().trim();
            String pattern = textAreaUsePattern.getText().trim();
            program.setAction(action);
            program.setFile(file);
            program.setAlias(textFieldAlias.getText().trim());
            program.setParametersBeforeFilename(
                parametersBeforeFilename.isEmpty()
                ? null
                : parametersBeforeFilename);
            program.setParametersAfterFilename(parametersAfterFilename.isEmpty()
                                               ? null
                                               : parametersAfterFilename);
            program.setInputBeforeExecute(checkBoxInputBeforeExecute.isSelected());
            program.setInputBeforeExecutePerFile(checkBoxInputBeforeExecutePerFile.isSelected());
            program.setSingleFileProcessing(radioButtonSingleFileProcessingYes.isSelected());
            program.setChangeFile(checkBoxChangeFile.isSelected());
            program.setUsePattern(checkBoxUsePattern.isSelected());
            program.setPattern(pattern.isEmpty()
                               ? null
                               : pattern);
            accecpted = true;
            setVisible(false);
        } else {
            String message = Bundle.getString(ProgramPropertiesDialog.class, "ProgramPropertiesDialog.Error.MissingData");
            MessageDisplayer.error(this, message);
        }
    }

    private boolean inputsValid() {
        return file != null && file.exists() && aliasDefined();
    }

    private boolean aliasDefined() {
        String alias = textFieldAlias.getText().trim();
        return !alias.isEmpty();
    }

    private void cancel() {
        accecpted = false;
        setVisible(false);
    }

    @Override
    protected void escape() {
        cancel();
    }

    private void chooseProgram() {
        JFileChooser fileChooser = new JFileChooser(lastDir);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            storeLastDirOfFile(file);
            labelFile.setText(file.getAbsolutePath());
            showFileExists(true);
            setProgramIcon();
            if (!StringUtil.hasContent(textFieldAlias.getText())) {
                textFieldAlias.setText(file.getName());
            }
            textFieldAlias.requestFocusInWindow();
        }
        setEnabledButtonOk();
    }

    private void storeLastDirOfFile(File file) {
        File dir = file.getParentFile();
        if ((dir == null) ||!dir.isDirectory()) {
            return;
        }
        PREFS.setString(KEY_LAST_DIR, dir.getAbsolutePath());
        lastDir = dir;
    }

    private void handleCheckBoxInputBeforeExecuteActionPerformed() {
        boolean selected = checkBoxInputBeforeExecute.isSelected();
        if (!selected && checkBoxInputBeforeExecutePerFile.isSelected()) {
            checkBoxInputBeforeExecutePerFile.setSelected(false);
        }
        checkBoxInputBeforeExecutePerFile.setEnabled(selected);
    }

    private void handleTextFieldAliasKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            accept();
        }
    }

    private void setPatternStatus() {
        boolean usePattern = checkBoxUsePattern.isSelected();
        textAreaParametersBeforeFilename.setEnabled(!usePattern);
        textAreaParametersAfterFilename.setEnabled(!usePattern);
        textAreaUsePattern.setEnabled(usePattern);
        if (usePattern && radioButtonSingleFileProcessingNo.isSelected()) {
            radioButtonSingleFileProcessingYes.setSelected(true);
        }
        if (usePattern && checkBoxInputBeforeExecute.isSelected()) {
            checkBoxInputBeforeExecute.setSelected(false);
            checkBoxInputBeforeExecutePerFile.setSelected(false);
            checkBoxInputBeforeExecutePerFile.setEnabled(false);
        }
        radioButtonSingleFileProcessingNo.setEnabled(!usePattern);
        checkBoxInputBeforeExecute.setEnabled(!usePattern);
    }

    private void handleCheckBoxUsePatternActionPerformed() {
        setPatternStatus();
    }

    private void showPatternHelp() {
        showHelp("/org/jphototagger/program/resource/doc/de/parameter_substitution.html");
    }

    private void toggleExpertSettings() {
        boolean isExpertSettings = toggleButtonExpertSettings.isSelected();
        if (isExpertSettings) {
            addExpertSettingsPanel();
        } else {
            removeExpertSettings();
        }
        PREFS.setBoolean(KEY_EXPERT_SETTINGS, isExpertSettings);
        pack();
        ComponentUtil.forceRepaint(this);
    }

    private void addExpertSettingsPanel() {
        getContentPane().add(panelExpertSettings, getExpertSettingsConstraints());
        toggleButtonExpertSettings.setText(BUTTON_TEXT_TOGGLE_TO_SIMPLE_SETTINGS);
        MnemonicUtil.setMnemonics(toggleButtonExpertSettings);
    }

    private void removeExpertSettings() {
        getContentPane().remove(panelExpertSettings);
        toggleButtonExpertSettings.setText(BUTTON_TEXT_TOGGLE_TO_EXPERT_SETTINGS);
        MnemonicUtil.setMnemonics(toggleButtonExpertSettings);
    }

    private GridBagConstraints getExpertSettingsConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            addExperSettingBasedOnUserSettings();
            pack();
            setLocationRelativeTo(null);
        }
        super.setVisible(visible);
    }

    private void addExperSettingBasedOnUserSettings() {
        boolean isExpertSettings = PREFS.getBoolean(KEY_EXPERT_SETTINGS);
        if (isExpertSettings) {
            addExpertSettingsPanel();
            toggleButtonExpertSettings.setSelected(true);
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

        buttonGroupSingleFileProcessing = new javax.swing.ButtonGroup();
        panelExpertSettings = org.jphototagger.resources.UiFactory.panel();
        checkBoxChangeFile = org.jphototagger.resources.UiFactory.checkBox();
        panelParameter = org.jphototagger.resources.UiFactory.panel();
        labelParametersBeforeFilename = org.jphototagger.resources.UiFactory.label();
        scrollPaneParametersBeforeFilename = org.jphototagger.resources.UiFactory.scrollPane();
        textAreaParametersBeforeFilename = new TabOrEnterLeavingTextArea();
        labelParametersAfterFilename = org.jphototagger.resources.UiFactory.label();
        scrollPaneParametersAfterFilename = org.jphototagger.resources.UiFactory.scrollPane();
        textAreaParametersAfterFilename = new TabOrEnterLeavingTextArea();
        checkBoxUsePattern = org.jphototagger.resources.UiFactory.checkBox();
        buttonInfoUsePattern = org.jphototagger.resources.UiFactory.button();
        scrollPaneUsePattern = org.jphototagger.resources.UiFactory.scrollPane();
        textAreaUsePattern = new TabOrEnterLeavingTextArea();
        panelInputBeforeExecute = org.jphototagger.resources.UiFactory.panel();
        checkBoxInputBeforeExecute = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxInputBeforeExecutePerFile = org.jphototagger.resources.UiFactory.checkBox();
        panelMultipleSelection = org.jphototagger.resources.UiFactory.panel();
        radioButtonSingleFileProcessingYes = UiFactory.radioButton();
        radioButtonSingleFileProcessingNo = UiFactory.radioButton();
        panelProgram = org.jphototagger.resources.UiFactory.panel();
        panelPrg = org.jphototagger.resources.UiFactory.panel();
        labelFilePrompt = org.jphototagger.resources.UiFactory.label();
        labelErrorFileDoesNotExist = org.jphototagger.resources.UiFactory.label();
        labelFile = org.jphototagger.resources.UiFactory.label();
        buttonChooseFile = org.jphototagger.resources.UiFactory.button();
        panelAlias = org.jphototagger.resources.UiFactory.panel();
        labelAlias = org.jphototagger.resources.UiFactory.label();
        textFieldAlias = org.jphototagger.resources.UiFactory.textField();
        toggleButtonExpertSettings = org.jphototagger.resources.UiFactory.toggleButton();
        labelInfoRequiredInputs = org.jphototagger.resources.UiFactory.label();
        buttonCancel = org.jphototagger.resources.UiFactory.button();
        buttonOk = org.jphototagger.resources.UiFactory.button();

        panelExpertSettings.setName("panelExpertSettings"); // NOI18N
        panelExpertSettings.setLayout(new java.awt.GridBagLayout());

        checkBoxChangeFile.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.checkBoxChangeFile.text")); // NOI18N
        checkBoxChangeFile.setToolTipText(Bundle.getString(getClass(), "ProgramPropertiesDialog.checkBoxChangeFile.toolTipText")); // NOI18N
        checkBoxChangeFile.setName("checkBoxChangeFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelExpertSettings.add(checkBoxChangeFile, gridBagConstraints);

        panelParameter.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ProgramPropertiesDialog.panelParameter.border.title"))); // NOI18N
        panelParameter.setName("panelParameter"); // NOI18N
        panelParameter.setLayout(new java.awt.GridBagLayout());

        labelParametersBeforeFilename.setLabelFor(textAreaParametersBeforeFilename);
        labelParametersBeforeFilename.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.labelParametersBeforeFilename.text")); // NOI18N
        labelParametersBeforeFilename.setName("labelParametersBeforeFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelParameter.add(labelParametersBeforeFilename, gridBagConstraints);

        scrollPaneParametersBeforeFilename.setName("scrollPaneParametersBeforeFilename"); // NOI18N

        textAreaParametersBeforeFilename.setColumns(20);
        textAreaParametersBeforeFilename.setLineWrap(true);
        textAreaParametersBeforeFilename.setRows(1);
        textAreaParametersBeforeFilename.setName("textAreaParametersBeforeFilename"); // NOI18N
        scrollPaneParametersBeforeFilename.setViewportView(textAreaParametersBeforeFilename);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelParameter.add(scrollPaneParametersBeforeFilename, gridBagConstraints);

        labelParametersAfterFilename.setLabelFor(textAreaParametersAfterFilename);
        labelParametersAfterFilename.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.labelParametersAfterFilename.text")); // NOI18N
        labelParametersAfterFilename.setName("labelParametersAfterFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelParameter.add(labelParametersAfterFilename, gridBagConstraints);

        scrollPaneParametersAfterFilename.setName("scrollPaneParametersAfterFilename"); // NOI18N

        textAreaParametersAfterFilename.setColumns(20);
        textAreaParametersAfterFilename.setLineWrap(true);
        textAreaParametersAfterFilename.setRows(1);
        textAreaParametersAfterFilename.setName("textAreaParametersAfterFilename"); // NOI18N
        scrollPaneParametersAfterFilename.setViewportView(textAreaParametersAfterFilename);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelParameter.add(scrollPaneParametersAfterFilename, gridBagConstraints);

        checkBoxUsePattern.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.checkBoxUsePattern.text")); // NOI18N
        checkBoxUsePattern.setName("checkBoxUsePattern"); // NOI18N
        checkBoxUsePattern.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUsePatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelParameter.add(checkBoxUsePattern, gridBagConstraints);

        buttonInfoUsePattern.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.buttonInfoUsePattern.text")); // NOI18N
        buttonInfoUsePattern.setName("buttonInfoUsePattern"); // NOI18N
        buttonInfoUsePattern.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInfoUsePatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelParameter.add(buttonInfoUsePattern, gridBagConstraints);

        scrollPaneUsePattern.setName("scrollPaneUsePattern"); // NOI18N

        textAreaUsePattern.setColumns(20);
        textAreaUsePattern.setLineWrap(true);
        textAreaUsePattern.setRows(1);
        textAreaUsePattern.setName("textAreaUsePattern"); // NOI18N
        scrollPaneUsePattern.setViewportView(textAreaUsePattern);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelParameter.add(scrollPaneUsePattern, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelExpertSettings.add(panelParameter, gridBagConstraints);

        panelInputBeforeExecute.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ProgramPropertiesDialog.panelInputBeforeExecute.border.title"))); // NOI18N
        panelInputBeforeExecute.setName("panelInputBeforeExecute"); // NOI18N
        panelInputBeforeExecute.setLayout(new java.awt.GridBagLayout());

        checkBoxInputBeforeExecute.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.checkBoxInputBeforeExecute.text")); // NOI18N
        checkBoxInputBeforeExecute.setName("checkBoxInputBeforeExecute"); // NOI18N
        checkBoxInputBeforeExecute.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxInputBeforeExecuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelInputBeforeExecute.add(checkBoxInputBeforeExecute, gridBagConstraints);

        checkBoxInputBeforeExecutePerFile.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.checkBoxInputBeforeExecutePerFile.text")); // NOI18N
        checkBoxInputBeforeExecutePerFile.setEnabled(false);
        checkBoxInputBeforeExecutePerFile.setName("checkBoxInputBeforeExecutePerFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelInputBeforeExecute.add(checkBoxInputBeforeExecutePerFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelExpertSettings.add(panelInputBeforeExecute, gridBagConstraints);

        panelMultipleSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ProgramPropertiesDialog.panelMultipleSelection.border.title"))); // NOI18N
        panelMultipleSelection.setName("panelMultipleSelection"); // NOI18N
        panelMultipleSelection.setLayout(new java.awt.GridBagLayout());

        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingYes);
        radioButtonSingleFileProcessingYes.setSelected(true);
        radioButtonSingleFileProcessingYes.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.radioButtonSingleFileProcessingYes.text")); // NOI18N
        radioButtonSingleFileProcessingYes.setName("radioButtonSingleFileProcessingYes"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelMultipleSelection.add(radioButtonSingleFileProcessingYes, gridBagConstraints);

        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingNo);
        radioButtonSingleFileProcessingNo.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.radioButtonSingleFileProcessingNo.text")); // NOI18N
        radioButtonSingleFileProcessingNo.setName("radioButtonSingleFileProcessingNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelMultipleSelection.add(radioButtonSingleFileProcessingNo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelExpertSettings.add(panelMultipleSelection, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ProgramPropertiesDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelProgram.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ProgramPropertiesDialog.panelProgram.border.title"))); // NOI18N
        panelProgram.setName("panelProgram"); // NOI18N
        panelProgram.setLayout(new java.awt.GridBagLayout());

        panelPrg.setName("panelPrg"); // NOI18N
        panelPrg.setLayout(new java.awt.GridBagLayout());

        labelFilePrompt.setForeground(new java.awt.Color(255, 0, 0));
        labelFilePrompt.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.labelFilePrompt.text")); // NOI18N
        labelFilePrompt.setName("labelFilePrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelPrg.add(labelFilePrompt, gridBagConstraints);

        labelErrorFileDoesNotExist.setForeground(new java.awt.Color(255, 0, 0));
        labelErrorFileDoesNotExist.setText("<html> </html>"); // NOI18N
        labelErrorFileDoesNotExist.setName("labelErrorFileDoesNotExist"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelPrg.add(labelErrorFileDoesNotExist, gridBagConstraints);

        labelFile.setForeground(new java.awt.Color(0, 0, 255));
        labelFile.setText(" "); // NOI18N
        labelFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFile.setName("labelFile"); // NOI18N
        labelFile.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelPrg.add(labelFile, gridBagConstraints);

        buttonChooseFile.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.setName("buttonChooseFile"); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        panelPrg.add(buttonChooseFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelProgram.add(panelPrg, gridBagConstraints);

        panelAlias.setName("panelAlias"); // NOI18N
        panelAlias.setLayout(new java.awt.GridBagLayout());

        labelAlias.setForeground(new java.awt.Color(255, 0, 0));
        labelAlias.setLabelFor(textFieldAlias);
        labelAlias.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.labelAlias.text")); // NOI18N
        labelAlias.setName("labelAlias"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelAlias.add(labelAlias, gridBagConstraints);

        textFieldAlias.setColumns(10);
        textFieldAlias.setName("textFieldAlias"); // NOI18N
        textFieldAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldAliasKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelAlias.add(textFieldAlias, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        panelProgram.add(panelAlias, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        getContentPane().add(panelProgram, gridBagConstraints);

        toggleButtonExpertSettings.setText(BUTTON_TEXT_TOGGLE_TO_EXPERT_SETTINGS);
        toggleButtonExpertSettings.setName("toggleButtonExpertSettings"); // NOI18N
        toggleButtonExpertSettings.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonExpertSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(8, 10, 0, 0);
        getContentPane().add(toggleButtonExpertSettings, gridBagConstraints);

        labelInfoRequiredInputs.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoRequiredInputs.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.labelInfoRequiredInputs.text")); // NOI18N
        labelInfoRequiredInputs.setName("labelInfoRequiredInputs"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 10, 10, 0);
        getContentPane().add(labelInfoRequiredInputs, gridBagConstraints);

        buttonCancel.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 10, 0);
        getContentPane().add(buttonCancel, gridBagConstraints);

        buttonOk.setText(Bundle.getString(getClass(), "ProgramPropertiesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 10, 10);
        getContentPane().add(buttonOk, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseFileActionPerformed
        chooseProgram();
    }//GEN-LAST:event_buttonChooseFileActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancel();
    }//GEN-LAST:event_formWindowClosing

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancel();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        accept();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void checkBoxInputBeforeExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxInputBeforeExecuteActionPerformed
        handleCheckBoxInputBeforeExecuteActionPerformed();
    }//GEN-LAST:event_checkBoxInputBeforeExecuteActionPerformed

    private void textFieldAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldAliasKeyPressed
        handleTextFieldAliasKeyPressed(evt);
    }//GEN-LAST:event_textFieldAliasKeyPressed

    private void checkBoxUsePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxUsePatternActionPerformed
        handleCheckBoxUsePatternActionPerformed();
    }//GEN-LAST:event_checkBoxUsePatternActionPerformed

    private void buttonInfoUsePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInfoUsePatternActionPerformed
        showPatternHelp();
    }//GEN-LAST:event_buttonInfoUsePatternActionPerformed

    private void toggleButtonExpertSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonExpertSettingsActionPerformed
        toggleExpertSettings();
    }//GEN-LAST:event_toggleButtonExpertSettingsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseFile;
    private javax.swing.ButtonGroup buttonGroupSingleFileProcessing;
    private javax.swing.JButton buttonInfoUsePattern;
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxChangeFile;
    private javax.swing.JCheckBox checkBoxInputBeforeExecute;
    private javax.swing.JCheckBox checkBoxInputBeforeExecutePerFile;
    private javax.swing.JCheckBox checkBoxUsePattern;
    private javax.swing.JLabel labelAlias;
    private javax.swing.JLabel labelErrorFileDoesNotExist;
    private javax.swing.JLabel labelFile;
    private javax.swing.JLabel labelFilePrompt;
    private javax.swing.JLabel labelInfoRequiredInputs;
    private javax.swing.JLabel labelParametersAfterFilename;
    private javax.swing.JLabel labelParametersBeforeFilename;
    private javax.swing.JPanel panelAlias;
    private javax.swing.JPanel panelExpertSettings;
    private javax.swing.JPanel panelInputBeforeExecute;
    private javax.swing.JPanel panelMultipleSelection;
    private javax.swing.JPanel panelParameter;
    private javax.swing.JPanel panelPrg;
    private javax.swing.JPanel panelProgram;
    private javax.swing.JRadioButton radioButtonSingleFileProcessingNo;
    private javax.swing.JRadioButton radioButtonSingleFileProcessingYes;
    private javax.swing.JScrollPane scrollPaneParametersAfterFilename;
    private javax.swing.JScrollPane scrollPaneParametersBeforeFilename;
    private javax.swing.JScrollPane scrollPaneUsePattern;
    private javax.swing.JTextArea textAreaParametersAfterFilename;
    private javax.swing.JTextArea textAreaParametersBeforeFilename;
    private javax.swing.JTextArea textAreaUsePattern;
    private javax.swing.JTextField textFieldAlias;
    private javax.swing.JToggleButton toggleButtonExpertSettings;
    // End of variables declaration//GEN-END:variables
}
