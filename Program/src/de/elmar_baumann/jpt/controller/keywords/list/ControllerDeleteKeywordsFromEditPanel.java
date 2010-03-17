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

package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.controller.keywords.tree
    .ControllerDeleteKeywordFromEditPanel;
import de.elmar_baumann.jpt.factory.ControllerFactory;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.lib.event.util.KeyEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-16
 */
public final class ControllerDeleteKeywordsFromEditPanel
        extends ControllerKeywords {
    private final JMenuItem menuItem =
        PopupMenuKeywordsList.INSTANCE.getItemRemoveFromEditPanel();

    public ControllerDeleteKeywordsFromEditPanel() {
        listenToActionsOf(menuItem);
    }

    @Override
    protected void action(List<String> keywords) {
        EditMetadataPanels editPanels =
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels();

        if (editPanels.isEditable()) {
            ControllerDeleteKeywordFromEditPanel ctrl =
                ControllerFactory.INSTANCE.getController(
                    ControllerDeleteKeywordFromEditPanel.class);

            for (String keyword : keywords) {
                ctrl.removeFromEditPanel(keyword);
            }
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == menuItem;
    }
}
