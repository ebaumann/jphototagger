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
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuHierarchicalKeywords#getItemAdd()}
 * and on action adds a new keyword below the selected keyword.
 *
 * Also listens to key events into the tree and adds a new keyword below the
 * selected keyword if the keys Ctrl+N were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerAddHierarchicalKeyword
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerAddHierarchicalKeyword(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_N);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            add(node, (HierarchicalKeyword) userObject);
        } else if (isRootNode(node)) {
            add(node, null);
        }
    }

    private boolean isRootNode(Object node) {
        return getHKPanel().getTree().getModel().getRoot().equals(node);
    }

    private void add(
            DefaultMutableTreeNode parentNode, HierarchicalKeyword parentKeyword) {
        HierarchicalKeyword newKeyword =
                new HierarchicalKeyword(
                null, parentKeyword == null
                      ? null
                      : parentKeyword.getId(),
                Bundle.getString("ControllerAddHierarchicalKeyword.DefaultName"),
                true);
        JTree tree = getHKPanel().getTree();
        String name = ControllerRenameHierarchicalKeyword.getName(
                newKeyword, DatabaseHierarchicalKeywords.INSTANCE, tree);
        if (name != null && !name.trim().isEmpty()) {
            TreeModel tm = tree.getModel();
            if (tm instanceof TreeModelHierarchicalKeywords) {
                ((TreeModelHierarchicalKeywords) tm).addKeyword(parentNode, name, true);
                HierarchicalKeywordsTreePathExpander.expand(parentNode);
            } else {
                AppLog.logWarning(ControllerAddHierarchicalKeyword.class,
                        "ControllerAddHierarchicalKeyword.Error.Model");
            }
        }
    }
}
