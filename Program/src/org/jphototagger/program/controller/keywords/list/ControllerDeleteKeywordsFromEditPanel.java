/*
 * @(#)ControllerDeleteKeywordsFromEditPanel.java    Created on 2010-03-16
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

package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.program.controller.keywords.tree
    .ControllerDeleteKeywordFromEditPanel;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
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
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

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
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == menuItem;
    }
}
