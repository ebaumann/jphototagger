/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.metadatatemplates;

import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.EditMetaDataTemplateDialog;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataTemplates;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class ControllerMetadataTemplateAdd extends ControllerMetadataTemplate {

    private JButton buttonAddEditPanel   = GUI.INSTANCE.getAppPanel().getButtonMetadataTemplateAdd();
    private JButton buttonAddInputHelper = InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonAdd();

    public ControllerMetadataTemplateAdd() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemAdd());
        buttonAddEditPanel.addActionListener(this);
        buttonAddInputHelper.addActionListener(this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControl(evt, KeyEvent.VK_N);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == buttonAddEditPanel || source == buttonAddInputHelper) {
            action((MetadataTemplate) null);
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuMetadataTemplates.INSTANCE.getItemAdd();
    }

    @Override
    protected void action(MetadataTemplate template) {
        EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();
        MetadataTemplate           t   = new MetadataTemplate();

        dlg.setTemplate(t);
        ComponentUtil.show(dlg);
        focusList();
    }
}
