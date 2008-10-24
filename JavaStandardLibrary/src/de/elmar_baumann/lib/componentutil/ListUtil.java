package de.elmar_baumann.lib.componentutil;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * Utils for {@link javax.swing.JList}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/19
 */
public class ListUtil {

    /**
     * Clears alls selected items in all lists.
     * 
     * @param lists  lists
     */
    public static void clearSelection(List<JList> lists) {
        for (JList list : lists) {
            if (!list.isSelectionEmpty()) {
                list.clearSelection();
            }
        }
    }

    /**
     * Returns the first item of a list model with a specific text.
     * 
     * @param  text  searched item text
     * @param  model list model
     * @return list item or null if not found
     */
    public static Object getFirstItemWithText(String text, DefaultListModel model) {
        Object item = null;
        int size = model.size();
        for (int i = 0; i < size; i++) {
            Object itemOfModel = model.get(i);
            if (text.equals(itemOfModel.toString())) {
                return itemOfModel;
            }
        }
        return item;
    }
}
