package org.jphototagger.program.controller.keywords.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 *
 * @author Elmar Baumann
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
        return Collections.unmodifiableList(nodes);
    }

    public void setContent(DefaultMutableTreeNode node, Action action) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        nodes.clear();
        nodes.add(node);
        this.action = action;
    }

    public void setContent(List<DefaultMutableTreeNode> nodes, Action action) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

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
