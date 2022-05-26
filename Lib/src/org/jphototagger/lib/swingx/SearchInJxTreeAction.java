package org.jphototagger.lib.swingx;

import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Performs the action "find" of a {@code JXLTree}'s action map.
 *
 * @author Elmar Baumann
 */
public final class SearchInJxTreeAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final JXTree tree;
    private final boolean expandAllBeforeSearch;

    /**
     * Creates an instance without expanding all nodes before performing a
     * search.
     *
     * @param tree
     */
    public SearchInJxTreeAction(JXTree tree) {
        this(tree, false);
    }

    public SearchInJxTreeAction(JXTree tree, boolean expandAllBeforeSearch) {
        super(Bundle.getString(SearchInJxTreeAction.class, "SearchInJxTreeAction.Name"));
        this.tree = Objects.requireNonNull(tree, "tree == null");
        this.expandAllBeforeSearch = expandAllBeforeSearch;
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        ActionMap actionMap = tree.getActionMap();
        Action action = actionMap.get("find");

        if (action != null) {
            if (expandAllBeforeSearch) {
                TreeUtil.expandAll(tree, true);
            }
            action.actionPerformed(new ActionEvent(tree, 0, "find"));
        }
    }
}
