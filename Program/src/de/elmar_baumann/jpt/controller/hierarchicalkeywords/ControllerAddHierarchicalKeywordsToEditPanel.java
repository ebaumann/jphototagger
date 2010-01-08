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
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Listens to the menu item
 * {@link PopupMenuHierarchicalKeywords#getMenuItemAddToEditPanel()}
 * and on action inserts the selected hierarchical keyword and it's real parents
 * into the edit panel.
 *
 * Also listens to key events and does the same if Ctrl+B were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-15
 */
public class ControllerAddHierarchicalKeywordsToEditPanel
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerAddHierarchicalKeywordsToEditPanel(
            KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_B);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        List<String> keywordNames = new ArrayList<String>();
        addParentKeywords(node, keywordNames);
        addToEditPanel(keywordNames);
    }

    private void addToEditPanel(List<String> keywordNames) {
        EditMetadataPanelsArray editPanels =
                GUI.INSTANCE.getAppPanel().getEditMetadataPanelsArray();
        JPanel panel = editPanels.getEditPanel(
                ColumnXmpDcSubjectsSubject.INSTANCE);
        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel =
                    (EditRepeatableTextEntryPanel) panel;
            if (editPanel.isEditable()) {
                for (String keywordName : keywordNames) {
                    editPanel.addText(keywordName);
                }
                HierarchicalKeywordsHelper.addHighlightKeywords(keywordNames);
                editPanels.checkSaveOnChanges();
            } else {
                MessageDisplayer.error(
                        null,
                        "ControllerAddHierarchicalKeywordsToEditPanel.Error.EditDisabled");
            }
        } else {
            MessageDisplayer.error(
                    null,
                    "ControllerAddHierarchicalKeywordsToEditPanel.Error.NoEditPanel");
        }
    }

    private void addParentKeywords(
            DefaultMutableTreeNode node, List<String> keywords) {

        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            if (keyword.isReal()) {
                keywords.add(keyword.getKeyword());
            }
        }
        TreeNode parent = node.getParent();
        if (parent == null ||
                getHKPanel().getTree().getModel().getRoot().equals(parent)) {
            return;
        }
        assert parent instanceof DefaultMutableTreeNode :
                "Not a DefaultMutableTreeNode: " + parent;
        if (parent instanceof DefaultMutableTreeNode) {
            addParentKeywords((DefaultMutableTreeNode) parent, keywords);
        }
    }
}
