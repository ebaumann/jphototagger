package org.jphototagger.lib.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.List;
import javax.swing.Action;
import javax.swing.JList;

import javax.swing.JPopupMenu;
import org.openide.util.lookup.InstanceContent;

import org.jphototagger.api.nodes.Node;

/**
 * @author Elmar Baumann
 */
final class LookupUtil {

    static Collection<?> createContentOfSelectedValues(JList list) {
        Object[] selectedValues = list.getSelectedValues();
        Collection<Object> selectedContent = new ArrayList<Object>(selectedValues.length);
        for (Object selectedValue : selectedValues) {
            if (selectedValue instanceof Node) {
                Node node = (Node) selectedValue;
                selectedContent.addAll(node.getContent());
            } else {
                selectedContent.add(selectedValue);
            }
        }
        return selectedContent;
    }

    static void clearInstanceContent(InstanceContent content) {
        content.set(Collections.emptyList(), null);
    }

    static JPopupMenu createPopupMenuFromNodeActions(Collection<?> selectedContent, boolean temporarySelected) {
        List<Action> actions = new ArrayList<Action>(selectedContent.size());
        for (Object selectedObject : selectedContent) {
            if (selectedObject instanceof Node) {
                Node node = (Node) selectedObject;
                actions.addAll(temporarySelected ? node.getActionsForTemporarySelections() : node.getActions());
            }
        }
        if (actions.isEmpty()) {
            return null;
        }
        JPopupMenu popupMenu = new JPopupMenu();
        for (Action action : actions) {
            popupMenu.add(action);
        }
        return popupMenu;
    }

    private LookupUtil() {
    }
}
