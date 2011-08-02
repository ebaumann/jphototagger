package org.jphototagger.program.view.dialogs;

import java.awt.Container;

import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.view.ViewUtil;

/**
 * Modal dialog for modifying and saving a {@link MetadataTemplate} into the
 * database.
 * <p>
 * You <strong>have to call</strong>
 * {@link #setTemplate(org.jphototagger.program.data.MetadataTemplate)} before
 * calling {@link #setVisible(boolean)}!
 *
 * @author Elmar Baumann
 */
public class EditMetaDataTemplateDialog extends Dialog {
    private static final long serialVersionUID = -6621176928237283620L;
    private transient MetadataTemplate template;
    private transient Xmp xmp = new Xmp();

    public EditMetaDataTemplateDialog() {
        super(InputHelperDialog.INSTANCE, true, UserSettings.INSTANCE.getSettings(), null);
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
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        this.template = template;

        String name = template.getName();

        if (name != null) {
            textFieldName.setText(name);
        }

        textFieldName.setEnabled(name == null);
        setTitle();
    }

    private void setTitle() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N

        setTitle(bundle.getString(templateHasName()
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
        String message = Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Confirm.CheckSave");

        if (panelXmpEdit.isDirty() && MessageDisplayer.confirmYesNo(this, message)) {
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
                String message = Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Error.Save");
                MessageDisplayer.error(this, message);
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
                    String message = Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Error.NameExists", name);
                    MessageDisplayer.error(this, message);
                    textFieldName.requestFocusInWindow();
                    textFieldName.selectAll();

                    return false;
                } else {
                    template.setName(name);
                }
            } else {
                String message = Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Error.SaveNoName");
                MessageDisplayer.error(this, message);
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

    private void initComponents() {//GEN-BEGIN:initComponents

        labelName = new javax.swing.JLabel();
        textFieldName = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        panelPadding = new javax.swing.JPanel();
        panelXmpEdit = new org.jphototagger.program.view.panels.EditXmpPanel();
        buttonCancel = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("EditMetaDataTemplateDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelName.setLabelFor(textFieldName);
        labelName.setText(bundle.getString("EditMetaDataTemplateDialog.labelName.text")); // NOI18N
        labelName.setName("labelName"); // NOI18N

        textFieldName.setEnabled(false);
        textFieldName.setName("textFieldName"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        panelPadding.setName("panelPadding"); // NOI18N

        panelXmpEdit.setName("panelXmpEdit"); // NOI18N

        javax.swing.GroupLayout panelPaddingLayout = new javax.swing.GroupLayout(panelPadding);
        panelPadding.setLayout(panelPaddingLayout);
        panelPaddingLayout.setHorizontalGroup(
            panelPaddingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 631, Short.MAX_VALUE)
            .addGroup(panelPaddingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelPaddingLayout.createSequentialGroup()
                    .addGap(8, 8, 8)
                    .addComponent(panelXmpEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelPaddingLayout.setVerticalGroup(
            panelPaddingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 834, Short.MAX_VALUE)
            .addGroup(panelPaddingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelPaddingLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelXmpEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(80, Short.MAX_VALUE)))
        );

        scrollPane.setViewportView(panelPadding);

        buttonCancel.setText(bundle.getString("EditMetaDataTemplateDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonSave.setText(bundle.getString("EditMetaDataTemplateDialog.buttonSave.text")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(labelName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelName)
                    .addComponent(textFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSave)
                    .addComponent(buttonCancel))
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonSave;
    private javax.swing.JLabel labelName;
    private javax.swing.JPanel panelPadding;
    private org.jphototagger.program.view.panels.EditXmpPanel panelXmpEdit;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField textFieldName;
    // End of variables declaration//GEN-END:variables
}
