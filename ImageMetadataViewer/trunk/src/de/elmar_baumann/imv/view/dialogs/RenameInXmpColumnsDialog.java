package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.SettingsHints;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class RenameInXmpColumnsDialog extends Dialog {

    private boolean accepted = true;

    /** Creates new form RenameXmpColumnsDialog */
    public RenameInXmpColumnsDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIconImages(AppIcons.getAppIcons());
        setModel();
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    private void setModel() {
        Set<Column> columns = EditColumns.getColumns();
        comboBoxReplaceColumn.setModel(
            new DefaultComboBoxModel(columns.toArray(new Column[columns.size()])));
    }

    public boolean accepted() {
        return accepted;
    }

    public String getOldString() {
        return textFieldOldString.getText().trim();
    }

    public String getNewString() {
        return textFieldNewString.getText().trim();
    }

    public Column getColumn() {
        return (Column) comboBoxReplaceColumn.getModel().getSelectedItem();
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
        UserSettings.INSTANCE.getSettings().getComponent(this, new SettingsHints(EnumSet.of(SettingsHints.Option.SET_TABBED_PANE_CONTENT)));
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setComponent(this, new SettingsHints(EnumSet.of(SettingsHints.Option.SET_TABBED_PANE_CONTENT)));
    }

    private void checkOk() {
        if (getOldString().isEmpty()) {
            oldStringIsEmptyErrorMessage();
        } else {
            accepted = true;
            setVisible(false);
        }
    }

    private void cancel() {
        accepted = false;
        setVisible(false);
    }

    private void oldStringIsEmptyErrorMessage() {
        JOptionPane.showMessageDialog(
            null,
            Bundle.getString("RenameInXmpColumnsDialog.ErrorMessage.MissingReplaceString"),
            Bundle.getString("RenameInXmpColumnsDialog.ErrorMessage.MissingReplaceString.Title"),
            JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.RenameInXmpColumnsDialog"));
    }

    @Override
    protected void escape() {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelReplaceIn = new javax.swing.JLabel();
        comboBoxReplaceColumn = new javax.swing.JComboBox();
        labelOldString = new javax.swing.JLabel();
        textFieldOldString = new javax.swing.JTextField();
        labelNewString = new javax.swing.JLabel();
        textFieldNewString = new javax.swing.JTextField();
        buttonReplace = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("RenameInXmpColumnsDialog.title")); // NOI18N

        labelReplaceIn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelReplaceIn.setText(Bundle.getString("RenameInXmpColumnsDialog.labelReplaceIn.text")); // NOI18N

        comboBoxReplaceColumn.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        comboBoxReplaceColumn.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        labelOldString.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelOldString.setText(Bundle.getString("RenameInXmpColumnsDialog.labelOldString.text")); // NOI18N

        labelNewString.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelNewString.setText(Bundle.getString("RenameInXmpColumnsDialog.labelNewString.text")); // NOI18N

        buttonReplace.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonReplace.setMnemonic('r');
        buttonReplace.setText(Bundle.getString("RenameInXmpColumnsDialog.buttonReplace.text")); // NOI18N
        buttonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReplaceActionPerformed(evt);
            }
        });

        buttonCancel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonCancel.setMnemonic('a');
        buttonCancel.setText(Bundle.getString("RenameInXmpColumnsDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldNewString, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .addComponent(textFieldOldString, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .addComponent(labelReplaceIn)
                    .addComponent(comboBoxReplaceColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelOldString)
                    .addComponent(labelNewString)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonReplace)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelReplaceIn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxReplaceColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelOldString)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldOldString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelNewString)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldNewString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonReplace)
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
    cancel();
}//GEN-LAST:event_buttonCancelActionPerformed

private void buttonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReplaceActionPerformed
    checkOk();
}//GEN-LAST:event_buttonReplaceActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RenameInXmpColumnsDialog dialog = new RenameInXmpColumnsDialog();
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
    private javax.swing.JButton buttonReplace;
    private javax.swing.JComboBox comboBoxReplaceColumn;
    private javax.swing.JLabel labelNewString;
    private javax.swing.JLabel labelOldString;
    private javax.swing.JLabel labelReplaceIn;
    private javax.swing.JTextField textFieldNewString;
    private javax.swing.JTextField textFieldOldString;
    // End of variables declaration//GEN-END:variables
}
