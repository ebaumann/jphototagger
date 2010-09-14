/*
 * @(#)ControllerDeleteKeywords.java    Created on 2009-07-12
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
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemRemove()}
 * and on action removes from the tree the selected keyword.
 *
 * Also listens to key events into the tree and removes the selected keyword if
 * the delete key was pressed.
 *
 * @author  Elmar Baumann
 */
public class ControllerDeleteKeywords extends ControllerKeywords
        implements ActionListener, KeyListener {
    public ControllerDeleteKeywords(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return true;
    }

    @Override
    protected void localAction(final List<DefaultMutableTreeNode> nodes) {
        if (!ensureNoChild(nodes) ||!confirmDeleteMultiple(nodes)) {
            return;
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteKeywords(nodes);
            }
        });
    }

    private void deleteKeywords(List<DefaultMutableTreeNode> nodes) {
                for (DefaultMutableTreeNode node : nodes) {
                    Object userObject = node.getUserObject();

                    if (userObject instanceof Keyword) {
                        delete(node, (Keyword) userObject, nodes.size() == 1);
                    } else {
                        MessageDisplayer.error(
                    null, "ControllerDeleteKeywords.Tree.Error.Node", node);
                    }
                }
            }

    private void delete(DefaultMutableTreeNode node, Keyword keyword,
                        boolean confirm) {
        if (!confirm
                || (confirm
                    && MessageDisplayer.confirmYesNo(
                        InputHelperDialog.INSTANCE,
                        "ControllerDeleteKeywords.Tree.Confirm.Delete",
                        keyword))) {
            ModelFactory.INSTANCE.getModel(TreeModelKeywords.class).delete(
                node);
        }
    }

    private boolean confirmDeleteMultiple(List<DefaultMutableTreeNode> nodes) {
        int size = nodes.size();

        if (size <= 1) {
            return true;
        }

        return MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE,
                "ControllerDeleteKeywords.Tree.Confirm.MultipleKeywords", size);
    }
}
