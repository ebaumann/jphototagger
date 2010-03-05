/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataTemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-01-08
 */
public final class ControllerMetadataTemplateDelete
        extends ControllerMetadataTemplate {
    private JButton buttonDeleteInputHelper =
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates()
            .getButtonDelete();

    public ControllerMetadataTemplateDelete() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemDelete());
        buttonDeleteInputHelper.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == buttonDeleteInputHelper)
                && isInputHelperListItemSelected()) {
            action(getTemplateOfInputHelperList());
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource()
               == PopupMenuMetadataTemplates.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(MetadataTemplate template) {
        String name = template.getName();

        if (MessageDisplayer.confirmYesNo(
                InputHelperDialog.INSTANCE,
                "ControllerMetadataTemplateDelete.Confirm", name)) {
            if (!DatabaseMetadataTemplates.INSTANCE.delete(name)) {
                MessageDisplayer.error(
                    InputHelperDialog.INSTANCE,
                    "ControllerMetadataTemplateDelete.Error", name);
            }
        }

        focusList();
    }
}
