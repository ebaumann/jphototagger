package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * Modal Dialog to change or define the properties of a program which can
 * be started within the application.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/04
 */
public final class ProgramPropertiesDialog extends Dialog {

    private Program program = new Program();
    private File file;
    private boolean accecpted = false;
    private boolean action;

    public ProgramPropertiesDialog(boolean action) {
        super((java.awt.Frame) null, true);
        this.action = action;
        program.setAction(action);
        initComponents();
        postInitComponents();
        registerKeyStrokes();
    }

    public void setProgram(Program program) {
        this.program = program;
        file = program.getFile();
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        String parametersAfterFilename = program.getParametersAfterFilename();
        labelFile.setText(file.getAbsolutePath());
        textFieldAlias.setText(program.getAlias());
        textAreaParametersBeforeFilename.setText(parametersBeforeFilename ==
                null
                                                 ? ""
                                                 : parametersBeforeFilename);
        textAreaParametersAfterFilename.setText(parametersAfterFilename == null
                                                ? ""
                                                : parametersAfterFilename);
        checkBoxInputBeforeExecute.setSelected(program.isInputBeforeExecute());
        checkBoxInputBeforeExecutePerFile.setSelected(program.
                isInputBeforeExecutePerFile());
        checkBoxInputBeforeExecutePerFile.setEnabled(program.
                isInputBeforeExecute());
        radioButtonSingleFileProcessingYes.setSelected(program.
                isSingleFileProcessing());
        radioButtonSingleFileProcessingNo.setSelected(!program.
                isSingleFileProcessing());
        checkBoxChangeFile.setSelected(program.isChangeFile());
        setProgramIcon();
    }

    private void setActionTexts() {
        setTitle(Bundle.getString("ProgramPropertiesDialog.title.Action")); // NOI18N
        labelFilePrompt.setText(Bundle.getString(
                "ProgramPropertiesDialog.labelFilePrompt.text.Action")); // NOI18N
        labelAlias.setText(Bundle.getString(
                "ProgramPropertiesDialog.labelAlias.text.Action")); // NOI18N
    }

    private void setProgramIcon() {
        if (file != null && file.exists()) {
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
            String parametersBeforeFilename = textAreaParametersBeforeFilename.
                    getText().trim();
            String parametersAfterFilename = textAreaParametersAfterFilename.
                    getText().trim();

            program.setAction(action);
            program.setFile(file);
            program.setAlias(textFieldAlias.getText().trim());
            program.setParametersBeforeFilename(parametersBeforeFilename.isEmpty()
                                                ? null
                                                : parametersBeforeFilename);
            program.setParametersAfterFilename(parametersAfterFilename.isEmpty()
                                               ? null
                                               : parametersAfterFilename);
            program.setInputBeforeExecute(
                    checkBoxInputBeforeExecute.isSelected());
            program.setInputBeforeExecutePerFile(checkBoxInputBeforeExecutePerFile.
                    isSelected());
            program.setSingleFileProcessing(radioButtonSingleFileProcessingYes.
                    isSelected());
            program.setChangeFile(checkBoxChangeFile.isSelected());

            accecpted = true;
            setVisible(false);
        } else {
            MessageDisplayer.error(
                    "ProgramPropertiesDialog.Error.MissingData"); // NOI18N
        }
    }

    private boolean inputsValid() {
        return file != null && file.exists() && !file.isDirectory() &&
                !textFieldAlias.getText().trim().isEmpty();
    }

    private void postInitComponents() {
        setIconImages(AppIcons.getAppIcons());
        if (action) {
            setActionTexts();
        } else {
            getContentPane().remove(checkBoxInputBeforeExecute);
        }
    }

    private void cancel() {
        accecpted = false;
        setVisible(false);
    }

    @Override
    protected void escape() {
        cancel();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.writeToFile();
    }

    private void chooseProgram() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (f.exists() && !f.isDirectory()) {
                file = f;
                labelFile.setText(file.getAbsolutePath());
                setProgramIcon();
            } else {
                MessageDisplayer.error(
                        "ProgramPropertiesDialog.Error.ChooseFile"); // NOI18N
            }
        }
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupSingleFileProcessing = new javax.swing.ButtonGroup();
        panelProgram = new javax.swing.JPanel();
        labelFilePrompt = new javax.swing.JLabel();
        buttonChooseFile = new javax.swing.JButton();
        labelFile = new javax.swing.JLabel();
        labelAlias = new javax.swing.JLabel();
        textFieldAlias = new javax.swing.JTextField();
        checkBoxChangeFile = new javax.swing.JCheckBox();
        panelParameter = new javax.swing.JPanel();
        labelParametersBeforeFilename = new javax.swing.JLabel();
        scrollPaneParametersBeforeFilename = new javax.swing.JScrollPane();
        textAreaParametersBeforeFilename = textAreaParametersBeforeFilename = new TabLeavingTextArea();
        labelParametersAfterFilename = new javax.swing.JLabel();
        scrollPaneParametersAfterFilename = new javax.swing.JScrollPane();
        textAreaParametersAfterFilename = textAreaParametersAfterFilename  = new TabLeavingTextArea();
        panelInputBeforeExecute = new javax.swing.JPanel();
        checkBoxInputBeforeExecute = new javax.swing.JCheckBox();
        checkBoxInputBeforeExecutePerFile = new javax.swing.JCheckBox();
        panelMultipleSelection = new javax.swing.JPanel();
        radioButtonSingleFileProcessingYes = new javax.swing.JRadioButton();
        radioButtonSingleFileProcessingNo = new javax.swing.JRadioButton();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("ProgramPropertiesDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelProgram.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("ProgramPropertiesDialog.panelProgram.border.title"))); // NOI18N

        labelFilePrompt.setText(Bundle.getString("ProgramPropertiesDialog.labelFilePrompt.text")); // NOI18N

        buttonChooseFile.setText(Bundle.getString("ProgramPropertiesDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        labelFile.setForeground(new java.awt.Color(0, 0, 255));
        labelFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelAlias.setText(Bundle.getString("ProgramPropertiesDialog.labelAlias.text")); // NOI18N

        textFieldAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldAliasKeyPressed(evt);
            }
        });

        checkBoxChangeFile.setText(Bundle.getString("ProgramPropertiesDialog.checkBoxChangeFile.text")); // NOI18N
        checkBoxChangeFile.setToolTipText(Bundle.getString("ProgramPropertiesDialog.checkBoxChangeFile.toolTipText")); // NOI18N

        javax.swing.GroupLayout panelProgramLayout = new javax.swing.GroupLayout(panelProgram);
        panelProgram.setLayout(panelProgramLayout);
        panelProgramLayout.setHorizontalGroup(
            panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProgramLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProgramLayout.createSequentialGroup()
                        .addComponent(labelFilePrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 256, Short.MAX_VALUE)
                        .addComponent(buttonChooseFile))
                    .addComponent(labelFile, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addGroup(panelProgramLayout.createSequentialGroup()
                        .addComponent(labelAlias)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldAlias, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .addComponent(checkBoxChangeFile))
                .addContainerGap())
        );
        panelProgramLayout.setVerticalGroup(
            panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProgramLayout.createSequentialGroup()
                .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFilePrompt)
                    .addComponent(buttonChooseFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFile, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProgramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAlias)
                    .addComponent(textFieldAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(checkBoxChangeFile)
                .addContainerGap())
        );

        panelParameter.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("ProgramPropertiesDialog.panelParameter.border.title"))); // NOI18N

        labelParametersBeforeFilename.setText(Bundle.getString("ProgramPropertiesDialog.labelParametersBeforeFilename.text")); // NOI18N

        textAreaParametersBeforeFilename.setColumns(20);
        textAreaParametersBeforeFilename.setLineWrap(true);
        textAreaParametersBeforeFilename.setRows(1);
        scrollPaneParametersBeforeFilename.setViewportView(textAreaParametersBeforeFilename);

        labelParametersAfterFilename.setText(Bundle.getString("ProgramPropertiesDialog.labelParametersAfterFilename.text")); // NOI18N

        textAreaParametersAfterFilename.setColumns(20);
        textAreaParametersAfterFilename.setLineWrap(true);
        textAreaParametersAfterFilename.setRows(1);
        scrollPaneParametersAfterFilename.setViewportView(textAreaParametersAfterFilename);

        javax.swing.GroupLayout panelParameterLayout = new javax.swing.GroupLayout(panelParameter);
        panelParameter.setLayout(panelParameterLayout);
        panelParameterLayout.setHorizontalGroup(
            panelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelParameterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelParametersBeforeFilename, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelParametersAfterFilename, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneParametersBeforeFilename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addComponent(scrollPaneParametersAfterFilename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelParameterLayout.setVerticalGroup(
            panelParameterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParameterLayout.createSequentialGroup()
                .addComponent(labelParametersBeforeFilename)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneParametersBeforeFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelParametersAfterFilename)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneParametersAfterFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelInputBeforeExecute.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("ProgramPropertiesDialog.panelInputBeforeExecute.border.title"))); // NOI18N

        checkBoxInputBeforeExecute.setText(Bundle.getString("ProgramPropertiesDialog.checkBoxInputBeforeExecute.text")); // NOI18N
        checkBoxInputBeforeExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxInputBeforeExecuteActionPerformed(evt);
            }
        });

        checkBoxInputBeforeExecutePerFile.setText(Bundle.getString("ProgramPropertiesDialog.checkBoxInputBeforeExecutePerFile.text")); // NOI18N
        checkBoxInputBeforeExecutePerFile.setEnabled(false);

        javax.swing.GroupLayout panelInputBeforeExecuteLayout = new javax.swing.GroupLayout(panelInputBeforeExecute);
        panelInputBeforeExecute.setLayout(panelInputBeforeExecuteLayout);
        panelInputBeforeExecuteLayout.setHorizontalGroup(
            panelInputBeforeExecuteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputBeforeExecuteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInputBeforeExecuteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxInputBeforeExecute)
                    .addComponent(checkBoxInputBeforeExecutePerFile))
                .addContainerGap(88, Short.MAX_VALUE))
        );

        panelInputBeforeExecuteLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {checkBoxInputBeforeExecute, checkBoxInputBeforeExecutePerFile});

        panelInputBeforeExecuteLayout.setVerticalGroup(
            panelInputBeforeExecuteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputBeforeExecuteLayout.createSequentialGroup()
                .addComponent(checkBoxInputBeforeExecute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxInputBeforeExecutePerFile)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        panelMultipleSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("ProgramPropertiesDialog.panelSingleFileProcessing.border.title"))); // NOI18N

        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingYes);
        radioButtonSingleFileProcessingYes.setSelected(true);
        radioButtonSingleFileProcessingYes.setText(Bundle.getString("ProgramPropertiesDialog.radioButtonSingleFileProcessingYes.text")); // NOI18N

        buttonGroupSingleFileProcessing.add(radioButtonSingleFileProcessingNo);
        radioButtonSingleFileProcessingNo.setText(Bundle.getString("ProgramPropertiesDialog.radioButtonSingleFileProcessingNo.text")); // NOI18N

        javax.swing.GroupLayout panelMultipleSelectionLayout = new javax.swing.GroupLayout(panelMultipleSelection);
        panelMultipleSelection.setLayout(panelMultipleSelectionLayout);
        panelMultipleSelectionLayout.setHorizontalGroup(
            panelMultipleSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMultipleSelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMultipleSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonSingleFileProcessingYes)
                    .addComponent(radioButtonSingleFileProcessingNo))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        panelMultipleSelectionLayout.setVerticalGroup(
            panelMultipleSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMultipleSelectionLayout.createSequentialGroup()
                .addComponent(radioButtonSingleFileProcessingYes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonSingleFileProcessingNo)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttonCancel.setText(Bundle.getString("ProgramPropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOk.setText(Bundle.getString("ProgramPropertiesDialog.buttonOk.text")); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelInputBeforeExecute, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelParameter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelProgram, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelMultipleSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonOk)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelParameter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInputBeforeExecute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(panelMultipleSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOk)
                    .addComponent(buttonCancel))
                .addContainerGap())
        );

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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(
                        true);
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
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxChangeFile;
    private javax.swing.JCheckBox checkBoxInputBeforeExecute;
    private javax.swing.JCheckBox checkBoxInputBeforeExecutePerFile;
    private javax.swing.JLabel labelAlias;
    private javax.swing.JLabel labelFile;
    private javax.swing.JLabel labelFilePrompt;
    private javax.swing.JLabel labelParametersAfterFilename;
    private javax.swing.JLabel labelParametersBeforeFilename;
    private javax.swing.JPanel panelInputBeforeExecute;
    private javax.swing.JPanel panelMultipleSelection;
    private javax.swing.JPanel panelParameter;
    private javax.swing.JPanel panelProgram;
    private javax.swing.JRadioButton radioButtonSingleFileProcessingNo;
    private javax.swing.JRadioButton radioButtonSingleFileProcessingYes;
    private javax.swing.JScrollPane scrollPaneParametersAfterFilename;
    private javax.swing.JScrollPane scrollPaneParametersBeforeFilename;
    private javax.swing.JTextArea textAreaParametersAfterFilename;
    private javax.swing.JTextArea textAreaParametersBeforeFilename;
    private javax.swing.JTextField textFieldAlias;
    // End of variables declaration//GEN-END:variables
}
