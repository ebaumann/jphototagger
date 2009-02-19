package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.MetadataEditTemplate;
import de.elmar_baumann.imv.database.DatabaseMetadataEditTemplates;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.MetadataEditPanelEvent;
import de.elmar_baumann.imv.event.MetadataEditPanelListener;
import de.elmar_baumann.imv.model.ComboBoxModelMetadataEditTemplates;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * Kontrolliert Eingaben bezüglich Metadaten-Templates.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public final class ControllerMetadataTemplates implements ActionListener, MetadataEditPanelListener {

    private final DatabaseMetadataEditTemplates db = DatabaseMetadataEditTemplates.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();
    private final JComboBox comboBoxMetadataTemplates = appPanel.getComboBoxMetadataTemplates();
    private final ComboBoxModelMetadataEditTemplates model = (ComboBoxModelMetadataEditTemplates) comboBoxMetadataTemplates.getModel();
    private final JButton buttonMetadataTemplateCreate = appPanel.getButtonMetadataTemplateCreate();
    private final JButton buttonMetadataTemplateUpdate = appPanel.getButtonMetadataTemplateUpdate();
    private final JButton buttonMetadataTemplateDelete = appPanel.getButtonMetadataTemplateDelete();
    private final JButton buttonMetadataTemplateInsert = appPanel.getButtonMetadataTemplateInsert();
    private final JButton buttonMetadataTemplateRename = appPanel.getButtonMetadataTemplateRename();

    public ControllerMetadataTemplates() {
        listen();
        enableButtons();
    }

    private void listen() {
        comboBoxMetadataTemplates.addActionListener(this);
        buttonMetadataTemplateCreate.addActionListener(this);
        buttonMetadataTemplateUpdate.addActionListener(this);
        buttonMetadataTemplateDelete.addActionListener(this);
        buttonMetadataTemplateInsert.addActionListener(this);
        buttonMetadataTemplateRename.addActionListener(this);
        ListenerProvider.getInstance().addMetadataEditPanelListener(this);
    }

    private void enableButtons() {
        boolean itemSelected = comboBoxMetadataTemplates.getSelectedItem() != null;
        buttonMetadataTemplateUpdate.setEnabled(itemSelected);
        buttonMetadataTemplateDelete.setEnabled(itemSelected);
        buttonMetadataTemplateRename.setEnabled(itemSelected);
        buttonMetadataTemplateInsert.setEnabled(itemSelected && editPanels.isEditable());
    }

    @Override
    public void actionPerformed(MetadataEditPanelEvent event) {
        MetadataEditPanelEvent.Type type = event.getType();

        if (type.equals(MetadataEditPanelEvent.Type.EDIT_ENABLED) ||
                type.equals(MetadataEditPanelEvent.Type.EDIT_DISABLED)) {
            buttonMetadataTemplateInsert.setEnabled(
                    type.equals(MetadataEditPanelEvent.Type.EDIT_ENABLED));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == buttonMetadataTemplateCreate) {
            createTemplate();
        } else if (source == buttonMetadataTemplateUpdate) {
            updateTemplate();
        } else if (source == buttonMetadataTemplateDelete) {
            deleteTemplate();
        } else if (source == buttonMetadataTemplateInsert) {
            setCurrentTemplateToPanel();
        } else if (source == buttonMetadataTemplateRename) {
            renameTemplate();
        }
        enableButtons();
    }

    private void createTemplate() {
        String name = getNewName();
        if (name != null) {
            MetadataEditTemplate template = editPanels.getMetadataEditTemplate();
            template.setName(name);
            model.insertTemplate(template);
        }
    }

    private void deleteTemplate() {
        Object o = model.getSelectedItem();
        if (o != null) {
            MetadataEditTemplate template = (MetadataEditTemplate) o;
            if (deleteConfirmed(template.getName())) {
                model.deleteMetadataEditTemplate(template);
            }
        }
    }

    private boolean deleteConfirmed(String templateName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerMetadataTemplates.ConfirmMessage.Delete"));
        Object[] params = {templateName};
        return JOptionPane.showConfirmDialog(
                null,
                msg.format(params),
                Bundle.getString("ControllerMetadataTemplates.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void renameTemplate() {
        Object o = model.getSelectedItem();
        String name = getNewName();
        if (name != null) {
            model.renameTemplate((MetadataEditTemplate) o, name);
        }
    }

    private void updateTemplate() {
        Object o = model.getSelectedItem();
        if (o != null) {
            MetadataEditTemplate oldTemplate = (MetadataEditTemplate) o;
            MetadataEditTemplate newTemplate = editPanels.getMetadataEditTemplate();
            newTemplate.setName(oldTemplate.getName());
            model.updateTemplate(newTemplate);
        }
    }

    private void setCurrentTemplateToPanel() {
        Object o = model.getSelectedItem();
        if (o != null) {
            MetadataEditTemplate template = (MetadataEditTemplate) o;
            editPanels.setMetadataEditTemplate(template);
        }
    }

    private String getNewName() {
        boolean exists = true;
        boolean abort = false;
        String name = null;
        while (exists && !abort) {
            name = JOptionPane.showInputDialog(Bundle.getString("ControllerMetadataTemplates.Input.TemplateName"), name);
            exists = name != null && db.existsMetadataEditTemplate(name);
            if (exists) {
                abort = abortConfirmed(name);
            }
            if (exists && abort) {
                name = null;
            }
        }
        return name;
    }

    private boolean abortConfirmed(String name) {
        MessageFormat msg = new MessageFormat(
                Bundle.getString("ControllerMetadataTemplates.ConfirmMessage.OverwriteExistingTemplate"));
        Object[] params = {name};
        return JOptionPane.showConfirmDialog(
                null,
                msg.format(params),
                Bundle.getString("ControllerMetadataTemplates.ConfirmMessage.OverwriteExistingTemplate.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppIcons.getMediumAppIcon()) == JOptionPane.NO_OPTION;
    }
}
