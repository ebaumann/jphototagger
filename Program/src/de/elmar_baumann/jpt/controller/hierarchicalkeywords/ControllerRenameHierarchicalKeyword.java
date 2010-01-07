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
import de.elmar_baumann.jpt.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuHierarchicalKeywords#getMenuItemRename()}
 * and on action renames in the tree the selected hierarchical keyword.
 *
 * Also listens to key events into the tree and renames the selected
 * hierarchical keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRenameHierarchicalKeyword
        extends    ControllerHierarchicalKeywords
        implements ActionListener,
                   KeyListener {

    private final DatabaseHierarchicalKeywords db = DatabaseHierarchicalKeywords.INSTANCE;

    public ControllerRenameHierarchicalKeyword(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            renameKeyword(node, (HierarchicalKeyword) userObject);
        } else {
            MessageDisplayer.error(
                    null,
                    "ControllerRenameHierarchicalKeyword.Error.Node",
                    node);
        }
    }

    private void renameKeyword(DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = getHKPanel().getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            String newName = getName(keyword, db, getHKPanel().getTree());
            if (newName != null && !newName.trim().isEmpty()) {
                String oldName = keyword.getKeyword();
                keyword.setKeyword(newName);
                HierarchicalKeywordsHelper.renameInFiles(oldName, keyword);
                ((TreeModelHierarchicalKeywords) tm).changed(node, keyword);
            }
        } else {
            AppLog.logWarning(ControllerRenameHierarchicalKeyword.class,
                    "ControllerRenameHierarchicalKeyword.Error.Model");
        }
    }

    static String getName(
            HierarchicalKeyword keyword,
            DatabaseHierarchicalKeywords database,
            JTree tree) {

        String newName = null;
        String oldName = keyword.getKeyword();
        boolean confirmed = true;
        while (newName == null && confirmed) {
            newName = MessageDisplayer.input(
                    "ControllerRenameHierarchicalKeyword.Input.Name",
                    oldName,
                    ControllerRenameHierarchicalKeyword.class.getName(),
                    oldName);
            confirmed = newName != null;
            if (newName != null && !newName.trim().isEmpty()) {
                HierarchicalKeyword s = new HierarchicalKeyword(keyword.getId(),
                        keyword.getIdParent(), newName.trim(), keyword.isReal());
                if (database.parentHasChild(s)) {
                    newName = null;
                    confirmed = MessageDisplayer.confirmYesNo(
                            null,
                            "ControllerRenameHierarchicalKeyword.Confirm.Exists",
                            s);
                }
            }
        }
        return newName;
    }
}
