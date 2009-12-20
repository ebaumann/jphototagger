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

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuHierarchicalKeywords#getMenuItemRemove()}
 * and on action removes from the tree the selected hierarchical keyword.
 *
 * Also listens to key events into the tree and removes the selected
 * hierarchical keyword if the delete key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRemoveHierarchicalKeyword
        extends    ControllerHierarchicalKeywords
        implements ActionListener,
                   KeyListener {

    public ControllerRemoveHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            delete(node, (HierarchicalKeyword) userObject);
        } else {
            MessageDisplayer.error(getHKPanel().getTree(),
                    "ControllerDeleteHierarchicalKeyword.Error.Node", // NOI18N
                    node);
        }
    }

    private void delete(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = getHKPanel().getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            if (MessageDisplayer.confirm(
                    getHKPanel(),
                    "ControllerDeleteHierarchicalKeyword.Confirm.Delete", // NOI18N
                    MessageDisplayer.CancelButton.HIDE, keyword).equals(
                    MessageDisplayer.ConfirmAction.YES)) {
                HierarchicalKeywordsHelper.deleteInFiles(keyword);
                ((TreeModelHierarchicalKeywords) tm).removeKeyword(node);
            }
        } else {
            AppLog.logWarning(ControllerRemoveHierarchicalKeyword.class,
                    "ControllerDeleteHierarchicalKeyword.Error.Model"); // NOI18N
        }
    }
}
