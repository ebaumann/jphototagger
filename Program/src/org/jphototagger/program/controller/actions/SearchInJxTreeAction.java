package org.jphototagger.program.controller.actions;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.program.resource.JptBundle;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;

/**
 * Performs the action "find" of a {@link JXList}'s action map.
 *
 * @author Elmar Baumann
 */
public final class SearchInJxTreeAction extends AbstractAction {
    private static final long serialVersionUID = 5922964998600398364L;
    private final JXTree tree;

    public SearchInJxTreeAction(JXTree tree) {
        super(JptBundle.INSTANCE.getString("SearchInJxTreeAction.Name"));

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
