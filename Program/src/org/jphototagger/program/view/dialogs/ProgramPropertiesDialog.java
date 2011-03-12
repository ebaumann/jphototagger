package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.component.TabOrEnterLeavingTextArea;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.image.util.IconUtil;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JFileChooser;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.LookAndFeelUtil;
import org.jphototagger.lib.util.Settings;

/**
 * Modal Dialog to change or define the properties of a program which can
 * be started within the application.
 *
 * @author Elmar Baumann
 */
public final class ProgramPropertiesDialog extends Dialog {
    private static final long serialVersionUID = 5953007101307866505L;
    private transient Program program = new Program();
    private static final String KEY_LAST_DIR = "ProgramPropertiesDialog.LastDirectory";
    private static final String KEY_EXPERT_SETTINGS = "ProgramPropertiesDialog.ExpertSettings";
    private static final String BUTTON_TEXT_TOGGLE_TO_EXPERT_SETTINGS = JptBundle.INSTANCE.getString("ProgramPropertiesDialog.ButtonText.ExpertSettings");
    private static final String BUTTON_TEXT_TOGGLE_TO_SIMPLE_SETTINGS = JptBundle.INSTANCE.getString("ProgramPropertiesDialog.ButtonText.SimpleSettings");
    private static final Settings SETTINGS = UserSettings.INSTANCE.getSettings();
    private File lastDir = new File(SETTINGS.getString(KEY_LAST_DIR));
    private File file;
    private boolean accecpted = false;
    private boolean action;
    private static final Color FG_COLOR_LABEL_FILE_EXISTS = Color.BLUE;
    private static final Color BG_COLOR_LABEL_FILE_EXISTS = LookAndFeelUtil.getUiColor("Label.background");
    private static final Color FG_COLOR_LABEL_FILE_NOT_EXISTS = Color.WHITE;
    private static final Color BG_COLOR_LABEL_FILE_NOT_EXISTS = Color.RED;

    public ProgramPropertiesDialog(boolean action) {
        super(GUI.getAppFrame(), true, SETTINGS, null);
        this.action = action;
        program.setAction(action);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        if (action) {
            setActionTexts();
        } else {
            getContentPane().remove(checkBoxInputBeforeExecute);
        }

        setIgnoreSizeAndLocation(true);
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
            labelErrorFileDoesNotExist.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.LabelErrorFileDoesNotExist.ErrorText"));
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
        setTitle(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.title.Action"));
        labelFilePrompt.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelFilePrompt.text.Action"));
        labelAlias.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelAlias.text.Action"));
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
            MessageDisplayer.error(this, "ProgramPropertiesDialog.Error.MissingData");
        }
    }

    private boolean inputsValid() {
        return (file != null) && file.exists() &&!file.isDirectory()
               &&!textFieldAlias.getText().trim().isEmpty();
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

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();

            storeLastDir(f);

            if (f.exists() &&!f.isDirectory()) {
                file = f;
                labelFile.setText(file.getAbsolutePath());
                showFileExists(true);
                setProgramIcon();
            } else {
                MessageDisplayer.error(
                    this, "ProgramPropertiesDialog.Error.ChooseFile");
            }
        }

        setEnabledButtonOk();
    }

    private void storeLastDir(File f) {
        File dir = f.getParentFile();

        if ((dir == null) ||!dir.isDirectory()) {
            return;
        }

        SETTINGS.set(dir.getAbsolutePath(), KEY_LAST_DIR);
        UserSettings.INSTANCE.writeToFile();
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
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        help(JptBundle.INSTANCE.getString("Help.Url.Contents.ParameterSubstitution"));
    }

    private void toggleExpertSettings() {
        boolean isExpertSettings = toggleButtonExpertSettings.isSelected();

        if (isExpertSettings) {
            addExpertSettingsPanel();
        } else {
            removeExpertSettings();
        }

        SETTINGS.set(isExpertSettings, KEY_EXPERT_SETTINGS);
        UserSettings.INSTANCE.writeToFile();
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
        gbc.insets = new Insets(5, 5, 0, 5);
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
        boolean isExpertSettings = SETTINGS.getBoolean(KEY_EXPERT_SETTINGS);

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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupSingleFileProcessing = new javax.swing.ButtonGroup();
        panelExpertSettings = new javax.swing.JPanel();
        checkBoxChangeFile = new javax.swing.JCheckBox();
        panelParameter = new javax.swing.JPanel();
        labelParametersBeforeFilename = new javax.swing.JLabel();
        scrollPaneParametersBeforeFilename = new javax.swing.JScrollPane();
        textAreaParametersBeforeFilename = new TabOrEnterLeavingTextArea();
        labelParametersAfterFilename = new javax.swing.JLabel();
        scrollPaneParametersAfterFilename = new javax.swing.JScrollPane();
        textAreaParametersAfterFilename = new TabOrEnterLeavingTextArea();
        checkBoxUsePattern = new javax.swing.JCheckBox();
        buttonInfoUsePattern = new javax.swing.JButton();
        scrollPaneUsePattern = new javax.swing.JScrollPane();
        textAreaUsePattern = new TabOrEnterLeavingTextArea();
        panelInputBeforeExecute = new javax.swing.JPanel();
        checkBoxInputBeforeExecute = new javax.swing.JCheckBox();
        checkBoxInputBeforeExecutePerFile = new javax.swing.JCheckBox();
        panelMultipleSelection = new javax.swing.JPanel();
        radioButtonSingleFileProcessingYes = new javax.swing.JRadioButton();
        radioButtonSingleFileProcessingNo = new javax.swing.JRadioButton();
        panelProgram = new javax.swing.JPanel();
        labelFilePrompt = new javax.swing.JLabel();
        labelErrorFileDoesNotExist = new javax.swing.JLabel();
        buttonChooseFile = new javax.swing.JButton();
        labelFile = new javax.swing.JLabel();
        labelAlias = new javax.swing.JLabel();
        textFieldAlias = new javax.swing.JTextField();
        toggleButtonExpertSettings = new javax.swing.JToggleButton();
        labelInfoRequiredInputs = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        panelExpertSettings.setName("panelExpertSettings"); // NOI18N
        panelExpertSettings.setLayout(new java.awt.GridBagLayout());

        checkBoxChangeFile.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.checkBoxChangeFile.text")); // NOI18N
        checkBoxChangeFile.setToolTipText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.checkBoxChangeFile.toolTipText")); // NOI18N
        checkBoxChangeFile.setName("checkBoxChangeFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelExpertSettings.add(checkBoxChangeFile, gridBagConstraints);

        panelParameter.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.panelParameter.border.title"))); // NOI18N
        panelParameter.setName("panelParameter"); // NOI18N
        panelParameter.setLayout(new java.awt.GridBagLayout());

        labelParametersBeforeFilename.setLabelFor(textAreaParametersBeforeFilename);
        labelParametersBeforeFilename.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelParametersBeforeFilename.text")); // NOI18N
        labelParametersBeforeFilename.setName("labelParametersBeforeFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelParameter.add(scrollPaneParametersBeforeFilename, gridBagConstraints);

        labelParametersAfterFilename.setLabelFor(textAreaParametersAfterFilename);
        labelParametersAfterFilename.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelParametersAfterFilename.text")); // NOI18N
        labelParametersAfterFilename.setName("labelParametersAfterFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelParameter.add(scrollPaneParametersAfterFilename, gridBagConstraints);

        checkBoxUsePattern.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.checkBoxUsePattern.text")); // NOI18N
        checkBoxUsePattern.setName("checkBoxUsePattern"); // NOI18N
        checkBoxUsePattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUsePatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelParameter.add(checkBoxUsePattern, gridBagConstraints);

        buttonInfoUsePattern.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.buttonInfoUsePattern.text")); // NOI18N
        buttonInfoUsePattern.setName("buttonInfoUsePattern"); // NOI18N
        buttonInfoUsePattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInfoUsePatternActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelParameter.add(scrollPaneUsePattern, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelExpertSettings.add(panelParameter, gridBagConstraints);

        panelInputBeforeExecute.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.panelInputBeforeExecute.border.title"))); // NOI18N
        panelInputBeforeExecute.setName("panelInputBeforeExecute"); // NOI18N
        panelInputBeforeExecute.setLayout(new java.awt.GridBagLayout());

        checkBoxInputBeforeExecute.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.checkBoxInputBeforeExecute.text")); // NOI18N
        checkBoxInputBeforeExecute.setName("checkBoxInputBeforeExecute"); // NOI18N
        checkBoxInputBeforeExecute.addActionListener(new java.awt.event.ActionListener() {
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelInputBeforeExecute.add(checkBoxInputBeforeExecute, gridBagConstraints);

        checkBoxInputBeforeExecutePerFile.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.checkBoxInputBeforeExecutePerFile.text")); // NOI18N
        checkBoxInputBeforeExecutePerFile.setEnabled(false);
        checkBoxInputBeforeExecutePerFile.setName("checkBoxInputBeforeExecutePerFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelInputBeforeExecute.add(checkBoxInputBeforeExecutePerFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelExpertSettings.add(panelInputBeforeExecute, gridBagConstraints);

        panelMultipleSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.panelSingleFileProcessing.border.title"))); // NOI18N
        panelMultipleSelection.setName("panelMultipleSelection"); // NOI18N
        panelMultipleSelection.setLayout(new java.awt.GridBagLayout());

        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingYes);
        radioButtonSingleFileProcessingYes.setSelected(true);
        radioButtonSingleFileProcessingYes.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.radioButtonSingleFileProcessingYes.text")); // NOI18N
        radioButtonSingleFileProcessingYes.setName("radioButtonSingleFileProcessingYes"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelMultipleSelection.add(radioButtonSingleFileProcessingYes, gridBagConstraints);

        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingNo);
        radioButtonSingleFileProcessingNo.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.radioButtonSingleFileProcessingNo.text")); // NOI18N
        radioButtonSingleFileProcessingNo.setName("radioButtonSingleFileProcessingNo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelMultipleSelection.add(radioButtonSingleFileProcessingNo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelExpertSettings.add(panelMultipleSelection, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelProgram.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.panelProgram.border.title"))); // NOI18N
        panelProgram.setName("panelProgram"); // NOI18N

        labelFilePrompt.setForeground(new java.awt.Color(255, 0, 0));
        labelFilePrompt.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelFilePrompt.text")); // NOI18N
        labelFilePrompt.setName("labelFilePrompt"); // NOI18N

        labelErrorFileDoesNotExist.setForeground(new java.awt.Color(255, 0, 0));
        labelErrorFileDoesNotExist.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelErrorFileDoesNotExist.text")); // NOI18N
        labelErrorFileDoesNotExist.setName("labelErrorFileDoesNotExist"); // NOI18N

        buttonChooseFile.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.setName("buttonChooseFile"); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        labelFile.setForeground(new java.awt.Color(0, 0, 255));
        labelFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFile.setName("labelFile"); // NOI18N
        labelFile.setOpaque(true);

        labelAlias.setForeground(new java.awt.Color(255, 0, 0));
        labelAlias.setLabelFor(textFieldAlias);
        labelAlias.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelAlias.text")); // NOI18N
        labelAlias.setName("labelAlias"); // NOI18N

        textFieldAlias.setName("textFieldAlias"); // NOI18N
        textFieldAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldAliasKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout panelProgramLayout = new javax.swing.GroupLayout(panelProgram);
        panelProgram.setLayout(panelProgramLayout);
        panelProgramLayout.setHorizontalGroup(
            panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProgramLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProgramLayout.createSequentialGroup()
                        .addComponent(labelFilePrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelErrorFileDoesNotExist, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addGroup(panelProgramLayout.createSequentialGroup()
                        .addComponent(labelAlias)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldAlias, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                    .addComponent(labelFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addComponent(buttonChooseFile)
                .addContainerGap())
        );
        panelProgramLayout.setVerticalGroup(
            panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProgramLayout.createSequentialGroup()
                .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelProgramLayout.createSequentialGroup()
                        .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelErrorFileDoesNotExist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFilePrompt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelFile, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonChooseFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAlias))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(panelProgram, gridBagConstraints);

        toggleButtonExpertSettings.setText(BUTTON_TEXT_TOGGLE_TO_EXPERT_SETTINGS);
        toggleButtonExpertSettings.setName("toggleButtonExpertSettings"); // NOI18N
        toggleButtonExpertSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonExpertSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
        getContentPane().add(toggleButtonExpertSettings, gridBagConstraints);

        labelInfoRequiredInputs.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoRequiredInputs.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.labelInfoRequiredInputs.text")); // NOI18N
        labelInfoRequiredInputs.setName("labelInfoRequiredInputs"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 0);
        getContentPane().add(labelInfoRequiredInputs, gridBagConstraints);

        buttonCancel.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 0);
        getContentPane().add(buttonCancel, gridBagConstraints);

        buttonOk.setText(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        getContentPane().add(buttonOk, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProgramPropertiesDialog dialog =
                    new ProgramPropertiesDialog(true);

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

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
    private javax.swing.JPanel panelExpertSettings;
    private javax.swing.JPanel panelInputBeforeExecute;
    private javax.swing.JPanel panelMultipleSelection;
    private javax.swing.JPanel panelParameter;
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
