/*
 * @(#)ControllerMetadataTemplates.java    Created on 2008-09-22
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.ComboBoxModelMetadataTemplates;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.EditMetaDataTemplateDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

/**
 * Kontrolliert Eingaben bezüglich Metadaten-Templates.
 *
 * @author  Elmar Baumann
 */
public final class ControllerMetadataTemplates implements ActionListener {
    public ControllerMetadataTemplates() {
        listen();
        setButtonsEnabled();
    }

    private void listen() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

        appPanel.getComboBoxMetadataTemplates().addActionListener(this);
        appPanel.getButtonMetadataTemplateCreate().addActionListener(this);
        appPanel.getButtonMetadataTemplateUpdate().addActionListener(this);
        appPanel.getButtonMetadataTemplateDelete().addActionListener(this);
        appPanel.getButtonMetadataTemplateInsert().addActionListener(this);
        appPanel.getButtonMetadataTemplateRename().addActionListener(this);
        appPanel.getButtonMetadataTemplateEdit().addActionListener(this);
    }

    private void setButtonsEnabled() {
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        boolean  itemSelected =
            appPanel.getComboBoxMetadataTemplates().getSelectedItem() != null;

        appPanel.getButtonMetadataTemplateUpdate().setEnabled(itemSelected);
        appPanel.getButtonMetadataTemplateDelete().setEnabled(itemSelected);
        appPanel.getButtonMetadataTemplateRename().setEnabled(itemSelected);
        appPanel.getButtonMetadataTemplateEdit().setEnabled(itemSelected);
    }

    private ComboBoxModelMetadataTemplates getModel() {
        return ModelFactory.INSTANCE.getModel(
            ComboBoxModelMetadataTemplates.class);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object   source   = evt.getSource();
        AppPanel appPanel = GUI.INSTANCE.getAppPanel();

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
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String name = getNewName();

                if (name != null) {
                    MetadataTemplate template =
                        ViewUtil.getEditPanel().getMetadataTemplate();

                    template.setName(name);
                    getModel().insert(template);
                }
            }
        });
    }

    private void deleteTemplate() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o instanceof MetadataTemplate) {
                    MetadataTemplate template = (MetadataTemplate) o;

                    if (confirmDelete(template.getName())) {
                        getModel().delete(template);
                    }
                } else {
                    AppLogger.logWarning(
                        ControllerMetadataTemplates.class,
                        "ControllerMetadataTemplates.Error.WrongObject", o);
                }
            }
        });
    }

    private void editTemplate() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o instanceof MetadataTemplate) {
                    EditMetaDataTemplateDialog dlg =
                        new EditMetaDataTemplateDialog();

                    dlg.setTemplate((MetadataTemplate) o);
                    dlg.setVisible(true);
                }
            }
        });
    }

    private void renameTemplate() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object o    = getModel().getSelectedItem();
                String name = getNewName();

                if (name != null) {
                    getModel().rename((MetadataTemplate) o, name);
                }
            }
        });
    }

    private void updateTemplate() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o instanceof MetadataTemplate) {
                    MetadataTemplate oldTemplate = (MetadataTemplate) o;
                    MetadataTemplate newTemplate =
                        ViewUtil.getEditPanel().getMetadataTemplate();

                    newTemplate.setName(oldTemplate.getName());
                    getModel().update(newTemplate);
                } else {
                    AppLogger.logWarning(
                        ControllerMetadataTemplates.class,
                        "ControllerMetadataTemplates.Error.WrongObject", o);
                }
            }
        });
    }

    private void setCurrentTemplateToPanel() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object o = getModel().getSelectedItem();

                if (o != null) {
                    MetadataTemplate template = (MetadataTemplate) o;

                    ViewUtil.getEditPanel().setMetadataTemplate(template);
                } else {
                    AppLogger.logWarning(
                        ControllerMetadataTemplates.class,
                        "ControllerMetadataTemplates.Error.InsertTemplateIsNull");
                }
            }
        });
    }

    private String getNewName() {
        boolean                   exists = true;
        boolean                   cancel = false;
        String                    name   = null;
        DatabaseMetadataTemplates db     = DatabaseMetadataTemplates.INSTANCE;

        while (exists &&!cancel) {
            name = MessageDisplayer.input(
                "ControllerMetadataTemplates.Input.TemplateName", name,
                getClass().getName());
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
        return MessageDisplayer.confirmYesNo(null,
                "ControllerMetadataTemplates.Confirm.Delete", templateName);
    }

    private boolean rejectOverride(String name) {
        return !MessageDisplayer.confirmYesNo(
            null,
            "ControllerMetadataTemplates.Confirm.OverwriteExistingTemplate",
            name);
    }
}
