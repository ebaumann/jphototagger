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
package de.elmar_baumann.jpt.controller.metadatatemplates;

import de.elmar_baumann.jpt.data.MetadataEditTemplate;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.EditMetaDataTemplateDialog;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataEditTemplates;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class ControllerMetadataEditTemplateEdit
        extends    ControllerMetadataEditTemplate
        implements MouseListener
{

    public ControllerMetadataEditTemplateEdit() {
        listen();
    }

    private void listen() {
        InputHelperDialog.INSTANCE.getPanelMetaDataEditTemplates().getList().addMouseListener(this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControl(evt, KeyEvent.VK_E);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuMetadataEditTemplates.INSTANCE.getItemEdit();
    }

    @Override
    protected void action(MetadataEditTemplate template) {
        EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();

        dlg.setTemplate(template);
        dlg.setVisible(true);
        dlg.toFront();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MouseEventUtil.isDoubleClick(e)) {
            Object selValue = InputHelperDialog.INSTANCE.getPanelMetaDataEditTemplates().getList().getSelectedValue();
            if (selValue != null) {
                action((MetadataEditTemplate) selValue);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ignore
    }

}
