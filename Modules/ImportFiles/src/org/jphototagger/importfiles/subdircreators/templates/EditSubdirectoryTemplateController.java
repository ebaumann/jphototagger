package org.jphototagger.importfiles.subdircreators.templates;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jphototagger.lib.swing.DocumentChangeListener;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * Controller for editing a single {@link SubdirectoryTemplate}.
 *
 * @author Elmar Baumann
 */
public final class EditSubdirectoryTemplateController {

    private final EditSubdirectoryTemplatePanel view = new EditSubdirectoryTemplatePanel();
    private final SubdirectoryTemplate template;
    private final EnterKeyListener displayNameEnterKeyListener = new EnterKeyListener(null);
    private String dialogTitle;
    private String okButtonText;
    private Component parentComponent;

    /**
     * @param template template for editing
     */
    public EditSubdirectoryTemplateController(SubdirectoryTemplate template) {
        this.template = Objects.requireNonNull(template, "template == null");
        initView();
    }

    private void initView() {
        view.getButtonAddYear().addActionListener(new TextInserter(TemplateSubdirectoryCreateStrategy.FILE_DATE_YEAR));
        view.getButtonAddMonth().addActionListener(new TextInserter(TemplateSubdirectoryCreateStrategy.FILE_DATE_MONTH));
        view.getButtonAddDay().addActionListener(new TextInserter(TemplateSubdirectoryCreateStrategy.FILE_DATE_DAY));
        view.getButtonAddFileSeparator().addActionListener(new TextInserter(File.separator));
        view.getTextFieldDisplayName().addKeyListener(displayNameEnterKeyListener);
        view.getTextFieldTemplate().getDocument().addDocumentListener(exampleListener);
        view.getLabelExample().setText(" "); // Ensures height
        MnemonicUtil.setMnemonics(view);
    }

    /**
     * @param parentComponent component, relative to that the UI shall be
     *                        positioned.
     */
    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setOkButtonText(String okButtonText) {
        this.okButtonText = okButtonText;
    }

    /**
     * Shows an UI for editing a template.
     *
     * @return true, if the template was modified
     */
    public boolean execute() {
        Objects.requireNonNull(template, "template == null");

        templateToView();

        InputDialog2 dlg = createDialog();

        dlg.setVisible(true);
        displayNameEnterKeyListener.buttonOk = null;
        if (dlg.isAccepted()) {
            viewToTemplate();
            return true;
        }

        return false;
    }

    private InputDialog2 createDialog() {
        InputDialog2 dlg = new InputDialog2(ComponentUtil.findFrameWithIcon(), true);

        dlg.setTitle(ObjectUtil.firstNonNull(dialogTitle, Bundle.getString(EditSubdirectoryTemplateController.class, "EditSubdirectoryTemplateController.Dialog.Title")));
        dlg.getButtonOk().setText(ObjectUtil.firstNonNull(okButtonText, Bundle.getString(EditSubdirectoryTemplateController.class, "EditSubdirectoryTemplateController.Dlg.ButtonOk.Text")));
        MnemonicUtil.setMnemonics(dlg);
        dlg.setComponent(view);
        dlg.setLocationRelativeTo(parentComponent);

        OkButtonEnabler okButtonEnabler = new OkButtonEnabler(dlg.getButtonOk());
        okButtonEnabler.setOkButtonEnabled();
        displayNameEnterKeyListener.buttonOk = dlg.getButtonOk();
        view.getTextFieldDisplayName().getDocument().addDocumentListener(okButtonEnabler);
        view.getTextFieldTemplate().getDocument().addDocumentListener(okButtonEnabler);
        view.getTextFieldTemplate().addKeyListener(new EnterKeyListener(dlg.getButtonOk()));

        dlg.pack();

        return dlg;
    }

    private void templateToView() {
        view.getTextFieldDisplayName().setText(template.getDisplayName());
        view.getTextFieldTemplate().setText(template.getTemplate());
    }

    private void viewToTemplate() {
        template.setDisplayName(view.getTextFieldDisplayName().getText());
        template.setTemplate(view.getTextFieldTemplate().getText());
    }

    private final class OkButtonEnabler extends DocumentChangeListener {

        private final AbstractButton okButton;

        private OkButtonEnabler(AbstractButton okButton) {
            this.okButton = okButton;
        }

        @Override
        public void documentChanged(DocumentEvent e) {
            setOkButtonEnabled();
        }

        private void setOkButtonEnabled() {
            okButton.setEnabled(inputsValid());
        }
    }

    private boolean inputsValid() {
        boolean hasDisplayName = StringUtil.hasContent(view.getTextFieldDisplayName().getText());
        boolean hasTemplate = StringUtil.hasContent(view.getTextFieldTemplate().getText());

        return hasDisplayName && hasTemplate;
    }

    private final class EnterKeyListener extends KeyAdapter {

        private AbstractButton buttonOk;

        private EnterKeyListener(AbstractButton buttonOk) {
            this.buttonOk = buttonOk;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (buttonOk != null && e.getKeyCode() == KeyEvent.VK_ENTER && inputsValid()) {
                buttonOk.doClick();
            }
        }

    }

    private final class TextInserter implements ActionListener {

        private final String text;

        private TextInserter(String text) {
            this.text = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            view.getTextFieldTemplate().replaceSelection(text);
            view.getTextFieldTemplate().requestFocusInWindow(); // When button was clicked via Mnemonics, this button would be selected
        }
    }

    private final DocumentListener exampleListener = new DocumentChangeListener() {
        @Override
        public void documentChanged(DocumentEvent e) {
            String example = TemplateSubdirectoryCreateStrategy.getExample(view.getTextFieldTemplate().getText());
            if (!StringUtil.hasContent(example)) {
                example = " "; // Ensures height
            }
            view.getLabelExample().setText(example);
        }
    };
}
