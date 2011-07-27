package org.jphototagger.program.view.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.domain.UserDefinedFileType;
import org.jphototagger.program.database.DatabaseUserDefinedFileTypes;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public class EditUserDefinedFileTypeDialog extends Dialog {
    private static final long serialVersionUID = -4574879678796117598L;
    private UserDefinedFileType userDefinedFileType = new UserDefinedFileType();
    private boolean changed;
    private boolean inAddNew = true;

    public EditUserDefinedFileTypeDialog() {
        super(GUI.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.UserDefinedFileType.Edit"));
        MnemonicUtil.setMnemonics(this);
        initSuffixTextField();
        listen();
    }

    private void initSuffixTextField() {
        Document document = textFieldSuffix.getDocument();

        if (document instanceof AbstractDocument) {
            ((AbstractDocument) document).setDocumentFilter(new SuffixDocumentFilter());
        }
    }

    private void listen() {
        ChangeListener changeListener = new ChangeListener();

        textFieldSuffix.getDocument().addDocumentListener(changeListener);
        textFieldDescription.getDocument().addDocumentListener(changeListener);
        checkBoxExternalThumbnailCreator.addActionListener(changeListener);
    }

    public void setUserDefinedFileType(UserDefinedFileType userDefinedFileType) {
        if (userDefinedFileType == null) {
            throw new NullPointerException("userDefinedFileType == null");
        }

        this.userDefinedFileType = userDefinedFileType;

        String suffix = userDefinedFileType.getSuffix();
        String description = userDefinedFileType.getDescription();
        boolean externalThumbnailCreator = userDefinedFileType.isExternalThumbnailCreator();

        if (suffix != null) {
            textFieldSuffix.setText(suffix);
        }

        if (description != null) {
            textFieldDescription.setText(description);
        }

        checkBoxExternalThumbnailCreator.setSelected(externalThumbnailCreator);
        changed = false;
        inAddNew = false;
    }

    private void setGuiToUserDefinedFileType() {
        String suffix = getSuffix();
        String description = getDescription();
        boolean externalThumbnailCreator = isExternalThumbnailCreator();

        userDefinedFileType.setSuffix(suffix);
        userDefinedFileType.setDescription(description);
        userDefinedFileType.setExternalThumbnailCreator(externalThumbnailCreator);
    }

    private String getDescription() {
        return textFieldDescription.getText().trim();
    }

    private String getSuffix() {
        return textFieldSuffix.getText().trim();
    }

    private boolean isExternalThumbnailCreator() {
        return checkBoxExternalThumbnailCreator.isSelected();
    }

    private void save() {
        if (checkValid()) {
            UserDefinedFileType oldUserDefinedFileType = new UserDefinedFileType(userDefinedFileType);
            setGuiToUserDefinedFileType();

            int countUpdated = 0;

            if (inAddNew) {
                countUpdated = DatabaseUserDefinedFileTypes.INSTANCE.insert(userDefinedFileType);
            } else {
                countUpdated = DatabaseUserDefinedFileTypes.INSTANCE.update(oldUserDefinedFileType, userDefinedFileType);
            }

            if (countUpdated > 0) {
                changed = false;
                escape();
            }
        }
    }

    private boolean checkValid() {
        return checkNotEmpty() && checkUniqueSuffix() && checkIsNotAcceptedSuffix();
    }

    private boolean checkNotEmpty() {
        String suffix = getSuffix();
        String description = getDescription();

        if (suffix.isEmpty() || description.isEmpty()) {
            MessageDisplayer.error(this, "EditUserDefinedFileTypeDialog.Error.Empty");
            return false;
        } else {
            return true;
        }
    }

    private boolean checkIsNotAcceptedSuffix() {
        String suffix = getSuffix();
        File aFile = new File("abc." + suffix);

        if (AppFileFilters.INSTANCE.isAcceptedImageFile(aFile)) {
            MessageDisplayer.error(this, "EditUserDefinedFileTypeDialog.Error.IsAcceptedSuffix", suffix);
            return false;
        } else {
            return true;
        }

    }

    private boolean checkUniqueSuffix() {
        if (!inAddNew) {
            return true;
        }

        String suffix = getSuffix();
        boolean suffixExists = DatabaseUserDefinedFileTypes.INSTANCE.existsSuffix(suffix);

        if (suffixExists) {
            MessageDisplayer.error(this, "EditUserDefinedFileTypeDialog.Error.SuffixExists", suffix);
        }

        return !suffixExists;
    }

    @Override
    protected void escape() {
        if (checkDiscardChanges()) {
            super.escape();
        }
    }

    private boolean checkDiscardChanges() {
        if (changed) {
            return MessageDisplayer.confirmYesNo(this, "EditUserDefinedFileTypeDialog.Confirm.DiscardChanges");
        } else {
            return true;
        }
    }

    private class ChangeListener implements DocumentListener, ActionListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            changed = true;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changed = true;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changed = true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            changed = true;
        }
    }

    private class SuffixDocumentFilter extends javax.swing.text.DocumentFilter {
        private static final String VALID_REGEX_PATTERN = "[A-Za-z0-9]+";
        private final int maxLength = DatabaseUserDefinedFileTypes.getMaxLengthSuffix();

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) {
                return;
            }

            int oldLength = fb.getDocument().getLength();
            int newLength = oldLength + string.length();

            if (isValid(string, newLength)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            int oldLength = fb.getDocument().getLength();
            int textLength = text == null ? 0 : text.length();
            int newLength = oldLength - length + textLength;

            if (isValid(text, newLength)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(String text, int newLength) {
            boolean lengthIsValid = newLength <= maxLength;

            return text == null
                    ? lengthIsValid
                    : lengthIsValid && text.matches(VALID_REGEX_PATTERN);
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

        labelSuffix = new javax.swing.JLabel();
        textFieldSuffix = new javax.swing.JTextField();
        labelDescription = new javax.swing.JLabel();
        textFieldDescription = new javax.swing.JTextField();
        checkBoxExternalThumbnailCreator = new javax.swing.JCheckBox();
        buttonHelp = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("EditUserDefinedFileTypeDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelSuffix.setLabelFor(textFieldSuffix);
        labelSuffix.setText(JptBundle.INSTANCE.getString("EditUserDefinedFileTypeDialog.labelSuffix.text")); // NOI18N

        labelDescription.setLabelFor(textFieldDescription);
        labelDescription.setText(JptBundle.INSTANCE.getString("EditUserDefinedFileTypeDialog.labelDescription.text")); // NOI18N

        checkBoxExternalThumbnailCreator.setText(JptBundle.INSTANCE.getString("EditUserDefinedFileTypeDialog.checkBoxExternalThumbnailCreator.text")); // NOI18N

        buttonHelp.setText(JptBundle.INSTANCE.getString("EditUserDefinedFileTypeDialog.buttonHelp.text")); // NOI18N
        buttonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpActionPerformed(evt);
            }
        });

        buttonSave.setText(JptBundle.INSTANCE.getString("EditUserDefinedFileTypeDialog.buttonSave.text")); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSuffix)
                            .addComponent(labelDescription))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                            .addComponent(textFieldSuffix, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)))
                    .addComponent(checkBoxExternalThumbnailCreator)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonHelp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSuffix)
                    .addComponent(textFieldSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDescription)
                    .addComponent(textFieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxExternalThumbnailCreator)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSave)
                    .addComponent(buttonHelp))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        save();
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        escape();
    }//GEN-LAST:event_formWindowClosing

    private void buttonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHelpActionPerformed
        help();
    }//GEN-LAST:event_buttonHelpActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditUserDefinedFileTypeDialog dialog = new EditUserDefinedFileTypeDialog();
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
    private javax.swing.JButton buttonHelp;
    private javax.swing.JButton buttonSave;
    private javax.swing.JCheckBox checkBoxExternalThumbnailCreator;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelSuffix;
    private javax.swing.JTextField textFieldDescription;
    private javax.swing.JTextField textFieldSuffix;
    // End of variables declaration//GEN-END:variables

}
