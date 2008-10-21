package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.MetaDataEditTemplate;
import de.elmar_baumann.imv.database.DatabaseMetaDataEditTemplates;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.MetaDataEditPanelEvent;
import de.elmar_baumann.imv.event.MetaDataEditPanelListener;
import de.elmar_baumann.imv.model.ComboBoxModelMetaDataEditTemplates;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.MetadataEditPanelsArray;
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
public class ControllerMetaDataTemplates extends Controller
    implements ActionListener, MetaDataEditPanelListener {

    private DatabaseMetaDataEditTemplates db = DatabaseMetaDataEditTemplates.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private MetadataEditPanelsArray editPanels = appPanel.getEditPanelsArray();
    private JComboBox comboBoxMetaDataTemplates = appPanel.getComboBoxMetaDataTemplates();
    private ComboBoxModelMetaDataEditTemplates model = (ComboBoxModelMetaDataEditTemplates) comboBoxMetaDataTemplates.getModel();
    private JButton buttonMetaDataTemplateCreate = appPanel.getButtonMetaDataTemplateCreate();
    private JButton buttonMetaDataTemplateUpdate = appPanel.getButtonMetaDataTemplateUpdate();
    private JButton buttonMetaDataTemplateDelete = appPanel.getButtonMetaDataTemplateDelete();
    private JButton buttonMetaDataTemplateInsert = appPanel.getButtonMetaDataTemplateInsert();
    private JButton buttonMetaDataTemplateRename = appPanel.getButtonMetaDataTemplateRename();

    public ControllerMetaDataTemplates() {
        listenToActionSources();
        enableButtons();
    }

    private void listenToActionSources() {
        comboBoxMetaDataTemplates.addActionListener(this);
        buttonMetaDataTemplateCreate.addActionListener(this);
        buttonMetaDataTemplateUpdate.addActionListener(this);
        buttonMetaDataTemplateDelete.addActionListener(this);
        buttonMetaDataTemplateInsert.addActionListener(this);
        buttonMetaDataTemplateRename.addActionListener(this);
        ListenerProvider.getInstance().addMetaDataEditPanelListener(this);
    }

    private void enableButtons() {
        boolean itemSelected = comboBoxMetaDataTemplates.getSelectedItem() != null;
        buttonMetaDataTemplateUpdate.setEnabled(itemSelected);
        buttonMetaDataTemplateDelete.setEnabled(itemSelected);
        buttonMetaDataTemplateRename.setEnabled(itemSelected);
        buttonMetaDataTemplateInsert.setEnabled(itemSelected && editPanels.isEditable());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            Object source = e.getSource();
            if (source == buttonMetaDataTemplateCreate) {
                createTemplate();
            } else if (source == buttonMetaDataTemplateUpdate) {
                updateTemplate();
            } else if (source == buttonMetaDataTemplateDelete) {
                deleteTemplate();
            } else if (source == buttonMetaDataTemplateInsert) {
                setCurrentTemplateToPanel();
            } else if (source == buttonMetaDataTemplateRename) {
                renameTemplate();
            }
        }
        enableButtons();
    }

    private void createTemplate() {
        String name = getNewName();
        if (name != null) {
            MetaDataEditTemplate template = editPanels.getMetaDataEditTemplate();
            template.setName(name);
            model.insertTemplate(template);
        }
    }

    private void deleteTemplate() {
        Object o = model.getSelectedItem();
        if (o != null) {
            MetaDataEditTemplate template = (MetaDataEditTemplate) o;
            if (confirmDelete(template.getName())) {
                model.deleteMetaDataEditTemplate(template);
            }
        }
    }

    private boolean confirmDelete(String templateName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerMetaDataTemplates.ConfirmMessage.Delete"));
        Object[] params = {templateName};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("ControllerMetaDataTemplates.ConfirmMessage.Delete.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void renameTemplate() {
        Object o = model.getSelectedItem();
        String name = getNewName();
        if (name != null) {
            model.renameTemplate((MetaDataEditTemplate) o, name);
        }
    }

    private void updateTemplate() {
        Object o = model.getSelectedItem();
        if (o != null) {
            MetaDataEditTemplate oldTemplate = (MetaDataEditTemplate) o;
            MetaDataEditTemplate newTemplate = editPanels.getMetaDataEditTemplate();
            newTemplate.setName(oldTemplate.getName());
            model.updateTemplate(newTemplate);
        }
    }

    private void setCurrentTemplateToPanel() {
        Object o = model.getSelectedItem();
        if (o != null) {
            MetaDataEditTemplate template = (MetaDataEditTemplate) o;
            editPanels.setMetaDataEditTemplate(template);
        }
    }

    private String getNewName() {
        boolean exists = true;
        boolean abort = false;
        String name = null;
        while (exists && !abort) {
            name = JOptionPane.showInputDialog(Bundle.getString("ControllerMetaDataTemplates.Input.TemplateName"), name);
            exists = name != null && db.existsMetaDataEditTemplate(name);
            if (exists) {
                abort = confirmAbort(name);
            }
            if (exists && abort) {
                name = null;
            }
        }
        return name;
    }

    private boolean confirmAbort(String name) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("ControllerMetaDataTemplates.ConfirmMessage.OverwriteExistingTemplate"));
        Object[] params = {name};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("ControllerMetaDataTemplates.ConfirmMessage.OverwriteExistingTemplate.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.NO_OPTION;
    }

    @Override
    public void actionPerformed(MetaDataEditPanelEvent event) {
        MetaDataEditPanelEvent.Type type = event.getType();

        if (type.equals(MetaDataEditPanelEvent.Type.EditEnabled) ||
            type.equals(MetaDataEditPanelEvent.Type.EditDisabled)) {
            buttonMetaDataTemplateInsert.setEnabled(
                type.equals(MetaDataEditPanelEvent.Type.EditEnabled));
        }
    }
}
