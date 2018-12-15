package org.jphototagger.userdefinedfiletypes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class EditUserDefinedFileTypeDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private UserDefinedFileType userDefinedFileType = new UserDefinedFileType();
    private boolean changed;
    private boolean inAddNew = true;
    private final UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup((UserDefinedFileTypesRepository.class));

    public EditUserDefinedFileTypeDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics(this);
        initSuffixTextField();
        listen();
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(EditUserDefinedFileTypeDialog.class, "EditUserDefinedFileTypeDialog.HelpPage"));
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
            int countUpdated;
            if (inAddNew) {
                countUpdated = repo.saveUserDefinedFileType(userDefinedFileType);
            } else {
                countUpdated = repo.updateUserDefinedFileType(oldUserDefinedFileType, userDefinedFileType);
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
            String message = Bundle.getString(EditUserDefinedFileTypeDialog.class, "EditUserDefinedFileTypeDialog.Error.Empty");
            MessageDisplayer.error(this, message);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkIsNotAcceptedSuffix() {
        if (!inAddNew) {
            return true;
        }
        String suffix = getSuffix();
        File aFile = new File("abc." + suffix);
        AppFileFilterProvider fileFilterProvider = Lookup.getDefault().lookup(AppFileFilterProvider.class);
        if (fileFilterProvider.isAcceptedImageFile(aFile)) {
            String message = Bundle.getString(EditUserDefinedFileTypeDialog.class, "EditUserDefinedFileTypeDialog.Error.IsAcceptedSuffix", suffix);
            MessageDisplayer.error(this, message);
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
        boolean suffixExists = repo.existsUserDefinedFileTypeWithSuffix(suffix);
        if (suffixExists) {
            String message = Bundle.getString(EditUserDefinedFileTypeDialog.class, "EditUserDefinedFileTypeDialog.Error.SuffixExists", suffix);
            MessageDisplayer.error(this, message);
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
            String message = Bundle.getString(EditUserDefinedFileTypeDialog.class, "EditUserDefinedFileTypeDialog.Confirm.DiscardChanges");
            return MessageDisplayer.confirmYesNo(this, message);
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

        private static final String VALID_REGEX_PATTERN = "[\\.\\-A-Za-z0-9]+";
        private final int maxLength;

        SuffixDocumentFilter() {
            UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
            maxLength = repo == null ? 45 : repo.getMaxLengthSuffix();
        }

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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        labelSuffix = UiFactory.label();
        textFieldSuffix = UiFactory.textField();
        labelDescription = UiFactory.label();
        textFieldDescription = UiFactory.textField();
        checkBoxExternalThumbnailCreator = UiFactory.checkBox();
        panelButtons = UiFactory.panel();
        buttonHelp = UiFactory.button();
        buttonSave = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "EditUserDefinedFileTypeDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelSuffix.setLabelFor(textFieldSuffix);
        labelSuffix.setText(Bundle.getString(getClass(), "EditUserDefinedFileTypeDialog.labelSuffix.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelContent.add(labelSuffix, gridBagConstraints);

        textFieldSuffix.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelContent.add(textFieldSuffix, gridBagConstraints);

        labelDescription.setLabelFor(textFieldDescription);
        labelDescription.setText(Bundle.getString(getClass(), "EditUserDefinedFileTypeDialog.labelDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(labelDescription, gridBagConstraints);

        textFieldDescription.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelContent.add(textFieldDescription, gridBagConstraints);

        checkBoxExternalThumbnailCreator.setText(Bundle.getString(getClass(), "EditUserDefinedFileTypeDialog.checkBoxExternalThumbnailCreator.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(checkBoxExternalThumbnailCreator, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonHelp.setText(Bundle.getString(getClass(), "EditUserDefinedFileTypeDialog.buttonHelp.text")); // NOI18N
        buttonHelp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelButtons.add(buttonHelp, gridBagConstraints);

        buttonSave.setText(Bundle.getString(getClass(), "EditUserDefinedFileTypeDialog.buttonSave.text")); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonSave, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        save();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        escape();
    }

    private void buttonHelpActionPerformed(java.awt.event.ActionEvent evt) {
        showHelp();
    }

    private javax.swing.JButton buttonHelp;
    private javax.swing.JButton buttonSave;
    private javax.swing.JCheckBox checkBoxExternalThumbnailCreator;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelSuffix;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JTextField textFieldDescription;
    private javax.swing.JTextField textFieldSuffix;
}
