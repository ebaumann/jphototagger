package org.jphototagger.lib.lookup;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JList;

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

    private LookupUtil() {
    }
}
