package org.jphototagger.lib.swingx;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.lib.util.Bundle;

/**
 * Performs the action "find" of a {@code JXLTree}'s action map.
 *
 * @author Elmar Baumann
 */
public final class SearchInJxTreeAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final JXTree tree;

    public SearchInJxTreeAction(JXTree tree) {
        super(Bundle.getString(SearchInJxTreeAction.class, "SearchInJxTreeAction.Name"));

        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        ActionMap actionMap = tree.getActionMap();
        Action action = actionMap.get("find");

        if (action != null) {
            action.actionPerformed(new ActionEvent(tree, 0, "find"));
        }
    }
}
