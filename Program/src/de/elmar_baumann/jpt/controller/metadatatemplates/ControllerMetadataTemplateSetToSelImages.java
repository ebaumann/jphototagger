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

import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.resource.GUI;
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
public final class ControllerMetadataTemplateSetToSelImages extends ControllerMetadataTemplate {

    private JButton buttonAddToSelImagesInputHelper = InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getButtonAddToSelImages();

    public ControllerMetadataTemplateSetToSelImages() {
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuMetadataTemplates.INSTANCE.getItemSetToSelImages());
        buttonAddToSelImagesInputHelper.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == buttonAddToSelImagesInputHelper && isInputHelperListItemSelected()) {
            action(getTemplateOfInputHelperList());
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_INSERT;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuMetadataTemplates.INSTANCE.getItemSetToSelImages();
    }

    @Override
    protected void action(MetadataTemplate template) {
        if (!imagesSelected()) return;
        GUI.INSTANCE.getAppPanel().getEditMetadataPanels().setMetadataTemplate(template);
        focusList();
   }

    private boolean imagesSelected() {
        return GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectionCount() > 0;
    }

}
