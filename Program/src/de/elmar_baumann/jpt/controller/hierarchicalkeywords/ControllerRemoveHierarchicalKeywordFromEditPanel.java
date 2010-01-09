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
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item
 * {@link PopupMenuHierarchicalKeywords#getMenuItemRemoveFromEditPanel()}
 * and on action removes the selected hierarchical keyword from the edit
 * panel.  If a keyword was already present it is removed again.
 *
 * Also listens to key events and does the same if Ctrl+D was pressed.
 *
 * @author  Martin Pohlack  <martinp@gmx.de>
 * @version 2009-07-26
 */
public class ControllerRemoveHierarchicalKeywordFromEditPanel
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerRemoveHierarchicalKeywordFromEditPanel(
            KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        String keyword = getKeyword(node);
        if (keyword != null) {
            removeFromEditPanel(keyword);
        }
    }

    private void removeFromEditPanel(String keyword) {
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getEditMetadataPanelsArray();
        JPanel panel = editPanels.getEditPanel(
                ColumnXmpDcSubjectsSubject.INSTANCE);
        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel =
                    (EditRepeatableTextEntryPanel) panel;
            if (editPanel.isEditable()) {
                editPanel.removeText(keyword);
                editPanels.checkSaveOnChanges();
                HierarchicalKeywordsHelper.removeHighlightKeyword(keyword);
            } else {
                MessageDisplayer.error(
                        null,
                        "ControllerRemoveHierarchicalKeywordFromEditPanel.Error.EditDisabled");
            }
        } else {
            MessageDisplayer.error(
                    null,
                    "ControllerRemoveHierarchicalKeywordFromEditPanel.Error.NoEditPanel");
        }
    }

    private String getKeyword(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            if (keyword.isReal()) {
                return keyword.getName();
            }
        }
        return null;
    }
}
