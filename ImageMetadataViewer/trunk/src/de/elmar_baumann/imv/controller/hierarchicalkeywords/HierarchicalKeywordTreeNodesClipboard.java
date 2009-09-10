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
