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

import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.model.ListModelMetadataTemplates;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMetadataTemplates;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-08
 */
public abstract class ControllerMetadataTemplate extends Controller {

    protected abstract void action(MetadataTemplate template);

    public ControllerMetadataTemplate() {
        listen();
    }

    private void listen() {
        listenToKeyEventsOf(
                InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList()
                );
    }

    @Override
    protected void action(ActionEvent evt) {
        action(getTemplateOfPopupMenu());
    }

    @Override
    protected void action(KeyEvent evt) {
        action(getTemplateOfList());
    }

    protected void focusList() {
        InputHelperDialog.INSTANCE.toFront();
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().requestFocusInWindow();
    }

    private MetadataTemplate getTemplateOfPopupMenu() {
        int                        index = PopupMenuMetadataTemplates.INSTANCE.getSelIndex();
        ListModelMetadataTemplates model = (ListModelMetadataTemplates) PopupMenuMetadataTemplates.INSTANCE.getList().getModel();

        return (MetadataTemplate) model.get(index);
    }

    private MetadataTemplate getTemplateOfList() {
        return (MetadataTemplate) InputHelperDialog.INSTANCE
                .getPanelMetaDataTemplates().getList().getSelectedValue();
    }
}
