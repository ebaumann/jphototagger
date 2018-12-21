package org.jphototagger.program.module.metadatatemplates;

import java.awt.Dimension;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class EditXmpDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private final Xmp xmp;
    private boolean accepted;

    public EditXmpDialog() {
        this(new Xmp());
    }

    public EditXmpDialog(Xmp xmp) {
        super(ComponentUtil.findFrameWithIcon(), true);
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }
        this.xmp = xmp;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        panelEditXmp.setXmp(xmp);
        setButtonInsertTemplateEnabled();
        buttonEditMetadataTemplates.setIcon(Icons.getIcon("icon_add.png")); // NOI18N
        MnemonicUtil.setMnemonics(this);
    }

    private void setButtonInsertTemplateEnabled() {
        buttonInsertTemplate.setEnabled(comboBoxTemplates.getSelectedIndex() >= 0);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Xmp getXmp() {
        return xmp;
    }

    private void insertTemplate() {
        Object selectedItem = comboBoxTemplates.getSelectedItem();
        if (selectedItem instanceof MetadataTemplate) {
            xmp.setMetaDataTemplate((MetadataTemplate) selectedItem);
            panelEditXmp.setXmp(xmp);
        }
    }

    private void acceptInputAndDispose() {
        accepted = true;
        if (panelEditXmp.isDirty()) {
            panelEditXmp.setInputToXmp();
        }
        dispose();
    }

    private void cancelAndDispose() {
        accepted = false;
        dispose();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panelEditXmp.requestFocusInWindow();
        }
        super.setVisible(visible);
    }

    private void editMetadataTemplates() {
        EditMetaDataTemplateDialog dialog = new EditMetaDataTemplateDialog();
        MetadataTemplate t = new MetadataTemplate();
        dialog.setTemplate(t);
        ComponentUtil.show(dialog);
        toFront();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        panelTemplates = UiFactory.panel();
        labelTemplates = UiFactory.label();
        comboBoxTemplates = UiFactory.comboBox();
        buttonEditMetadataTemplates = UiFactory.button();
        buttonInsertTemplate = UiFactory.button();
        scrollPanePanelEditXmp = UiFactory.scrollPane();
        panelEditXmp = new org.jphototagger.program.module.metadatatemplates.EditXmpPanel();
        panelSubmitButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonOk = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "EditXmpDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setPreferredSize(new Dimension(500, 450));
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelTemplates.setLayout(new java.awt.GridBagLayout());

        labelTemplates.setLabelFor(comboBoxTemplates);
        labelTemplates.setText(Bundle.getString(getClass(), "EditXmpDialog.labelTemplates.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelTemplates.add(labelTemplates, gridBagConstraints);

        comboBoxTemplates.setModel(new MetadataTemplatesComboBoxModel());
        comboBoxTemplates.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxTemplatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTemplates.add(comboBoxTemplates, gridBagConstraints);

        buttonEditMetadataTemplates.setToolTipText(Bundle.getString(getClass(), "EditXmpDialog.buttonEditMetadataTemplates.toolTipText")); // NOI18N
        buttonEditMetadataTemplates.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonEditMetadataTemplates.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditMetadataTemplatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTemplates.add(buttonEditMetadataTemplates, gridBagConstraints);

        buttonInsertTemplate.setText(Bundle.getString(getClass(), "EditXmpDialog.buttonInsertTemplate.text")); // NOI18N
        buttonInsertTemplate.setEnabled(false);
        buttonInsertTemplate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertTemplateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTemplates.add(buttonInsertTemplate, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelTemplates, gridBagConstraints);

        scrollPanePanelEditXmp.setViewportView(panelEditXmp);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(scrollPanePanelEditXmp, gridBagConstraints);

        panelSubmitButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setText(Bundle.getString(getClass(), "EditXmpDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panelSubmitButtons.add(buttonCancel, gridBagConstraints);

        buttonOk.setText(Bundle.getString(getClass(), "EditXmpDialog.buttonOk.text")); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelSubmitButtons.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelSubmitButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonInsertTemplateActionPerformed(java.awt.event.ActionEvent evt) {
        insertTemplate();
    }

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {
        acceptInputAndDispose();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        cancelAndDispose();
    }

    private void comboBoxTemplatesActionPerformed(java.awt.event.ActionEvent evt) {
        setButtonInsertTemplateEnabled();
    }

    private void buttonEditMetadataTemplatesActionPerformed(java.awt.event.ActionEvent evt) {
        editMetadataTemplates();
    }

    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonEditMetadataTemplates;
    private javax.swing.JButton buttonInsertTemplate;
    private javax.swing.JButton buttonOk;
    private javax.swing.JComboBox<Object> comboBoxTemplates;
    private javax.swing.JLabel labelTemplates;
    private javax.swing.JPanel panelContent;
    private org.jphototagger.program.module.metadatatemplates.EditXmpPanel panelEditXmp;
    private javax.swing.JPanel panelSubmitButtons;
    private javax.swing.JPanel panelTemplates;
    private javax.swing.JScrollPane scrollPanePanelEditXmp;
}
