package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.database.DatabasePrograms;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Modal Dialog to change or define the properties of a program which can
 * be started within the application.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/04
 */
public class ProgramPropertiesDialog extends Dialog {

    private DatabasePrograms db = DatabasePrograms.getInstance();
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
        String parameters = program.getParameters();
        labelFile.setText(file.getAbsolutePath());
        textFieldAlias.setText(program.getAlias());
        textAreaParameters.setText(parameters == null ? "" : parameters);
        checkBoxParametersAfterFilenames.setSelected(program.isParametersAfterFilename());
        checkBoxInputBeforeExecute.setSelected(program.isInputBeforeExecute());
        setProgramIcon();
    }

    private void setActionTexts() {
        setTitle(Bundle.getString("ProgramPropertiesDialog.title.Action"));
        labelFilePrompt.setText(Bundle.getString("ProgramPropertiesDialog.labelFilePrompt.text.Action"));
        labelAlias.setText(Bundle.getString("ProgramPropertiesDialog.labelAlias.text.Action"));
        labelParameters.setText(Bundle.getString("ProgramPropertiesDialog.labelParameters.text.Action"));
    }

    private void setProgramIcon() {
        if (file != null && file.exists()) {
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
            String parameters = textAreaParameters.getText().trim();
            program.setFile(file);
            program.setAlias(textFieldAlias.getText().trim());
            program.setParameters(parameters.isEmpty() ? null : parameters);
            program.setParametersAfterFilename(checkBoxParametersAfterFilenames.isSelected());
            program.setInputBeforeExecute(checkBoxInputBeforeExecute.isSelected());
            program.setAction(action);
            accecpted = true;
            setVisible(false);
        } else {
            errorMessage(Bundle.getString("ProgramPropertiesDialog.ErrorMessage.MissingData"));
        }
    }

    private boolean inputsValid() {
        return file != null && file.exists() && !file.isDirectory() &&
            !textFieldAlias.getText().trim().isEmpty();
    }

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
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
            PersistentAppSizes.getSizeAndLocation(this);
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
        }
        super.setVisible(visible);
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
                errorMessage(Bundle.getString("ProgramPropertiesDialog.ErrorMessage.ChooseFile"));
            }
        }
    }

    private void handleCheckBoxInputBeforeExecuteActionPerformed() {
        program.setInputBeforeExecute(checkBoxInputBeforeExecute.isSelected());
    }

    private void handleTextFieldAliasKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            accept();
        }
    }

    private void errorMessage(String string) {
        JOptionPane.showMessageDialog(
            this,
            string,
            Bundle.getString("ProgramPropertiesDialog.ErrorMessage.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelFilePrompt = new javax.swing.JLabel();
        labelFile = new javax.swing.JLabel();
        buttonChooseFile = new javax.swing.JButton();
        labelAlias = new javax.swing.JLabel();
        textFieldAlias = new javax.swing.JTextField();
        labelParameters = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        textAreaParameters = new javax.swing.JTextArea();
        checkBoxParametersAfterFilenames = new javax.swing.JCheckBox();
        checkBoxInputBeforeExecute = new javax.swing.JCheckBox();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("ProgramPropertiesDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelFilePrompt.setFont(new java.awt.Font("Dialog", 0, 12));
        labelFilePrompt.setText(Bundle.getString("ProgramPropertiesDialog.labelFilePrompt.text")); // NOI18N

        labelFile.setFont(new java.awt.Font("Dialog", 0, 12));
        labelFile.setForeground(new java.awt.Color(0, 0, 255));
        labelFile.setText(Bundle.getString("ProgramPropertiesDialog.labelFile.text")); // NOI18N
        labelFile.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseFile.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseFile.setText(Bundle.getString("ProgramPropertiesDialog.buttonChooseFile.text")); // NOI18N
        buttonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFileActionPerformed(evt);
            }
        });

        labelAlias.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAlias.setText(Bundle.getString("ProgramPropertiesDialog.labelAlias.text")); // NOI18N

        textFieldAlias.setText(Bundle.getString("ProgramPropertiesDialog.textFieldAlias.text")); // NOI18N
        textFieldAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldAliasKeyPressed(evt);
            }
        });

        labelParameters.setFont(new java.awt.Font("Dialog", 0, 12));
        labelParameters.setText(Bundle.getString("ProgramPropertiesDialog.labelParameters.text")); // NOI18N

        textAreaParameters.setColumns(20);
        textAreaParameters.setRows(2);
        scrollPane.setViewportView(textAreaParameters);

        checkBoxParametersAfterFilenames.setText(Bundle.getString("ProgramPropertiesDialog.checkBoxParametersAfterFilenames.text")); // NOI18N

        checkBoxInputBeforeExecute.setText(Bundle.getString("ProgramPropertiesDialog.checkBoxInputBeforeExecute.text")); // NOI18N
        checkBoxInputBeforeExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxInputBeforeExecuteActionPerformed(evt);
            }
        });

        buttonCancel.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonCancel.setText(Bundle.getString("ProgramPropertiesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOk.setFont(new java.awt.Font("Dialog", 0, 12));
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
                    .addComponent(labelFile, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                    .addComponent(labelFilePrompt)
                    .addComponent(buttonChooseFile, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelAlias)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textFieldAlias, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                    .addComponent(labelParameters)
                    .addComponent(checkBoxParametersAfterFilenames)
                    .addComponent(checkBoxInputBeforeExecute, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
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
                .addComponent(labelFilePrompt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFile, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAlias)
                    .addComponent(textFieldAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelParameters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxParametersAfterFilenames)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxInputBeforeExecute)
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
                ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(true);
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
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxInputBeforeExecute;
    private javax.swing.JCheckBox checkBoxParametersAfterFilenames;
    private javax.swing.JLabel labelAlias;
    private javax.swing.JLabel labelFile;
    private javax.swing.JLabel labelFilePrompt;
    private javax.swing.JLabel labelParameters;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea textAreaParameters;
    private javax.swing.JTextField textFieldAlias;
    // End of variables declaration//GEN-END:variables
}
