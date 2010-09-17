/*
 * @(#)ControllerEnableInsertMetadataTemplate.java    Created on 2008-09-22
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

import org.jphototagger.program.event.listener.EditMetadataPanelsListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import javax.swing.JButton;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEnableInsertMetadataTemplate
        implements EditMetadataPanelsListener, ActionListener {
    public ControllerEnableInsertMetadataTemplate() {
        listen();
    }

    private void listen() {
        GUI.getEditPanel().addEditMetadataPanelsListener(this);
        GUI.getAppPanel().getComboBoxMetadataTemplates().addActionListener(
            this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        setButtonEnabled();
    }

    private void setButtonEnabled() {
        boolean  editable = GUI.getEditPanel().isEditable();
        AppPanel appPanel = GUI.getAppPanel();
        boolean  selected =
            appPanel.getComboBoxMetadataTemplates().getSelectedIndex() >= 0;
        JButton button = appPanel.getButtonMetadataTemplateInsert();

        button.setEnabled(editable && (selected));
    }

    @Override
    public void editEnabled() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setButtonEnabled();
            }
        });
    }

    @Override
    public void editDisabled() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setButtonEnabled();
            }
        });
    }
}
