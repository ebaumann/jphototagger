package org.jphototagger.program.app.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;

import org.jdesktop.swingx.JXList;

import org.jphototagger.lib.util.Bundle;

/**
 * Performs the action "find" of a {@code JXList}'s action map.
 *
 * @author Elmar Baumann
 */
public final class SearchInJxListAction extends AbstractAction {

    private static final long serialVersionUID = -1674416888249161901L;
    private final JXList list;

    public SearchInJxListAction(JXList list) {
        super(Bundle.getString(SearchInJxListAction.class, "SearchInJxListAction.Name"));

        if (list == null) {
            throw new NullPointerException("list == null");
        }

        this.list = list;
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        ActionMap actionMap = list.getActionMap();
        Action action = actionMap.get("find");

        if (action != null) {
            action.actionPerformed(new ActionEvent(list, 0, "find"));
        }
    }
}
