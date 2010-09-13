/*
 * @(#)ControllerRenameKeyword.java    Created on 2009-07-12
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

import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemRename()}
 * and on action renames in the tree the selected keyword.
 *
 * Also listens to key events into the tree and renames the selected
 * keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author  Elmar Baumann
 */
public class ControllerRenameKeyword extends ControllerKeywords
        implements ActionListener, KeyListener {
    private final DatabaseKeywords db = DatabaseKeywords.INSTANCE;

    public ControllerRenameKeyword(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
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

        DefaultMutableTreeNode node       = nodes.get(0);
        Object                 userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            renameKeyword(node, (Keyword) userObject);
        } else {
            MessageDisplayer.error(null, "ControllerRenameKeyword.Error.Node",
                                   node);
        }
    }

    private void renameKeyword(final DefaultMutableTreeNode node,
                               final Keyword keyword) {
        final String newName = getName(keyword, db, getHKPanel().getTree());

        if ((newName != null) &&!newName.trim().isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TreeModelKeywords model =
                        ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

                    keyword.setName(newName);
                    model.changed(node, keyword);
                }
            });
        }
    }

    static String getName(Keyword keyword, DatabaseKeywords database,
                          JTree tree) {
        String      toName   = null;
        String      fromName = keyword.getName();
        boolean     input    = true;
        InputDialog dlg = new InputDialog(
                              InputHelperDialog.INSTANCE,
                              JptBundle.INSTANCE.getString(
                                  "ControllerRenameKeyword.Input.Name",
                                  fromName), fromName,
                                      UserSettings.INSTANCE.getProperties(),
                                      ControllerRenameKeyword.class.getName());

        while (input && (toName == null)) {
            dlg.setVisible(true);
            toName = dlg.getInput();
            input  = false;

            if (dlg.isAccepted() && (toName != null)
                    &&!toName.trim().isEmpty()) {
                Keyword s = new Keyword(keyword.getId(), keyword.getIdParent(),
                                        toName.trim(), keyword.isReal());

                if (database.hasParentChildWithEqualName(s)) {
                    toName = null;
                    input = MessageDisplayer.confirmYesNo(null,
                            "ControllerRenameKeyword.Confirm.Exists", s);
                }
            }
        }

        return toName;
    }
}
