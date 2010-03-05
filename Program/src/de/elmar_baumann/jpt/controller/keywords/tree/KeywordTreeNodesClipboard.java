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

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2009-09-10
 */
public final class KeywordTreeNodesClipboard {
    public enum Action { COPY, MOVE, UNKNOWN }

    public static final KeywordTreeNodesClipboard INSTANCE =
        new KeywordTreeNodesClipboard();
    private Action                             action = Action.UNKNOWN;
    private final List<DefaultMutableTreeNode> nodes  =
        new ArrayList<DefaultMutableTreeNode>();

    public boolean isEmpty() {
        return nodes.size() <= 0;
    }

    public List<DefaultMutableTreeNode> getContent() {
        return nodes;
    }

    public void setContent(DefaultMutableTreeNode node, Action action) {
        nodes.clear();
        nodes.add(node);
        this.action = action;
    }

    public void setContent(List<DefaultMutableTreeNode> nodes, Action action) {
        nodes.clear();
        this.nodes.addAll(nodes);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void empty() {
        nodes.clear();
        action = Action.UNKNOWN;
    }

    public boolean isMove() {
        return action.equals(Action.MOVE);
    }

    public boolean isCopy() {
        return action.equals(Action.COPY);
    }

    private KeywordTreeNodesClipboard() {}
}
