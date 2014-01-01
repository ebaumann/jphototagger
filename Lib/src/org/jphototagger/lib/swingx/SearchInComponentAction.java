package org.jphototagger.lib.swingx;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import org.jphototagger.lib.util.Bundle;

/**
 * Invokes the action "find" if the action map contains an action with that key.
 * @author Elmar Baumann
 */
public final class SearchInComponentAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final JComponent component;

    public SearchInComponentAction(JComponent component) {
        this(component, Bundle.getString(SearchInComponentAction.class, "SearchInComponentAction.Name"));
    }

    public SearchInComponentAction(JComponent component, String name) {
        super(name);
        if (component == null) {
            throw new NullPointerException("list == null");
        }

        this.component = component;
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        ActionMap actionMap = component.getActionMap();
        Action action = actionMap.get("find");

        if (action != null) {
            action.actionPerformed(new ActionEvent(component, 0, "find"));
        }
    }
}
