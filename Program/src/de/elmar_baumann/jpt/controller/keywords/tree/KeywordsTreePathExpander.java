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

import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Expands the path of all {@link KeywordsPanel}s trees.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-31
 */
public final class KeywordsTreePathExpander {

    public static void expand(JTree tree, DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        tree.expandPath(path);
    }

    private KeywordsTreePathExpander() {
    }
}
