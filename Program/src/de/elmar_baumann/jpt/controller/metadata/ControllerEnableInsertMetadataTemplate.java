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

import de.elmar_baumann.jpt.event.EditMetadataPanelsEvent;
import de.elmar_baumann.jpt.event.listener.EditMetadataPanelsListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-22
 */
public final class ControllerEnableInsertMetadataTemplate
        implements EditMetadataPanelsListener, ActionListener {

    private final JButton            buttonMetadataTemplateInsert = GUI.INSTANCE.getAppPanel().getButtonMetadataTemplateInsert();
    private final JComboBox          comboBoxTemplates            = GUI.INSTANCE.getAppPanel().getComboBoxMetadataTemplates();
    private final EditMetadataPanels editPanels                   = GUI.INSTANCE.getAppPanel().getEditMetadataPanels();

    public ControllerEnableInsertMetadataTemplate() {
        listen();
    }

    private void listen() {
        editPanels.addEditMetadataPanelsListener(this);
        comboBoxTemplates.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setButtonEnabled();
    }

    @Override
    public void actionPerformed(EditMetadataPanelsEvent event) {
        setButtonEnabled();
    }

    private void setButtonEnabled() {
        buttonMetadataTemplateInsert.setEnabled(
                editPanels.isEditable() && comboBoxTemplates.getSelectedIndex() >= 0);
    }
}
