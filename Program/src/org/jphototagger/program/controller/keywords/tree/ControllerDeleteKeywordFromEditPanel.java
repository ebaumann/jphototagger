/*
 * @(#)ControllerDeleteKeywordFromEditPanel.java    Created on 2009-07-26
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

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item
 * {@link PopupMenuKeywordsTree#getItemRemoveFromEditPanel()} and on action
 * removes the selected keyword from the edit panel.  If a keyword was already
 * present it is removed again.
 *
 * Also listens to key events and does the same if Ctrl+D was pressed.
 *
 * @author  Martin Pohlack
 */
public class ControllerDeleteKeywordFromEditPanel extends ControllerKeywords
        implements ActionListener, KeyListener {
    public ControllerDeleteKeywordFromEditPanel(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return false;
    }

    @Override
    protected void localAction(List<DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        DefaultMutableTreeNode node    = nodes.get(0);
        String                 keyword = getKeyword(node);

        if (keyword != null) {
            removeFromEditPanel(keyword);
        }
    }

    public void removeFromEditPanel(String keyword) {
        EditMetadataPanels editPanels =
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels();
        JPanel panel =
            editPanels.getEditPanel(ColumnXmpDcSubjectsSubject.INSTANCE);

        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel =
                (EditRepeatableTextEntryPanel) panel;

            if (editPanel.isEditable()) {
                editPanel.removeText(keyword);
                editPanels.checkSaveOnChanges();
                KeywordsHelper.removeHighlightKeyword(keyword);
            } else {
                MessageDisplayer.error(
                    null,
                    "ControllerDeleteKeywordFromEditPanel.Error.EditDisabled");
            }
        } else {
            MessageDisplayer.error(
                null, "ControllerDeleteKeywordFromEditPanel.Error.NoEditPanel");
        }
    }

    private String getKeyword(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            Keyword keyword = (Keyword) userObject;

            if (keyword.isReal()) {
                return keyword.getName();
            }
        }

        return null;
    }
}
