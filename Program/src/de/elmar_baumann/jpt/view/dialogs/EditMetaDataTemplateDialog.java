/*
 * @(#)EditMetaDataTemplateDialog.java    2010-01-08
 *
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
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;

import java.awt.Container;

/**
 * Modal dialog for modifying and saving a {@link MetadataTemplate} into the
 * database.
 * <p>
 * You <strong>have to call</strong>
 * {@link #setTemplate(de.elmar_baumann.jpt.data.MetadataTemplate)} before
 * calling {@link #setVisible(boolean)}!
 *
 * @author  Elmar Baumann
 */
public class EditMetaDataTemplateDialog extends Dialog {
    private static final long          serialVersionUID = -6621176928237283620L;
    private transient MetadataTemplate template;
    private transient Xmp              xmp = new Xmp();

    public EditMetaDataTemplateDialog() {
        super(InputHelperDialog.INSTANCE, true,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setMnemonics();
    }

    private void setMnemonics() {
        MnemonicUtil.setMnemonics((Container) this);
        ViewUtil.setDisplayedMnemonicsToLabels(panelXmpEdit,
                (char) buttonCancel.getMnemonic(),
                (char) buttonSave.getMnemonic(),
                (char) labelName.getDisplayedMnemonic());
    }

    /**
     * Sets a template and <strong>has to be called bevor getting visible!</strong>
     *
     * @param template template, will be inserted or updated in the database on
     *                 save action
     */
    public void setTemplate(MetadataTemplate template) {
        this.template = template;

        String name = template.getName();

        if (name != null) {
            textFieldName.setText(name);
        }

        textFieldName.setEnabled(name == null);
        setTitle();
    }

    private void setTitle() {
        setTitle(JptBundle.INSTANCE.getString(templateHasName()
                ? "EditMetaDataTemplateDialog.Title.Edit"
                : "EditMetaDataTemplateDialog.Title.New"));
    }

    private boolean templateHasName() {
        return (template.getName() != null)
               &&!template.getName().trim().isEmpty();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            templateToInput();
        }

        super.setVisible(visible);
    }

    private void templateToInput() {
        assert template != null : "Template was not set via #setTemplate()!";
        xmp.setMetaDataTemplate(template);
        panelXmpEdit.setXmp(xmp);
    }

    @Override
    protected void escape() {
        checkSave();
        super.escape();
    }

    private void checkSave() {
        if (panelXmpEdit.isDirty()
                && MessageDisplayer.confirmYesNo(this,
                    "EditMetaDataTemplateDialog.Confirm.CheckSave")) {
            save();
        }
    }

    private void save() {
        if (panelXmpEdit.isDirty() && checkSaveTemplateName()) {
            panelXmpEdit.setInputToXmp();
            template.setXmp(xmp);

            if (DatabaseMetadataTemplates.INSTANCE.insertOrUpdate(template)) {
                panelXmpEdit.setDirty(false);
            } else {
                MessageDisplayer.error(this,
                                       "EditMetaDataTemplateDialog.Error.Save");
            }

            setVisible(false);
        }
    }

    private boolean checkSaveTemplateName() {
        if (!templateHasName()) {
            String  name             = textFieldName.getText();
            boolean textfieldHasName = (name != null) &&!name.trim().isEmpty();

            if (textfieldHasName) {
                if (DatabaseMetadataTemplates.INSTANCE.exists(name)) {
                    MessageDisplayer.error(
                        this, "EditMetaDataTemplateDialog.Error.NameExists",
                        name);
                    textFieldName.requestFocusInWindow();
                    textFieldName.selectAll();

                    return false;
                } else {
                    template.setName(name);
                }
            } else {
                MessageDisplayer.error(
                    this, "EditMetaDataTemplateDialog.Error.SaveNoName");
                textFieldName.requestFocusInWindow();

                return false;
            }
        }

        return true;
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
        labelName     = new javax.swing.JLabel();
        textFieldName = new javax.swing.JTextField();
        scrollPane    = new javax.swing.JScrollPane();
        panelPadding  = new javax.swing.JPanel();
        panelXmpEdit  = new de.elmar_baumann.jpt.view.panels.EditXmpPanel();
        buttonCancel  = new javax.swing.JButton();
        buttonSave    = new javax.swing.JButton();
        setDefaultCloseOperation(
            javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        setTitle(bundle.getString("EditMetaDataTemplateDialog.title"));    // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        labelName.setLabelFor(textFieldName);
        labelName.setText(
            bundle.getString("EditMetaDataTemplateDialog.labelName.text"));    // NOI18N
        textFieldName.setEnabled(false);

        javax.swing.GroupLayout panelPaddingLayout =
            new javax.swing.GroupLayout(panelPadding);

        panelPadding.setLayout(panelPaddingLayout);
        panelPaddingLayout.setHorizontalGroup(
            panelPaddingLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0, 374, Short.MAX_VALUE).addGroup(
                panelPaddingLayout.createParallelGroup(
                    javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    panelPaddingLayout.createSequentialGroup().addGap(
                        8, 8, 8).addComponent(
                        panelXmpEdit, javax.swing.GroupLayout.DEFAULT_SIZE,
                        354, Short.MAX_VALUE).addContainerGap())));
        panelPaddingLayout
            .setVerticalGroup(panelPaddingLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 754, Short.MAX_VALUE)
                .addGroup(panelPaddingLayout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment
                        .LEADING)
                            .addGroup(panelPaddingLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelXmpEdit,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE).addContainerGap())));
        scrollPane.setViewportView(panelPadding);
        buttonCancel.setText(
            bundle.getString("EditMetaDataTemplateDialog.buttonCancel.text"));    // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        buttonSave.setText(
            bundle.getString("EditMetaDataTemplateDialog.buttonSave.text"));    // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
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
                        scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 392,
                        Short.MAX_VALUE).addGroup(
                            javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup().addComponent(
                                buttonCancel).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                buttonSave)).addGroup(
                                    layout.createSequentialGroup().addComponent(
                                        labelName).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                        textFieldName,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        333,
                                        Short.MAX_VALUE))).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        labelName).addComponent(
                        textFieldName, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                            scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE,
                            260, Short.MAX_VALUE).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                layout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                    buttonSave).addComponent(
                                    buttonCancel)).addContainerGap()));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        save();
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        checkSave();
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditMetaDataTemplateDialog dialog =
                    new EditMetaDataTemplateDialog();

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
    private javax.swing.JButton                           buttonCancel;
    private javax.swing.JButton                           buttonSave;
    private javax.swing.JLabel                            labelName;
    private javax.swing.JPanel                            panelPadding;
    private de.elmar_baumann.jpt.view.panels.EditXmpPanel panelXmpEdit;
    private javax.swing.JScrollPane                       scrollPane;
    private javax.swing.JTextField                        textFieldName;

    // End of variables declaration//GEN-END:variables
}
