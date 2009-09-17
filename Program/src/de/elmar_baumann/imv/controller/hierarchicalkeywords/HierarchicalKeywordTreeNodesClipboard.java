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
package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-10
 */
public final class HierarchicalKeywordTreeNodesClipboard {

    public static final HierarchicalKeywordTreeNodesClipboard INSTANCE =
            new HierarchicalKeywordTreeNodesClipboard();
    private final List<DefaultMutableTreeNode> nodes =
            new ArrayList<DefaultMutableTreeNode>();

    public boolean hasContent() {
        return nodes.size() > 0;
    }

    public List<DefaultMutableTreeNode> getContent() {
        return nodes;
    }

    public void setContent(DefaultMutableTreeNode node) {
        nodes.clear();
        nodes.add(node);
    }

    public void setContent(List<DefaultMutableTreeNode> nodes) {
        nodes.clear();
        this.nodes.addAll(nodes);
    }

    public void empty() {
        nodes.clear();
    }

    private HierarchicalKeywordTreeNodesClipboard() {
    }
}
