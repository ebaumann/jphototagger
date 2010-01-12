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
package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemRemove()}
 * and on action removes from the tree the selected keyword.
 *
 * Also listens to key events into the tree and removes the selected keyword if
 * the delete key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRemoveKeyword
        extends    ControllerKeywords
        implements ActionListener,
                   KeyListener {

    public ControllerRemoveKeyword(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof Keyword) {
            delete(node, (Keyword) userObject);
        } else {
            MessageDisplayer.error(null, "ControllerRemoveKeyword.Error.Node", node);
        }
    }

    private void delete(
            DefaultMutableTreeNode node, Keyword keyword) {
        TreeModel tm = getHKPanel().getTree().getModel();
        if (tm instanceof TreeModelKeywords) {
            if (MessageDisplayer.confirmYesNo(null, "ControllerRemoveKeyword.Confirm.Remove", keyword)) {
                KeywordsHelper.deleteInFiles(keyword);
                ((TreeModelKeywords) tm).delete(node);
            }
        } else {
            AppLog.logWarning(ControllerRemoveKeyword.class, "ControllerRemoveKeyword.Error.Model");
        }
    }
}
