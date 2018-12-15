package org.jphototagger.program.module.editmetadata;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.metadatatemplates.EditMetaDataTemplateDialog;
import org.jphototagger.program.module.metadatatemplates.MetadataTemplateUtil;
import org.jphototagger.program.module.metadatatemplates.MetadataTemplatesComboBoxModel;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class EditMetaDataActionsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final EditMetaDataPanels editMetaDataPanels;
    private final MetadataTemplatesComboBoxModel metadataTemplatesComboBoxModel = new MetadataTemplatesComboBoxModel();
    private final MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);
    private boolean aFileSelected;

    EditMetaDataActionsPanel(EditMetaDataPanels editMetaDataPanels) {
        this.editMetaDataPanels = editMetaDataPanels;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setTemplateName();
        setTemplateButtonsEnabled();
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    private void setTemplateName() {
        Object selectedItem = comboBoxMetadataTemplates.getSelectedItem();

        labelTemplateName.setText(selectedItem == null
                ? Bundle.getString(EditMetaDataActionsPanel.class, "EditMetadataActionsPanel.labelTemplateName.NoTemplateSelected")
                : selectedItem.toString());
    }

    List<Character> getButtonsMnemonicChars() {
        List<Character> mnemonicChars = new ArrayList<>();

        mnemonicChars.add((char) buttonEmptyMetadata.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateCreate.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateDelete.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateEdit.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateInsert.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateRename.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateUpdate.getMnemonic());
        mnemonicChars.add((char) buttonMetadataTemplateAdd.getMnemonic());
        mnemonicChars.add((char) labelPromptCurrentTemplate.getDisplayedMnemonic());

        return mnemonicChars;
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(ThumbnailsSelectionChangedEvent evt) {
        aFileSelected = evt.isAFileSelected();
        boolean editable = editMetaDataPanels.canEdit(evt.getSelectedFiles());
        buttonEmptyMetadata.setEnabled(aFileSelected && editable);
        buttonMetadataTemplateCreate.setEnabled(aFileSelected);
        boolean metaDataTemplateSelected = isMetaDataTemplateSelected();
        buttonMetadataTemplateInsert.setEnabled(metaDataTemplateSelected && aFileSelected && editable);
        buttonMetadataTemplateUpdate.setEnabled(aFileSelected && metaDataTemplateSelected);
    }

    private void setTemplateButtonsEnabled() {
        boolean templateSelected = isMetaDataTemplateSelected();
        buttonMetadataTemplateRename.setEnabled(templateSelected);
        buttonMetadataTemplateDelete.setEnabled(templateSelected);
        buttonMetadataTemplateEdit.setEnabled(templateSelected);
        boolean metaDataTemplateSelected = isMetaDataTemplateSelected();
        buttonMetadataTemplateUpdate.setEnabled(aFileSelected && metaDataTemplateSelected);
        buttonMetadataTemplateInsert.setEnabled(aFileSelected && metaDataTemplateSelected);
    }

    private boolean isMetaDataTemplateSelected() {
        return comboBoxMetadataTemplates.getSelectedIndex() >= 0;
    }

    private void emptyMetaData() {
        editMetaDataPanels.emptyAllEditPanels();
    }

    private void insertSelectedTemplateIntoEditPanels() {
        Object selectedItem = metadataTemplatesComboBoxModel.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        MetadataTemplate selectedTemplate = (MetadataTemplate) selectedItem;
        editMetaDataPanels.setMetadataTemplate(selectedTemplate);
    }

    private void createTemplateFromEditMetaDataPanels() {
        MetadataTemplate template = editMetaDataPanels.createMetadataTemplateFromInput();
        String defaultName = null;
        String gotName = MetadataTemplateUtil.getNewTemplateName(defaultName);
        if (gotName != null) {
            template.setName(gotName);
            repo.saveOrUpdateMetadataTemplate(template);
        }
    }

    private void deleteSelectedTemplate() {
        Object selectedItem = metadataTemplatesComboBoxModel.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        MetadataTemplate selectedTemplate = (MetadataTemplate) selectedItem;
        String templateName = selectedTemplate.getName();
        if (confirmDelete(templateName)) {
            repo.deleteMetadataTemplate(templateName);
        }
    }

    private boolean confirmDelete(String templateName) {
        String message = Bundle.getString(EditMetaDataActionsPanel.class, "EditMetaDataActionsPanel.Confirm.Delete", templateName);
        return MessageDisplayer.confirmYesNo(null, message);
    }

    private void updateSelectedTemplateFromEditMetaDataPanels() {
        Object selectedItem = metadataTemplatesComboBoxModel.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        MetadataTemplate oldTemplate = (MetadataTemplate) selectedItem;
        MetadataTemplate newTemplate = editMetaDataPanels.createMetadataTemplateFromInput();
        newTemplate.setName(oldTemplate.getName());
        repo.updateMetadataTemplate(newTemplate);
    }

    private void editSelectedTemplate() {
        Object selectedItem = metadataTemplatesComboBoxModel.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        EditMetaDataTemplateDialog dialog = new EditMetaDataTemplateDialog();
        MetadataTemplate selectedTemplate = (MetadataTemplate) selectedItem;
        dialog.setTemplate(selectedTemplate);
        dialog.setVisible(true);
    }

    private void renameSelectedTemplate() {
        Object selectedItem = metadataTemplatesComboBoxModel.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        MetadataTemplate selectedTemplate = (MetadataTemplate) selectedItem;
        String fromName = selectedTemplate.getName();
        String toName = MetadataTemplateUtil.getNewTemplateName(fromName);
        if (toName != null) {
            repo.updateRenameMetadataTemplate(fromName, toName);
        }
    }

    private void createNewTemplate() {
        MetadataTemplate template = new MetadataTemplate();
        EditMetaDataTemplateDialog dialog = new EditMetaDataTemplateDialog();
        dialog.setTemplate(template);
        dialog.setVisible(true);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = UiFactory.tabbedPane();
        panelGroupMetadataEdit = UiFactory.panel();
        panelTemplateNameInfo = UiFactory.panel();
        labelPromptInfoCurrentTemplate = UiFactory.label();
        labelTemplateName = UiFactory.label();
        panelEditButtons = UiFactory.panel();
        buttonMetadataTemplateInsert = UiFactory.button();
        buttonMetadataTemplateCreate = UiFactory.button();
        buttonEmptyMetadata = UiFactory.button();
        panelGroupMetadataTemplates = UiFactory.panel();
        panelComboBox = UiFactory.panel();
        labelPromptCurrentTemplate = UiFactory.label();
        comboBoxMetadataTemplates = UiFactory.comboBox();
        panelTemplateButtons = UiFactory.panel();
        buttonMetadataTemplateRename = UiFactory.button();
        buttonMetadataTemplateUpdate = UiFactory.button();
        buttonMetadataTemplateDelete = UiFactory.button();
        buttonMetadataTemplateEdit = UiFactory.button();
        buttonMetadataTemplateAdd = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelGroupMetadataEdit.setName("panelGroupMetadataEdit"); // NOI18N
        panelGroupMetadataEdit.setLayout(new java.awt.GridBagLayout());

        panelTemplateNameInfo.setName("panelTemplateNameInfo"); // NOI18N
        panelTemplateNameInfo.setLayout(new java.awt.GridBagLayout());

        labelPromptInfoCurrentTemplate.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.labelPromptInfoCurrentTemplate.text")); // NOI18N
        labelPromptInfoCurrentTemplate.setName("labelPromptInfoCurrentTemplate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelTemplateNameInfo.add(labelPromptInfoCurrentTemplate, gridBagConstraints);

        labelTemplateName.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.labelTemplateName.text")); // NOI18N
        labelTemplateName.setName("labelTemplateName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTemplateNameInfo.add(labelTemplateName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelGroupMetadataEdit.add(panelTemplateNameInfo, gridBagConstraints);

        panelEditButtons.setName("panelEditButtons"); // NOI18N
        panelEditButtons.setLayout(new java.awt.GridBagLayout());

        buttonMetadataTemplateInsert.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateInsert.text")); // NOI18N
        buttonMetadataTemplateInsert.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateInsert.toolTipText")); // NOI18N
        buttonMetadataTemplateInsert.setEnabled(false);
        buttonMetadataTemplateInsert.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonMetadataTemplateInsert.setName("buttonMetadataTemplateInsert"); // NOI18N
        buttonMetadataTemplateInsert.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateInsertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelEditButtons.add(buttonMetadataTemplateInsert, gridBagConstraints);

        buttonMetadataTemplateCreate.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateCreate.text")); // NOI18N
        buttonMetadataTemplateCreate.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateCreate.toolTipText")); // NOI18N
        buttonMetadataTemplateCreate.setEnabled(false);
        buttonMetadataTemplateCreate.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonMetadataTemplateCreate.setName("buttonMetadataTemplateCreate"); // NOI18N
        buttonMetadataTemplateCreate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateCreateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelEditButtons.add(buttonMetadataTemplateCreate, gridBagConstraints);

        buttonEmptyMetadata.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonEmptyMetadata.text")); // NOI18N
        buttonEmptyMetadata.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonEmptyMetadata.toolTipText")); // NOI18N
        buttonEmptyMetadata.setEnabled(false);
        buttonEmptyMetadata.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonEmptyMetadata.setName("buttonEmptyMetadata"); // NOI18N
        buttonEmptyMetadata.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEmptyMetadataActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelEditButtons.add(buttonEmptyMetadata, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelGroupMetadataEdit.add(panelEditButtons, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "EditMetaDataActionsPanel.panelGroupMetadataEdit.TabConstraints.tabTitle"), panelGroupMetadataEdit); // NOI18N

        panelGroupMetadataTemplates.setName("panelGroupMetadataTemplates"); // NOI18N
        panelGroupMetadataTemplates.setLayout(new java.awt.GridBagLayout());

        panelComboBox.setName("panelComboBox"); // NOI18N
        panelComboBox.setLayout(new java.awt.GridBagLayout());

        labelPromptCurrentTemplate.setLabelFor(comboBoxMetadataTemplates);
        labelPromptCurrentTemplate.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.labelPromptCurrentTemplate.text")); // NOI18N
        labelPromptCurrentTemplate.setName("labelPromptCurrentTemplate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelComboBox.add(labelPromptCurrentTemplate, gridBagConstraints);

        comboBoxMetadataTemplates.setModel(metadataTemplatesComboBoxModel);
        comboBoxMetadataTemplates.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.comboBoxMetadataTemplates.toolTipText")); // NOI18N
        comboBoxMetadataTemplates.setName("comboBoxMetadataTemplates"); // NOI18N
        comboBoxMetadataTemplates.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxMetadataTemplatesActionPerformed(evt);
            }
        });
        comboBoxMetadataTemplates.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxMetadataTemplatesPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelComboBox.add(comboBoxMetadataTemplates, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelGroupMetadataTemplates.add(panelComboBox, gridBagConstraints);

        panelTemplateButtons.setName("panelTemplateButtons"); // NOI18N
        panelTemplateButtons.setLayout(new java.awt.GridLayout(3, 0, UiFactory.scale(5), UiFactory.scale(5)));

        buttonMetadataTemplateRename.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateRename.text")); // NOI18N
        buttonMetadataTemplateRename.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateRename.toolTipText")); // NOI18N
        buttonMetadataTemplateRename.setEnabled(false);
        buttonMetadataTemplateRename.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonMetadataTemplateRename.setName("buttonMetadataTemplateRename"); // NOI18N
        buttonMetadataTemplateRename.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateRenameActionPerformed(evt);
            }
        });
        panelTemplateButtons.add(buttonMetadataTemplateRename);

        buttonMetadataTemplateUpdate.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateUpdate.text")); // NOI18N
        buttonMetadataTemplateUpdate.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateUpdate.toolTipText")); // NOI18N
        buttonMetadataTemplateUpdate.setEnabled(false);
        buttonMetadataTemplateUpdate.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonMetadataTemplateUpdate.setName("buttonMetadataTemplateUpdate"); // NOI18N
        buttonMetadataTemplateUpdate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateUpdateActionPerformed(evt);
            }
        });
        panelTemplateButtons.add(buttonMetadataTemplateUpdate);

        buttonMetadataTemplateDelete.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateDelete.text")); // NOI18N
        buttonMetadataTemplateDelete.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateDelete.toolTipText")); // NOI18N
        buttonMetadataTemplateDelete.setEnabled(false);
        buttonMetadataTemplateDelete.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonMetadataTemplateDelete.setName("buttonMetadataTemplateDelete"); // NOI18N
        buttonMetadataTemplateDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateDeleteActionPerformed(evt);
            }
        });
        panelTemplateButtons.add(buttonMetadataTemplateDelete);

        buttonMetadataTemplateEdit.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateEdit.text")); // NOI18N
        buttonMetadataTemplateEdit.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateEdit.toolTipText")); // NOI18N
        buttonMetadataTemplateEdit.setEnabled(false);
        buttonMetadataTemplateEdit.setName("buttonMetadataTemplateEdit"); // NOI18N
        buttonMetadataTemplateEdit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateEditActionPerformed(evt);
            }
        });
        panelTemplateButtons.add(buttonMetadataTemplateEdit);

        buttonMetadataTemplateAdd.setText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateAdd.text")); // NOI18N
        buttonMetadataTemplateAdd.setToolTipText(Bundle.getString(getClass(), "EditMetaDataActionsPanel.buttonMetadataTemplateAdd.toolTipText")); // NOI18N
        buttonMetadataTemplateAdd.setMargin(UiFactory.insets(2, 2, 2, 2));
        buttonMetadataTemplateAdd.setName("buttonMetadataTemplateAdd"); // NOI18N
        buttonMetadataTemplateAdd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMetadataTemplateAddActionPerformed(evt);
            }
        });
        panelTemplateButtons.add(buttonMetadataTemplateAdd);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelGroupMetadataTemplates.add(panelTemplateButtons, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "EditMetaDataActionsPanel.panelGroupMetadataTemplates.TabConstraints.tabTitle"), panelGroupMetadataTemplates); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }

    private void comboBoxMetadataTemplatesPropertyChange(java.beans.PropertyChangeEvent evt) {
        setTemplateName();
    }

    private void comboBoxMetadataTemplatesActionPerformed(java.awt.event.ActionEvent evt) {
        setTemplateName();
        setTemplateButtonsEnabled();
    }

    private void buttonEmptyMetadataActionPerformed(java.awt.event.ActionEvent evt) {
        emptyMetaData();
    }

    private void buttonMetadataTemplateCreateActionPerformed(java.awt.event.ActionEvent evt) {
        createTemplateFromEditMetaDataPanels();
    }

    private void buttonMetadataTemplateUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        updateSelectedTemplateFromEditMetaDataPanels();
    }

    private void buttonMetadataTemplateDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        deleteSelectedTemplate();
    }

    private void buttonMetadataTemplateInsertActionPerformed(java.awt.event.ActionEvent evt) {
        insertSelectedTemplateIntoEditPanels();
    }

    private void buttonMetadataTemplateRenameActionPerformed(java.awt.event.ActionEvent evt) {
        renameSelectedTemplate();
    }

    private void buttonMetadataTemplateEditActionPerformed(java.awt.event.ActionEvent evt) {
        editSelectedTemplate();
    }

    private void buttonMetadataTemplateAddActionPerformed(java.awt.event.ActionEvent evt) {
        createNewTemplate();
    }

    private javax.swing.JButton buttonEmptyMetadata;
    private javax.swing.JButton buttonMetadataTemplateAdd;
    private javax.swing.JButton buttonMetadataTemplateCreate;
    private javax.swing.JButton buttonMetadataTemplateDelete;
    private javax.swing.JButton buttonMetadataTemplateEdit;
    private javax.swing.JButton buttonMetadataTemplateInsert;
    private javax.swing.JButton buttonMetadataTemplateRename;
    private javax.swing.JButton buttonMetadataTemplateUpdate;
    private javax.swing.JComboBox<Object> comboBoxMetadataTemplates;
    private javax.swing.JLabel labelPromptCurrentTemplate;
    private javax.swing.JLabel labelPromptInfoCurrentTemplate;
    private javax.swing.JLabel labelTemplateName;
    private javax.swing.JPanel panelComboBox;
    private javax.swing.JPanel panelEditButtons;
    private javax.swing.JPanel panelGroupMetadataEdit;
    private javax.swing.JPanel panelGroupMetadataTemplates;
    private javax.swing.JPanel panelTemplateButtons;
    private javax.swing.JPanel panelTemplateNameInfo;
    public javax.swing.JTabbedPane tabbedPane;
}
