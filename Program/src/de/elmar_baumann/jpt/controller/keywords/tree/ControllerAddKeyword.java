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
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemAdd()}
 * and on action adds a new keyword below the selected keyword.
 *
 * Also listens to key events into the tree and adds a new keyword below the
 * selected keyword if the keys Ctrl+N were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerAddKeyword
        extends ControllerKeywords
        implements ActionListener, KeyListener {

    public ControllerAddKeyword(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_N);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof Keyword) {
            add(node, (Keyword) userObject);
        } else if (isRootNode(node)) {
            add(node, null);
        }
    }

    private boolean isRootNode(Object node) {
        return getHKPanel().getTree().getModel().getRoot().equals(node);
    }

    private void add(
            DefaultMutableTreeNode parentNode, Keyword parentKeyword) {
        Keyword newKeyword = new Keyword(
                     null,
                     parentKeyword == null
                         ? null
                         : parentKeyword.getId(),
                      Bundle.getString("ControllerAddKeyword.DefaultName"),
                      true);
        JTree tree = getHKPanel().getTree();
        String name = ControllerRenameKeyword.getName(
                newKeyword, DatabaseKeywords.INSTANCE, tree);
        if (name != null && !name.trim().isEmpty()) {
            TreeModel tm = tree.getModel();
            if (tm instanceof TreeModelKeywords) {
                ((TreeModelKeywords) tm).insert(parentNode, name, true);
                KeywordsTreePathExpander.expand(parentNode);
            } else {
                AppLog.logWarning(ControllerAddKeyword.class, "ControllerAddKeyword.Error.Model");
            }
        }
    }
}
