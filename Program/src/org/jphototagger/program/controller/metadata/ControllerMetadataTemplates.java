package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.ComboBoxModelMetadataTemplates;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.EditMetaDataTemplateDialog;
import org.jphototagger.program.view.panels.AppPanel;

/**
 * Kontrolliert Eingaben bezüglich Metadaten-Templates.
 *
 * @author Elmar Baumann
 */
public final class ControllerMetadataTemplates implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(ControllerMetadataTemplates.class.getName());

    public ControllerMetadataTemplates() {
        listen();
        setButtonsEnabled();
    }

    private void listen() {
        AppPanel appPanel = GUI.getAppPanel();

        appPanel.getComboBoxMetadataTemplates().addActionListener(this);
        appPanel.getButtonMetadataTemplateCreate().addActionListener(this);
        appPanel.getButtonMetadataTemplateUpdate().addActionListener(this);
        appPanel.getButtonMetadataTemplateDelete().addActionListener(this);
        appPanel.getButtonMetadataTemplateInsert().addActionListener(this);
        appPanel.getButtonMetadataTemplateRename().addActionListener(this);
        appPanel.getButtonMetadataTemplateEdit().addActionListener(this);
    }

    private void setButtonsEnabled() {
        AppPanel appPanel = GUI.getAppPanel();
        boolean itemSelected = appPanel.getComboBoxMetadataTemplates().getSelectedItem() != null;

        appPanel.getButtonMetadataTemplateUpdate().setEnabled(itemSelected);
        appPanel.getButtonMetadataTemplateDelete().setEnabled(itemSelected);
        appPanel.getButtonMetadataTemplateRename().setEnabled(itemSelected);
        appPanel.getButtonMetadataTemplateEdit().setEnabled(itemSelected);
    }

    private ComboBoxModelMetadataTemplates getModel() {
        return ModelFactory.INSTANCE.getModel(ComboBoxModelMetadataTemplates.class);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        AppPanel appPanel = GUI.getAppPanel();

        if (source == appPanel.getButtonMetadataTemplateCreate()) {
            createTemplate();
        } else if (source == appPanel.getButtonMetadataTemplateUpdate()) {
            updateTemplate();
        } else if (source == appPanel.getButtonMetadataTemplateDelete()) {
            deleteTemplate();
        } else if (source == appPanel.getButtonMetadataTemplateInsert()) {
            setCurrentTemplateToPanel();
        } else if (source == appPanel.getButtonMetadataTemplateRename()) {
            renameTemplate();
        } else if (source == appPanel.getButtonMetadataTemplateEdit()) {
            editTemplate();
        }

        setButtonsEnabled();
    }

    private void createTemplate() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                final String name = getNewName();

                if (name != null) {
                    MetadataTemplate template = GUI.getEditPanel().getMetadataTemplate();

                    template.setName(name);
                    getModel().insert(template);
                }
            }
        });
    }

    private void deleteTemplate() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o instanceof MetadataTemplate) {
                    MetadataTemplate template = (MetadataTemplate) o;

                    if (confirmDelete(template.getName())) {
                        getModel().delete(template);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Metadata template: Got this object instead a metadata template: {0}", o);
                }
            }
        });
    }

    private void editTemplate() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o instanceof MetadataTemplate) {
                    EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();

                    dlg.setTemplate((MetadataTemplate) o);
                    dlg.setVisible(true);
                }
            }
        });
    }

    private void renameTemplate() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();
                String name = getNewName();

                if (name != null) {
                    getModel().rename((MetadataTemplate) o, name);
                }
            }
        });
    }

    private void updateTemplate() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o instanceof MetadataTemplate) {
                    MetadataTemplate oldTemplate = (MetadataTemplate) o;
                    MetadataTemplate newTemplate = GUI.getEditPanel().getMetadataTemplate();

                    newTemplate.setName(oldTemplate.getName());
                    getModel().update(newTemplate);
                } else {
                    LOGGER.log(Level.WARNING, "Metadata template: Got this object instead a metadata template: {0}", o);
                }
            }
        });
    }

    private void setCurrentTemplateToPanel() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o != null) {
                    MetadataTemplate template = (MetadataTemplate) o;

                    GUI.getEditPanel().setMetadataTemplate(template);
                } else {
                    LOGGER.log(Level.WARNING, "Insert Metadata template: Selected Metadata template == null");
                }
            }
        });
    }

    private String getNewName() {
        boolean exists = true;
        boolean cancel = false;
        String name = null;
        DatabaseMetadataTemplates db = DatabaseMetadataTemplates.INSTANCE;

        while (exists &&!cancel) {
            name = MessageDisplayer.input("ControllerMetadataTemplates.Input.TemplateName", name, getClass().getName());
            exists = (name != null) && db.exists(name);

            if (exists) {
                cancel = rejectOverride(name);
            }

            if (exists && cancel) {
                name = null;
            }
        }

        return name;
    }

    private boolean confirmDelete(String templateName) {
        return MessageDisplayer.confirmYesNo(null, "ControllerMetadataTemplates.Confirm.Delete", templateName);
    }

    private boolean rejectOverride(String name) {
        return !MessageDisplayer.confirmYesNo(null, "ControllerMetadataTemplates.Confirm.OverwriteExistingTemplate",
                name);
    }
}
