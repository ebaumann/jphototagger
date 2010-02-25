/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.ComboBoxModelMetadataTemplates;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.EditMetaDataTemplateDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert Eingaben bezüglich Metadaten-Templates.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-22
 */
public final class ControllerMetadataTemplates implements ActionListener {

    private final DatabaseMetadataTemplates      db                           = DatabaseMetadataTemplates.INSTANCE;
    private final AppPanel                       appPanel                     = GUI.INSTANCE.getAppPanel();
    private final EditMetadataPanels             editPanels                   = appPanel.getEditMetadataPanels();
    private final JComboBox                      comboBoxMetadataTemplates    = appPanel.getComboBoxMetadataTemplates();
    private final ComboBoxModelMetadataTemplates model                        = ModelFactory.INSTANCE.getModel(ComboBoxModelMetadataTemplates.class);
    private final JButton                        buttonMetadataTemplateCreate = appPanel.getButtonMetadataTemplateCreate();
    private final JButton                        buttonMetadataTemplateUpdate = appPanel.getButtonMetadataTemplateUpdate();
    private final JButton                        buttonMetadataTemplateDelete = appPanel.getButtonMetadataTemplateDelete();
    private final JButton                        buttonMetadataTemplateInsert = appPanel.getButtonMetadataTemplateInsert();
    private final JButton                        buttonMetadataTemplateRename = appPanel.getButtonMetadataTemplateRename();
    private final JButton                        buttonMetadataTemplateEdit   = appPanel.getButtonMetadataTemplateEdit();

    public ControllerMetadataTemplates() {
        listen();
        setButtonsEnabled();
    }

    private void listen() {
        comboBoxMetadataTemplates.addActionListener(this);
        buttonMetadataTemplateCreate.addActionListener(this);
        buttonMetadataTemplateUpdate.addActionListener(this);
        buttonMetadataTemplateDelete.addActionListener(this);
        buttonMetadataTemplateInsert.addActionListener(this);
        buttonMetadataTemplateRename.addActionListener(this);
        buttonMetadataTemplateEdit.addActionListener(this);
    }

    private void setButtonsEnabled() {
        boolean itemSelected = comboBoxMetadataTemplates.getSelectedItem() != null;
        buttonMetadataTemplateUpdate.setEnabled(itemSelected);
        buttonMetadataTemplateDelete.setEnabled(itemSelected);
        buttonMetadataTemplateRename.setEnabled(itemSelected);
        buttonMetadataTemplateEdit  .setEnabled(itemSelected);
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
        } else if (source == buttonMetadataTemplateEdit) {
            editTemplate();
        }
        setButtonsEnabled();
    }

    private void createTemplate() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final String name = getNewName();
                if (name != null) {
                    MetadataTemplate template = editPanels.getMetadataTemplate();
                    template.setName(name);
                    model.insert(template);
                }
            }
        });
    }

    private void deleteTemplate() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Object o = model.getSelectedItem();
                if (o instanceof MetadataTemplate) {
                    MetadataTemplate template = (MetadataTemplate) o;
                    if (confirmDelete(template.getName())) {
                        model.delete(template);
                    }
                } else {
                    AppLogger.logWarning(ControllerMetadataTemplates.class, "ControllerMetadataTemplates.Error.WrongObject", o);
                }
            }
        });
    }

    private void editTemplate() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Object o = model.getSelectedItem();
                if (o instanceof MetadataTemplate) {
                    EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();
                    dlg.setTemplate((MetadataTemplate) o);
                    dlg.setVisible(true);
                }
            }
        });
    }

    private void renameTemplate() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Object o = model.getSelectedItem();
                String name = getNewName();
                if (name != null) {
                    model.rename((MetadataTemplate) o, name);
                }
            }
        });
    }

    private void updateTemplate() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Object o = model.getSelectedItem();
                if (o instanceof MetadataTemplate) {
                    MetadataTemplate oldTemplate = (MetadataTemplate) o;
                    MetadataTemplate newTemplate = editPanels.getMetadataTemplate();
                    newTemplate.setName(oldTemplate.getName());
                    model.update(newTemplate);
                } else {
                    AppLogger.logWarning(ControllerMetadataTemplates.class, "ControllerMetadataTemplates.Error.WrongObject", o);
                }
            }
        });
    }

    private void setCurrentTemplateToPanel() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Object o = model.getSelectedItem();
                if (o != null) {
                    MetadataTemplate template = (MetadataTemplate) o;
                    editPanels.setMetadataTemplate(template);
                } else {
                    AppLogger.logWarning(ControllerMetadataTemplates.class, "ControllerMetadataTemplates.Error.InsertTemplateIsNull");
                }
            }
        });
    }

    private String getNewName() {
        boolean exists = true;
        boolean abort = false;
        String name = null;
        while (exists && !abort) {
            name = MessageDisplayer.input("ControllerMetadataTemplates.Input.TemplateName", name, getClass().getName());
            exists = name != null && db.exists(name);
            if (exists) {
                abort = confirmOverride(name);
            }
            if (exists && abort) {
                name = null;
            }
        }
        return name;
    }

    private boolean confirmDelete(String templateName) {
        return MessageDisplayer.confirmYesNo(null, "ControllerMetadataTemplates.Confirm.Delete", templateName);
    }

    private boolean confirmOverride(String name) {
        return !MessageDisplayer.confirmYesNo(null, "ControllerMetadataTemplates.Confirm.OverwriteExistingTemplate", name);
    }
}
