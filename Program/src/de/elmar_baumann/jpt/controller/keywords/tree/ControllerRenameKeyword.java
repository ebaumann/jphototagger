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

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import de.elmar_baumann.lib.dialog.InputDialog;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemRename()}
 * and on action renames in the tree the selected keyword.
 *
 * Also listens to key events into the tree and renames the selected
 * keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRenameKeyword
        extends    ControllerKeywords
        implements ActionListener,
                   KeyListener
    {
    private final DatabaseKeywords db = DatabaseKeywords.INSTANCE;

    public ControllerRenameKeyword(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return false;
    }

    @Override
    protected void localAction(List<DefaultMutableTreeNode> nodes) {
        DefaultMutableTreeNode node       = nodes.get(0);
        Object                 userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            renameKeyword(node, (Keyword) userObject);
        } else {
            MessageDisplayer.error(null, "ControllerRenameKeyword.Error.Node", node);
        }
    }

    private void renameKeyword(DefaultMutableTreeNode node, Keyword keyword) {
        String newName = getName(keyword, db, getHKPanel().getTree());
        if (newName != null && !newName.trim().isEmpty()) {
            TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

            keyword.setName(newName);
            model.changed(node, keyword);
        }
    }

    static String getName(Keyword keyword, DatabaseKeywords database, JTree tree) {
        String      newName = null;
        String      oldName = keyword.getName();
        boolean     input   = true;
        InputDialog dlg     = new InputDialog(InputHelperDialog.INSTANCE,
                                      JptBundle.INSTANCE.getString("ControllerRenameKeyword.Input.Name", oldName),
                                      oldName,
                                      UserSettings.INSTANCE.getProperties(),
                                      ControllerRenameKeyword.class.getName());

        while (input && newName == null) {
            dlg.setVisible(true);
            newName = dlg.getInput();
            input = false;

            if (dlg.isAccepted() && newName != null && !newName.trim().isEmpty()) {
                Keyword s = new Keyword(keyword.getId(), keyword.getIdParent(), newName.trim(), keyword.isReal());
                if (database.hasParentChildWithEqualName(s)) {
                    newName = null;
                    input = MessageDisplayer.confirmYesNo(null, "ControllerRenameKeyword.Confirm.Exists", s);
                }
            }
        }
        return newName;
    }
}
