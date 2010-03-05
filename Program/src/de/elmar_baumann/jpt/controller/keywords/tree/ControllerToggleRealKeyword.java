/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsTree;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemToggleReal()}
 * and toggles the real property of a keyword.
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-13
 */
public class ControllerToggleRealKeyword
        extends    ControllerKeywords
        implements ActionListener,
                   KeyListener
    {

    public ControllerToggleRealKeyword(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_R);
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
            Keyword           keyword = (Keyword) userObject;
            TreeModelKeywords model   = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

            keyword.setReal(!keyword.isReal());
            model.changed(node, keyword);
        } else {
            MessageDisplayer.error(null, "ControllerToggleRealKeyword.Error.Node", node);
        }
    }
}
