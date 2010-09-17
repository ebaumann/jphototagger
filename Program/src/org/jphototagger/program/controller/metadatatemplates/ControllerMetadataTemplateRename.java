/*
 * @(#)ControllerMetadataTemplateRename.java    Created on 2010-01-08
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

package org.jphototagger.program.controller.metadatatemplates;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.helper.MetadataTemplateHelper;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerMetadataTemplateRename
        extends ControllerMetadataTemplate {
    public ControllerMetadataTemplateRename() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemRename());
        getRenameButton().addActionListener(this);
    }

    private JButton getRenameButton() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates()
            .getButtonRename();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if ((evt.getSource() == getRenameButton())
                && isInputHelperListItemSelected()) {
            action(getTemplateOfInputHelperList());
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuMetadataTemplates.INSTANCE.getItemRename();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        String fromName = template.getName();
        String toName   = MetadataTemplateHelper.getNewTemplateName(fromName);

        if (toName != null) {
            if (!DatabaseMetadataTemplates.INSTANCE.updateRename(fromName,
                    toName)) {
                MessageDisplayer.error(
                    null, "ControllerMetadataTemplateRename.Error", fromName);
            }
        }

        focusList();
    }
}
