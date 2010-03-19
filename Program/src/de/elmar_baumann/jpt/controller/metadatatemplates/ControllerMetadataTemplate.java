/*
 * @(#)ControllerMetadataTemplate.java    Created on 2010-01-08
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.metadatatemplates;

import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.ListModelMetadataTemplates;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataTemplates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JList;

/**
 *
 *
 * @author  Elmar Baumann
 */
public abstract class ControllerMetadataTemplate extends Controller {
    protected abstract void action(MetadataTemplate template);

    public ControllerMetadataTemplate() {
        listen();
    }

    private void listen() {
        listenToKeyEventsOf(
            InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList());
    }

    @Override
    protected void action(ActionEvent evt) {
        action(getTemplateOfPopupMenu());
    }

    @Override
    protected void action(KeyEvent evt) {
        action(getTemplateOfInputHelperList());
    }

    protected void focusList() {
        InputHelperDialog.INSTANCE.toFront();
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList()
            .requestFocusInWindow();
    }

    protected JList getInputHelperList() {
        return InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList();
    }

    protected boolean isInputHelperListItemSelected() {
        return getInputHelperList().getSelectedIndex() >= 0;
    }

    private MetadataTemplate getTemplateOfPopupMenu() {
        int index = PopupMenuMetadataTemplates.INSTANCE.getSelIndex();

        if (index < 0) {
            return null;
        }

        ListModelMetadataTemplates model =
            ModelFactory.INSTANCE.getModel(ListModelMetadataTemplates.class);

        return (MetadataTemplate) model.get(index);
    }

    protected MetadataTemplate getTemplateOfInputHelperList() {
        assert isInputHelperListItemSelected();

        return (MetadataTemplate) getInputHelperList().getSelectedValue();
    }
}
