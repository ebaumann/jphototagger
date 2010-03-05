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
import de.elmar_baumann.jpt.view.dialogs.EditMetaDataTemplateDialog;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataTemplates;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public final class ControllerMetadataTemplateEdit
        extends    ControllerMetadataTemplate
        implements MouseListener
{

    private JButton buttonEditInputHelper = InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonEdit();

    public ControllerMetadataTemplateEdit() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemEdit());
        listenToDoubleClick();
        buttonEditInputHelper.addActionListener(this);
    }

    private void listenToDoubleClick() {
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == buttonEditInputHelper && isInputHelperListItemSelected()) {
            action(getTemplateOfInputHelperList());
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControl(evt, KeyEvent.VK_E);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuMetadataTemplates.INSTANCE.getItemEdit();
    }

    @Override
    protected void action(MetadataTemplate template) {
        EditMetaDataTemplateDialog dlg = new EditMetaDataTemplateDialog();

        dlg.setTemplate(template);
        ComponentUtil.show(dlg);
        focusList();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MouseEventUtil.isDoubleClick(e)) {
            Object selValue = InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().getSelectedValue();
            if (selValue != null) {
                action((MetadataTemplate) selValue);
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
