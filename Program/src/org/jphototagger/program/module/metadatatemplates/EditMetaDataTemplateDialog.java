package org.jphototagger.program.module.metadatatemplates;

import java.awt.Component;
import java.awt.Container;
import java.util.Collections;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.view.ViewUtil;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * You <strong>have to call</strong>
 * {@code #setTemplate(org.jphototagger.program.data.MetadataTemplate)} before
 * calling {@code #setVisible(boolean)}!
 *
 * @author Elmar Baumann
 */
public class EditMetaDataTemplateDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private transient MetadataTemplate template;
    private final transient Xmp xmp = new Xmp();
    private final MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

    public EditMetaDataTemplateDialog() {
        super(InputHelperDialog.INSTANCE, true);
        initComponents();
        setMnemonics();
    }

    private void setMnemonics() {
        MnemonicUtil.setMnemonics((Container) this);
        ViewUtil.setDisplayedMnemonicsToLabels(panelXmpEdit,
                Collections.<Component>emptyList(),
                (char) buttonCancel.getMnemonic(),
                (char) buttonSave.getMnemonic(),
                (char) labelName.getDisplayedMnemonic());
    }

    /**
     * Sets a template and <strong>has to be called bevor getting visible!</strong>
     *
     * @param template template, will be inserted or updated in the repository on
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
        String title = templateHasName()
                ? Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Title.Edit")
                : Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Title.New");
        setTitle(title);
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
        if (checkDirty() && checkSaveTemplateName()) {
            panelXmpEdit.setInputToXmp();
            template.setXmp(xmp);
            if (repo.saveOrUpdateMetadataTemplate(template)) {
                panelXmpEdit.setDirty(false);
            } else {
                String message = Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.Error.Save");
                MessageDisplayer.error(this, message);
            }
            setVisible(false);
        }
    }

    private boolean checkDirty() {
        boolean dirty = panelXmpEdit.isDirty();
        if (!dirty) {
            String message = Bundle.getString(EditMetaDataTemplateDialog.class, "EditMetaDataTemplateDialog.NotDirty");
            MessageDisplayer.error(this, message);
            return false;
        }
        return true;
    }
////
    private boolean checkSaveTemplateName() { // Should be refactored: Checks *and* sets the template name (should only check)
        if (!templateHasName()) {
            String name = textFieldName.getText();
            if (StringUtil.hasContent(name)) {
                name = name.trim();
                if (repo.existsMetadataTemplate(name)) {
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        panelName = UiFactory.panel();
        labelName = UiFactory.label();
        textFieldName = UiFactory.textField();
        scrollPane = UiFactory.scrollPane();
        panelXmpEdit = new org.jphototagger.program.module.metadatatemplates.EditXmpPanel();
        panelButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonSave = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "EditMetaDataTemplateDialog.title")); // NOI18N
        
        setPreferredSize(UiFactory.dimension(500, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        panelName.setLayout(new java.awt.GridBagLayout());

        labelName.setLabelFor(textFieldName);
        labelName.setText(Bundle.getString(getClass(), "EditMetaDataTemplateDialog.labelName.text")); // NOI18N
        labelName.setName("labelName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelName.add(labelName, gridBagConstraints);

        textFieldName.setColumns(20);
        textFieldName.setEnabled(false);
        textFieldName.setName("textFieldName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelName.add(textFieldName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelName, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        panelXmpEdit.setName("panelXmpEdit"); // NOI18N
        scrollPane.setViewportView(panelXmpEdit);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 0, 0, 0);
        panelContent.add(scrollPane, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(5), 0));

        buttonCancel.setText(Bundle.getString(getClass(), "EditMetaDataTemplateDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel);

        buttonSave.setText(Bundle.getString(getClass(), "EditMetaDataTemplateDialog.buttonSave.text")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });
        panelButtons.add(buttonSave);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        save();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        checkSave();
        setVisible(false);
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonSave;
    private javax.swing.JLabel labelName;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelName;
    private org.jphototagger.program.module.metadatatemplates.EditXmpPanel panelXmpEdit;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField textFieldName;
}
