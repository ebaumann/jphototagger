/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.component.TabOrEnterLeavingTextArea;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.image.util.IconUtil;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JFileChooser;

/**
 * Modal Dialog to change or define the properties of a program which can
 * be started within the application.
 *
 * @author  Elmar Baumann
 * @version 2008-11-04
 */
public final class ProgramPropertiesDialog extends Dialog {
    private static final long   serialVersionUID = 5953007101307866505L;
    private transient Program   program          = new Program();
    private static final String KEY_LAST_DIR     =
        "ProgramPropertiesDialog.LastDirectory";
    private File lastDir =
        new File(UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIR));
    private File               file;
    private boolean            accecpted = false;
    private boolean            action;
    private static final Color FG_COLOR_LABEL_FILE_EXISTS = Color.BLUE;
    private static final Color BG_COLOR_LABEL_FILE_EXISTS =
        ComponentUtil.getUiColor("Label.background");
    private static final Color FG_COLOR_LABEL_FILE_NOT_EXISTS = Color.WHITE;
    private static final Color BG_COLOR_LABEL_FILE_NOT_EXISTS = Color.RED;

    public ProgramPropertiesDialog(boolean action) {
        super(GUI.INSTANCE.getAppFrame(), true,
              UserSettings.INSTANCE.getSettings(), null);
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

        MnemonicUtil.setMnemonics((Container) this);
        listen();
    }

    private void listen() {
        textFieldAlias.getDocument().addDocumentListener(
            new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setEnabledButtonOk();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                setEnabledButtonOk();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
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
            labelErrorFileDoesNotExist.setText(
                JptBundle.INSTANCE.getString(
                    "ProgramPropertiesDialog.LabelErrorFileDoesNotExist.ErrorText"));
        }

        labelFile.setForeground(exists
                                ? FG_COLOR_LABEL_FILE_EXISTS
                                : FG_COLOR_LABEL_FILE_NOT_EXISTS);
        labelFile.setBackground(exists
                                ? BG_COLOR_LABEL_FILE_EXISTS
                                : BG_COLOR_LABEL_FILE_NOT_EXISTS);
    }

    public void setProgram(Program program) {
        this.program = program;
        file         = program.getFile();

        String parametersBeforeFilename = program.getParametersBeforeFilename();
        String parametersAfterFilename  = program.getParametersAfterFilename();

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
        checkBoxInputBeforeExecutePerFile.setSelected(
            program.isInputBeforeExecutePerFile());
        checkBoxInputBeforeExecutePerFile.setEnabled(
            program.isInputBeforeExecute());
        radioButtonSingleFileProcessingYes.setSelected(
            program.isSingleFileProcessing());
        radioButtonSingleFileProcessingNo.setSelected(
            !program.isSingleFileProcessing());
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
        setTitle(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.title.Action"));
        labelFilePrompt.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelFilePrompt.text.Action"));
        labelAlias.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelAlias.text.Action"));
    }

    private void setProgramIcon() {
        if ((file != null) && file.exists()) {
            labelFile.setIcon(IconUtil.getSystemIcon(file));
        }
    }

    public Program getProgram() {
        return program;
    }

    public boolean accepted() {
        return accecpted;
    }

    private void accept() {
        if (inputsValid()) {
            String parametersBeforeFilename =
                textAreaParametersBeforeFilename.getText().trim();
            String parametersAfterFilename =
                textAreaParametersAfterFilename.getText().trim();
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
            program.setInputBeforeExecute(
                checkBoxInputBeforeExecute.isSelected());
            program.setInputBeforeExecutePerFile(
                checkBoxInputBeforeExecutePerFile.isSelected());
            program.setSingleFileProcessing(
                radioButtonSingleFileProcessingYes.isSelected());
            program.setChangeFile(checkBoxChangeFile.isSelected());
            program.setUsePattern(checkBoxUsePattern.isSelected());
            program.setPattern(pattern.isEmpty()
                               ? null
                               : pattern);
            accecpted = true;
            setVisible(false);
        } else {
            MessageDisplayer.error(this,
                                   "ProgramPropertiesDialog.Error.MissingData");
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

        UserSettings.INSTANCE.getSettings().set(dir.getAbsolutePath(),
                KEY_LAST_DIR);
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
        help(JptBundle.INSTANCE.getString(
            "Help.Url.Contents.ParameterSubstitution"));
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
        buttonGroupSingleFileProcessing    = new javax.swing.ButtonGroup();
        panelProgram                       = new javax.swing.JPanel();
        labelFilePrompt                    = new javax.swing.JLabel();
        labelErrorFileDoesNotExist         = new javax.swing.JLabel();
        buttonChooseFile                   = new javax.swing.JButton();
        labelFile                          = new javax.swing.JLabel();
        labelAlias                         = new javax.swing.JLabel();
        textFieldAlias                     = new javax.swing.JTextField();
        checkBoxChangeFile                 = new javax.swing.JCheckBox();
        panelParameter                     = new javax.swing.JPanel();
        labelParametersBeforeFilename      = new javax.swing.JLabel();
        scrollPaneParametersBeforeFilename = new javax.swing.JScrollPane();
        textAreaParametersBeforeFilename   = new TabOrEnterLeavingTextArea();
        labelParametersAfterFilename       = new javax.swing.JLabel();
        scrollPaneParametersAfterFilename  = new javax.swing.JScrollPane();
        textAreaParametersAfterFilename    = new TabOrEnterLeavingTextArea();
        checkBoxUsePattern                 = new javax.swing.JCheckBox();
        buttonInfoUsePattern               = new javax.swing.JButton();
        scrollPaneUsePattern               = new javax.swing.JScrollPane();
        textAreaUsePattern                 = new TabOrEnterLeavingTextArea();
        panelInputBeforeExecute            = new javax.swing.JPanel();
        checkBoxInputBeforeExecute         = new javax.swing.JCheckBox();
        checkBoxInputBeforeExecutePerFile  = new javax.swing.JCheckBox();
        panelMultipleSelection             = new javax.swing.JPanel();
        radioButtonSingleFileProcessingYes = new javax.swing.JRadioButton();
        radioButtonSingleFileProcessingNo  = new javax.swing.JRadioButton();
        labelInfoRequiredInputs            = new javax.swing.JLabel();
        buttonCancel                       = new javax.swing.JButton();
        buttonOk                           = new javax.swing.JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("ProgramPropertiesDialog.title"));    // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        panelProgram.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                JptBundle.INSTANCE.getString(
                    "ProgramPropertiesDialog.panelProgram.border.title")));    // NOI18N
        labelFilePrompt.setForeground(new java.awt.Color(255, 0, 0));
        labelFilePrompt.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelFilePrompt.text"));    // NOI18N
        labelErrorFileDoesNotExist.setForeground(new java.awt.Color(255, 0, 0));
        labelErrorFileDoesNotExist.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelErrorFileDoesNotExist.text"));    // NOI18N
        buttonChooseFile.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.buttonChooseFile.text"));    // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });
        labelFile.setForeground(new java.awt.Color(0, 0, 255));
        labelFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFile.setOpaque(true);
        labelAlias.setForeground(new java.awt.Color(255, 0, 0));
        labelAlias.setLabelFor(textFieldAlias);
        labelAlias.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelAlias.text"));    // NOI18N
        textFieldAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldAliasKeyPressed(evt);
            }
        });
        checkBoxChangeFile.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.checkBoxChangeFile.text"));    // NOI18N
        checkBoxChangeFile.setToolTipText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.checkBoxChangeFile.toolTipText"));    // NOI18N

        javax.swing.GroupLayout panelProgramLayout =
            new javax.swing.GroupLayout(panelProgram);

        panelProgram.setLayout(panelProgramLayout);
        panelProgramLayout.setHorizontalGroup(
            panelProgramLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panelProgramLayout.createSequentialGroup().addContainerGap().addGroup(
                    panelProgramLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelProgramLayout.createSequentialGroup().addComponent(
                            labelFilePrompt).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            labelErrorFileDoesNotExist,
                            javax.swing.GroupLayout.DEFAULT_SIZE, 296,
                            Short.MAX_VALUE).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                buttonChooseFile)).addComponent(
                                    labelFile,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 533,
                                    Short.MAX_VALUE).addGroup(
                                        panelProgramLayout.createSequentialGroup().addComponent(
                                            labelAlias).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                    textFieldAlias,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            416,
                                                            Short.MAX_VALUE)).addComponent(
                                                                checkBoxChangeFile)).addContainerGap()));
        panelProgramLayout.setVerticalGroup(
            panelProgramLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panelProgramLayout.createSequentialGroup().addGroup(
                    panelProgramLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        labelFilePrompt).addComponent(
                        buttonChooseFile).addComponent(
                        labelErrorFileDoesNotExist,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            labelFile, javax.swing.GroupLayout.PREFERRED_SIZE,
                            23,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                panelProgramLayout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                    labelAlias).addComponent(
                                    textFieldAlias,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        14, Short.MAX_VALUE).addComponent(
                                            checkBoxChangeFile).addContainerGap()));
        panelParameter.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                JptBundle.INSTANCE.getString(
                    "ProgramPropertiesDialog.panelParameter.border.title")));    // NOI18N
        labelParametersBeforeFilename.setLabelFor(
            textAreaParametersBeforeFilename);
        labelParametersBeforeFilename.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelParametersBeforeFilename.text"));    // NOI18N
        textAreaParametersBeforeFilename.setColumns(20);
        textAreaParametersBeforeFilename.setLineWrap(true);
        textAreaParametersBeforeFilename.setRows(1);
        scrollPaneParametersBeforeFilename.setViewportView(
            textAreaParametersBeforeFilename);
        labelParametersAfterFilename.setLabelFor(
            textAreaParametersAfterFilename);
        labelParametersAfterFilename.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelParametersAfterFilename.text"));    // NOI18N
        textAreaParametersAfterFilename.setColumns(20);
        textAreaParametersAfterFilename.setLineWrap(true);
        textAreaParametersAfterFilename.setRows(1);
        scrollPaneParametersAfterFilename.setViewportView(
            textAreaParametersAfterFilename);

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        checkBoxUsePattern.setText(
            bundle.getString(
                "ProgramPropertiesDialog.checkBoxUsePattern.text"));    // NOI18N
        checkBoxUsePattern.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUsePatternActionPerformed(evt);
            }
        });
        buttonInfoUsePattern.setText(
            bundle.getString(
                "ProgramPropertiesDialog.buttonInfoUsePattern.text"));    // NOI18N
        buttonInfoUsePattern.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInfoUsePatternActionPerformed(evt);
            }
        });
        textAreaUsePattern.setColumns(20);
        textAreaUsePattern.setLineWrap(true);
        textAreaUsePattern.setRows(1);
        scrollPaneUsePattern.setViewportView(textAreaUsePattern);

        javax.swing.GroupLayout panelParameterLayout =
            new javax.swing.GroupLayout(panelParameter);

        panelParameter.setLayout(panelParameterLayout);
        panelParameterLayout.setHorizontalGroup(
            panelParameterLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panelParameterLayout.createSequentialGroup().addContainerGap().addGroup(
                    panelParameterLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelParameterLayout.createSequentialGroup().addComponent(
                            checkBoxUsePattern).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            buttonInfoUsePattern)).addComponent(
                                scrollPaneParametersBeforeFilename,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 533,
                                Short.MAX_VALUE).addComponent(
                                    labelParametersBeforeFilename).addComponent(
                                    labelParametersAfterFilename).addComponent(
                                    scrollPaneParametersAfterFilename,
                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 533,
                                    Short.MAX_VALUE).addComponent(
                                        scrollPaneUsePattern,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        533,
                                        Short.MAX_VALUE)).addContainerGap()));
        panelParameterLayout.setVerticalGroup(
            panelParameterLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panelParameterLayout.createSequentialGroup().addComponent(
                    labelParametersBeforeFilename).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    scrollPaneParametersBeforeFilename,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 23,
                    Short.MAX_VALUE).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        labelParametersAfterFilename).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        scrollPaneParametersAfterFilename,
                        javax.swing.GroupLayout.DEFAULT_SIZE, 23,
                        Short.MAX_VALUE).addGap(8, 8, 8).addGroup(
                            panelParameterLayout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                checkBoxUsePattern).addComponent(
                                buttonInfoUsePattern)).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                    scrollPaneUsePattern,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 23,
                                    Short.MAX_VALUE).addContainerGap()));
        panelInputBeforeExecute.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                JptBundle.INSTANCE.getString(
                    "ProgramPropertiesDialog.panelInputBeforeExecute.border.title")));    // NOI18N
        checkBoxInputBeforeExecute.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.checkBoxInputBeforeExecute.text"));    // NOI18N
        checkBoxInputBeforeExecute.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxInputBeforeExecuteActionPerformed(evt);
            }
        });
        checkBoxInputBeforeExecutePerFile.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.checkBoxInputBeforeExecutePerFile.text"));    // NOI18N
        checkBoxInputBeforeExecutePerFile.setEnabled(false);

        javax.swing.GroupLayout panelInputBeforeExecuteLayout =
            new javax.swing.GroupLayout(panelInputBeforeExecute);

        panelInputBeforeExecute.setLayout(panelInputBeforeExecuteLayout);
        panelInputBeforeExecuteLayout
            .setHorizontalGroup(panelInputBeforeExecuteLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelInputBeforeExecuteLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelInputBeforeExecuteLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment
                            .LEADING).addComponent(checkBoxInputBeforeExecute)
                                .addComponent(checkBoxInputBeforeExecutePerFile))
                                    .addContainerGap(91, Short.MAX_VALUE)));
        panelInputBeforeExecuteLayout.linkSize(
            javax.swing.SwingConstants.HORIZONTAL,
            new java.awt.Component[] { checkBoxInputBeforeExecute,
                                       checkBoxInputBeforeExecutePerFile });
        panelInputBeforeExecuteLayout
            .setVerticalGroup(panelInputBeforeExecuteLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelInputBeforeExecuteLayout.createSequentialGroup()
                    .addComponent(checkBoxInputBeforeExecute)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement
                        .RELATED)
                            .addComponent(checkBoxInputBeforeExecutePerFile)
                            .addContainerGap(9, Short.MAX_VALUE)));
        panelMultipleSelection.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                JptBundle.INSTANCE.getString(
                    "ProgramPropertiesDialog.panelSingleFileProcessing.border.title")));    // NOI18N
        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingYes);
        radioButtonSingleFileProcessingYes.setSelected(true);
        radioButtonSingleFileProcessingYes.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.radioButtonSingleFileProcessingYes.text"));    // NOI18N
        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingNo);
        radioButtonSingleFileProcessingNo.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.radioButtonSingleFileProcessingNo.text"));    // NOI18N

        javax.swing.GroupLayout panelMultipleSelectionLayout =
            new javax.swing.GroupLayout(panelMultipleSelection);

        panelMultipleSelection.setLayout(panelMultipleSelectionLayout);
        panelMultipleSelectionLayout
            .setHorizontalGroup(panelMultipleSelectionLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelMultipleSelectionLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelMultipleSelectionLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment
                            .LEADING)
                                .addComponent(radioButtonSingleFileProcessingYes)
                                .addComponent(radioButtonSingleFileProcessingNo))
                                    .addContainerGap(89, Short.MAX_VALUE)));
        panelMultipleSelectionLayout
            .setVerticalGroup(panelMultipleSelectionLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelMultipleSelectionLayout.createSequentialGroup()
                    .addComponent(radioButtonSingleFileProcessingYes)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement
                        .RELATED)
                            .addComponent(radioButtonSingleFileProcessingNo)
                            .addContainerGap(9, Short.MAX_VALUE)));
        labelInfoRequiredInputs.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoRequiredInputs.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.labelInfoRequiredInputs.text"));    // NOI18N
        buttonCancel.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.buttonCancel.text"));    // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        buttonOk.setText(
            JptBundle.INSTANCE.getString(
                "ProgramPropertiesDialog.buttonOk.text"));    // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        panelInputBeforeExecute,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(
                            panelParameter,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).addComponent(
                                panelProgram,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE).addComponent(
                                    panelMultipleSelection,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE).addGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                        layout.createSequentialGroup().addComponent(
                                            labelInfoRequiredInputs).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                    218,
                                                    Short.MAX_VALUE).addComponent(
                                                        buttonCancel).addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                buttonOk))).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    panelProgram, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        panelParameter, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            panelInputBeforeExecute,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                8, 8, 8).addComponent(
                                panelMultipleSelection,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                    layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                        buttonOk).addComponent(
                                        buttonCancel).addComponent(
                                        labelInfoRequiredInputs)).addContainerGap()));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonChooseFileActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonChooseFileActionPerformed
        chooseProgram();
    }    // GEN-LAST:event_buttonChooseFileActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {    // GEN-FIRST:event_formWindowClosing
        cancel();
    }    // GEN-LAST:event_formWindowClosing

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonCancelActionPerformed
        cancel();
    }    // GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonOkActionPerformed
        accept();
    }    // GEN-LAST:event_buttonOkActionPerformed

    private void checkBoxInputBeforeExecuteActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_checkBoxInputBeforeExecuteActionPerformed
        handleCheckBoxInputBeforeExecuteActionPerformed();
    }    // GEN-LAST:event_checkBoxInputBeforeExecuteActionPerformed

    private void textFieldAliasKeyPressed(java.awt.event.KeyEvent evt) {    // GEN-FIRST:event_textFieldAliasKeyPressed
        handleTextFieldAliasKeyPressed(evt);
    }    // GEN-LAST:event_textFieldAliasKeyPressed

    private void checkBoxUsePatternActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_checkBoxUsePatternActionPerformed
        handleCheckBoxUsePatternActionPerformed();
    }    // GEN-LAST:event_checkBoxUsePatternActionPerformed

    private void buttonInfoUsePatternActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonInfoUsePatternActionPerformed
        showPatternHelp();
    }    // GEN-LAST:event_buttonInfoUsePatternActionPerformed

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
    private javax.swing.JButton      buttonCancel;
    private javax.swing.JButton      buttonChooseFile;
    private javax.swing.ButtonGroup  buttonGroupSingleFileProcessing;
    private javax.swing.JButton      buttonInfoUsePattern;
    private javax.swing.JButton      buttonOk;
    private javax.swing.JCheckBox    checkBoxChangeFile;
    private javax.swing.JCheckBox    checkBoxInputBeforeExecute;
    private javax.swing.JCheckBox    checkBoxInputBeforeExecutePerFile;
    private javax.swing.JCheckBox    checkBoxUsePattern;
    private javax.swing.JLabel       labelAlias;
    private javax.swing.JLabel       labelErrorFileDoesNotExist;
    private javax.swing.JLabel       labelFile;
    private javax.swing.JLabel       labelFilePrompt;
    private javax.swing.JLabel       labelInfoRequiredInputs;
    private javax.swing.JLabel       labelParametersAfterFilename;
    private javax.swing.JLabel       labelParametersBeforeFilename;
    private javax.swing.JPanel       panelInputBeforeExecute;
    private javax.swing.JPanel       panelMultipleSelection;
    private javax.swing.JPanel       panelParameter;
    private javax.swing.JPanel       panelProgram;
    private javax.swing.JRadioButton radioButtonSingleFileProcessingNo;
    private javax.swing.JRadioButton radioButtonSingleFileProcessingYes;
    private javax.swing.JScrollPane  scrollPaneParametersAfterFilename;
    private javax.swing.JScrollPane  scrollPaneParametersBeforeFilename;
    private javax.swing.JScrollPane  scrollPaneUsePattern;
    private javax.swing.JTextArea    textAreaParametersAfterFilename;
    private javax.swing.JTextArea    textAreaParametersBeforeFilename;
    private javax.swing.JTextArea    textAreaUsePattern;
    private javax.swing.JTextField   textFieldAlias;

    // End of variables declaration//GEN-END:variables
}
